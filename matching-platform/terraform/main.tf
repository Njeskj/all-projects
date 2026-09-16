terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.32"
    }
  }
}

provider "kubernetes" {
  config_path    = "~/.kube/config"
  config_context = "kind-platform"
}

variable "namespace" {
  default = "matching"
}

variable "image_tag" {
  default = "3a4b75d"
}

resource "kubernetes_config_map" "matching_config" {
  metadata {
    name      = "matching-config"
    namespace = var.namespace
  }
  data = {
    REDIS_ADDR                     = "matching-redis:6379"
    KAFKA_ADDR                     = "matching-kafka:9092"
    HTTP_PORT                      = "8091"
    GRPC_PORT                      = "9095"
    SPRING_DATASOURCE_URL          = "jdbc:postgresql://matching-postgres:5432/matching"
    SPRING_KAFKA_BOOTSTRAP_SERVERS = "matching-kafka:9092"
    MATCHING_ENGINE_HOST           = "matching-engine"
    MATCHING_ENGINE_GRPC_PORT      = "9095"
  }
}

resource "kubernetes_secret" "matching_secrets" {
  metadata {
    name      = "matching-secrets"
    namespace = var.namespace
  }
  data = {
    SPRING_DATASOURCE_USERNAME = "postgres"
    SPRING_DATASOURCE_PASSWORD = "postgres"
  }
  type = "Opaque"
}

resource "kubernetes_deployment" "matching_engine" {
  metadata {
    name      = "matching-engine"
    namespace = var.namespace
  }
  spec {
    replicas = 1
    selector {
      match_labels = { app = "matching-engine" }
    }
    template {
      metadata {
        labels = { app = "matching-engine" }
      }
      spec {
        container {
          name  = "matching-engine"
          image = "localhost:5001/matching-engine:${var.image_tag}"
          port {
            container_port = 8091
          }
          port {
            container_port = 9095
          }
          env_from {
            config_map_ref {
              name = kubernetes_config_map.matching_config.metadata[0].name
            }
          }
          readiness_probe {
            http_get {
              path = "/healthz"
              port = 8091
            }
            initial_delay_seconds = 3
            period_seconds        = 5
          }
          liveness_probe {
            http_get {
              path = "/healthz"
              port = 8091
            }
            initial_delay_seconds = 5
            period_seconds        = 10
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "matching_engine" {
  metadata {
    name      = "matching-engine"
    namespace = var.namespace
  }
  spec {
    selector = { app = "matching-engine" }
    port {
      name        = "http"
      port        = 8091
      target_port = 8091
    }
    port {
      name        = "grpc"
      port        = 9095
      target_port = 9095
    }
  }
}

resource "kubernetes_deployment" "user_service" {
  metadata {
    name      = "user-service"
    namespace = var.namespace
  }
  spec {
    replicas = 1
    selector {
      match_labels = { app = "user-service" }
    }
    template {
      metadata {
        labels = { app = "user-service" }
      }
      spec {
        container {
          name  = "user-service"
          image = "localhost:5001/user-service:${var.image_tag}"
          port {
            container_port = 8092
          }
          env_from {
            config_map_ref {
              name = kubernetes_config_map.matching_config.metadata[0].name
            }
          }
          env_from {
            secret_ref {
              name = kubernetes_secret.matching_secrets.metadata[0].name
            }
          }
          readiness_probe {
            http_get {
              path = "/actuator/health"
              port = 8092
            }
            initial_delay_seconds = 30
            period_seconds        = 5
            failure_threshold     = 10
          }
          liveness_probe {
            http_get {
              path = "/actuator/health"
              port = 8092
            }
            initial_delay_seconds = 60
            period_seconds        = 10
            failure_threshold     = 6
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "user_service" {
  metadata {
    name      = "user-service"
    namespace = var.namespace
  }
  spec {
    selector = { app = "user-service" }
    port {
      name        = "http"
      port        = 8092
      target_port = 8092
    }
  }
}
