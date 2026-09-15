resource "kubernetes_config_map" "inventory_service_config" {
  metadata {
    name      = "inventory-service-config"
    namespace = var.namespace
  }
  data = {
    REDIS_ADDR     = "redis.${var.namespace}.svc.cluster.local:6379"
    KAFKA_BROKERS  = "kafka.${var.namespace}.svc.cluster.local:9092"
  }
}

resource "kubernetes_secret" "inventory_service_secret" {
  metadata {
    name      = "inventory-service-secret"
    namespace = var.namespace
  }
  # ponytail: plaintext dev creds for a local kind cluster; replace with a
  # real secret manager / sealed-secrets before any shared environment.
  data = {
    DATABASE_URL = "postgres://postgres:postgres@postgres.${var.namespace}.svc.cluster.local:5432/inventory?sslmode=disable"
  }
}

resource "kubernetes_deployment" "inventory_service" {
  metadata {
    name      = "inventory-service"
    namespace = var.namespace
    labels    = { app = "inventory-service" }
  }
  spec {
    replicas = 1
    selector {
      match_labels = { app = "inventory-service" }
    }
    template {
      metadata {
        labels = { app = "inventory-service" }
      }
      spec {
        container {
          name  = "inventory-service"
          image = "localhost:5001/inventory-service:${var.inventory_image_tag}"
          port {
            container_port = 8080
            name           = "http"
          }
          port {
            container_port = 9090
            name           = "grpc"
          }
          env_from {
            config_map_ref {
              name = kubernetes_config_map.inventory_service_config.metadata[0].name
            }
          }
          env_from {
            secret_ref {
              name = kubernetes_secret.inventory_service_secret.metadata[0].name
            }
          }
          readiness_probe {
            http_get {
              path = "/healthz"
              port = 8080
            }
            initial_delay_seconds = 3
            period_seconds         = 5
          }
          liveness_probe {
            http_get {
              path = "/healthz"
              port = 8080
            }
            initial_delay_seconds = 5
            period_seconds         = 10
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "inventory_service" {
  metadata {
    name      = "inventory-service"
    namespace = var.namespace
  }
  spec {
    selector = { app = "inventory-service" }
    port {
      name = "http"
      port = 8080
    }
    port {
      name = "grpc"
      port = 9090
    }
  }
}
