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

# ponytail: this Terraform manages only resources INSIDE the pre-existing `ecommerce` namespace,
# and only the ConfigMap/Secret. The Deployments/Services are owned by k8s/ + the ArgoCD
# Application (argocd-application.yaml) so there's a single writer per resource — Terraform and
# ArgoCD fighting over the same Deployment causes endless OutOfSync/replace churn.

locals {
  namespace = "ecommerce"
}

resource "kubernetes_config_map" "ecommerce_config" {
  metadata {
    name      = "ecommerce-config"
    namespace = local.namespace
  }
  data = {
    DATABASE_URL               = "postgres://ecommerce:ecommerce@postgres:5432/ecommerce?sslmode=disable"
    REDIS_ADDR                 = "redis:6379"
    KAFKA_BROKERS               = "kafka:9092"
    SPRING_DATASOURCE_URL       = "jdbc:postgresql://postgres:5432/ecommerce"
    SPRING_DATASOURCE_USERNAME  = "ecommerce"
    INVENTORY_GRPC_HOST         = "inventory-service"
    INVENTORY_GRPC_PORT         = "9090"
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
