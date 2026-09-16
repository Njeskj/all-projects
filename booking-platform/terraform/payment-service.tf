resource "kubernetes_config_map" "payment_service_config" {
  metadata {
    name      = "payment-service-config"
    namespace = var.namespace
  }
  data = {
    SPRING_KAFKA_BOOTSTRAP_SERVERS = "kafka.${var.namespace}.svc.cluster.local:9092"
    INVENTORY_GRPC_HOST            = "inventory-service.${var.namespace}.svc.cluster.local"
    INVENTORY_GRPC_PORT            = "9090"
  }
}

resource "kubernetes_secret" "payment_service_secret" {
  metadata {
    name      = "payment-service-secret"
    namespace = var.namespace
  }
  # ponytail: plaintext dev creds for a local kind cluster; replace with a
  # real secret manager / sealed-secrets before any shared environment.
  data = {
    SPRING_DATASOURCE_URL      = "jdbc:postgresql://postgres.${var.namespace}.svc.cluster.local:5432/payment"
    SPRING_DATASOURCE_USERNAME = "postgres"
    SPRING_DATASOURCE_PASSWORD = "postgres"
  }
}

resource "kubernetes_deployment" "payment_service" {
  metadata {
    name      = "payment-service"
    namespace = var.namespace
    labels    = { app = "payment-service" }
  }
  spec {
    replicas = 1
    selector {
      match_labels = { app = "payment-service" }
    }
    template {
      metadata {
        labels = { app = "payment-service" }
      }
      spec {
        container {
          name  = "payment-service"
          image = "localhost:5001/payment-service:${var.payment_image_tag}"
          port {
            container_port = 18081
            name           = "http"
          }
          env_from {
            config_map_ref {
              name = kubernetes_config_map.payment_service_config.metadata[0].name
            }
          }
          env_from {
            secret_ref {
              name = kubernetes_secret.payment_service_secret.metadata[0].name
            }
          }
          readiness_probe {
            http_get {
              path = "/actuator/health"
              port = 18081
            }
            initial_delay_seconds = 20
            period_seconds         = 10
          }
          liveness_probe {
            http_get {
              path = "/actuator/health"
              port = 18081
            }
            initial_delay_seconds = 30
            period_seconds         = 15
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "payment_service" {
  metadata {
    name      = "payment-service"
    namespace = var.namespace
  }
  spec {
    selector = { app = "payment-service" }
    port {
      name = "http"
      port = 18081
    }
  }
}
