terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.31"
    }
  }
}

provider "kubernetes" {
  config_path    = "~/.kube/config"
  config_context = "kind-platform"
}

# ponytail: this Terraform manages only resources INSIDE the pre-existing `ecommerce` namespace.
# It does not create the namespace, the kind cluster, the registry, or touch ArgoCD's own install.

locals {
  namespace = "ecommerce"
  image_tag = "9020270" # git short-sha
}

resource "kubernetes_config_map" "ecommerce_config" {
  metadata {
    name      = "ecommerce-config"
    namespace = local.namespace
  }
  data = {
    DATABASE_URL                = "postgres://ecommerce:ecommerce@postgres:5432/ecommerce?sslmode=disable"
    REDIS_ADDR                  = "redis:6379"
    KAFKA_BROKERS                = "kafka:9092"
    SPRING_DATASOURCE_URL        = "jdbc:postgresql://postgres:5432/ecommerce"
    SPRING_DATASOURCE_USERNAME   = "ecommerce"
    INVENTORY_GRPC_HOST          = "inventory-service"
    INVENTORY_GRPC_PORT          = "9090"
  }
}

resource "kubernetes_secret" "ecommerce_secrets" {
  metadata {
    name      = "ecommerce-secrets"
    namespace = local.namespace
  }
  data = {
    POSTGRES_PASSWORD          = "ecommerce"
    SPRING_DATASOURCE_PASSWORD = "ecommerce"
  }
  type = "Opaque"
}

resource "kubernetes_deployment" "postgres" {
  metadata {
    name      = "postgres"
    namespace = local.namespace
    labels    = { app = "postgres" }
  }
  spec {
    replicas = 1
    selector { match_labels = { app = "postgres" } }
    template {
      metadata { labels = { app = "postgres" } }
      spec {
        container {
          name  = "postgres"
          image = "postgres:16-alpine"
          port { container_port = 5432 }
          env {
            name  = "POSTGRES_USER"
            value = "ecommerce"
          }
          env {
            name  = "POSTGRES_DB"
            value = "ecommerce"
          }
          env {
            name = "POSTGRES_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.ecommerce_secrets.metadata[0].name
                key  = "POSTGRES_PASSWORD"
              }
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "postgres" {
  metadata {
    name      = "postgres"
    namespace = local.namespace
  }
  spec {
    selector = { app = "postgres" }
    port {
      port        = 5432
      target_port = 5432
    }
  }
}

resource "kubernetes_deployment" "redis" {
  metadata {
    name      = "redis"
    namespace = local.namespace
    labels    = { app = "redis" }
  }
  spec {
    replicas = 1
    selector { match_labels = { app = "redis" } }
    template {
      metadata { labels = { app = "redis" } }
      spec {
        container {
          name  = "redis"
          image = "redis:7-alpine"
          port { container_port = 6379 }
        }
      }
    }
  }
}

resource "kubernetes_service" "redis" {
  metadata {
    name      = "redis"
    namespace = local.namespace
  }
  spec {
    selector = { app = "redis" }
    port {
      port        = 6379
      target_port = 6379
    }
  }
}

resource "kubernetes_deployment" "inventory_service" {
  metadata {
    name      = "inventory-service"
    namespace = local.namespace
    labels    = { app = "inventory-service" }
  }
  spec {
    replicas = 1
    selector { match_labels = { app = "inventory-service" } }
    template {
      metadata { labels = { app = "inventory-service" } }
      spec {
        container {
          name  = "inventory-service"
          image = "localhost:5001/inventory-service:${local.image_tag}"
          port { container_port = 8081 }
          port { container_port = 9090 }
          env_from {
            config_map_ref {
              name = kubernetes_config_map.ecommerce_config.metadata[0].name
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "inventory_service" {
  metadata {
    name      = "inventory-service"
    namespace = local.namespace
  }
  spec {
    selector = { app = "inventory-service" }
    port {
      name        = "http"
      port        = 8081
      target_port = 8081
    }
    port {
      name        = "grpc"
      port        = 9090
      target_port = 9090
    }
  }
}

resource "kubernetes_deployment" "order_service" {
  metadata {
    name      = "order-service"
    namespace = local.namespace
    labels    = { app = "order-service" }
  }
  spec {
    replicas = 1
    selector { match_labels = { app = "order-service" } }
    template {
      metadata { labels = { app = "order-service" } }
      spec {
        container {
          name  = "order-service"
          image = "localhost:5001/order-service:${local.image_tag}"
          port { container_port = 8080 }
          env_from {
            config_map_ref {
              name = kubernetes_config_map.ecommerce_config.metadata[0].name
            }
          }
          env {
            name = "SPRING_DATASOURCE_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.ecommerce_secrets.metadata[0].name
                key  = "SPRING_DATASOURCE_PASSWORD"
              }
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "order_service" {
  metadata {
    name      = "order-service"
    namespace = local.namespace
  }
  spec {
    selector = { app = "order-service" }
    port {
      port        = 8080
      target_port = 8080
    }
  }
}
