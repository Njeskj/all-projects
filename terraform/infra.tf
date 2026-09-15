# ponytail: single-replica Pods (not StatefulSets/PVCs) for dev infra inside
# the booking namespace, matching k8s/infra.yaml. Add PVCs if data must
# survive pod restarts.

resource "kubernetes_config_map" "postgres_init" {
  metadata {
    name      = "postgres-init"
    namespace = var.namespace
  }
  data = {
    "01-create-inventory-db.sql" = "CREATE DATABASE inventory;"
  }
}

resource "kubernetes_pod" "postgres" {
  metadata {
    name      = "postgres"
    namespace = var.namespace
    labels    = { app = "postgres" }
  }
  spec {
    container {
      name  = "postgres"
      image = "postgres:16-alpine"
      env {
        name  = "POSTGRES_PASSWORD"
        value = "postgres"
      }
      env {
        name  = "POSTGRES_DB"
        value = "payment"
      }
      port {
        container_port = 5432
      }
      volume_mount {
        name       = "init"
        mount_path = "/docker-entrypoint-initdb.d"
      }
    }
    volume {
      name = "init"
      config_map {
        name = kubernetes_config_map.postgres_init.metadata[0].name
      }
    }
  }
}

resource "kubernetes_service" "postgres" {
  metadata {
    name      = "postgres"
    namespace = var.namespace
  }
  spec {
    selector = { app = "postgres" }
    port {
      port = 5432
    }
  }
}

resource "kubernetes_pod" "redis" {
  metadata {
    name      = "redis"
    namespace = var.namespace
    labels    = { app = "redis" }
  }
  spec {
    container {
      name  = "redis"
      image = "redis:7-alpine"
      port {
        container_port = 6379
      }
    }
  }
}

resource "kubernetes_service" "redis" {
  metadata {
    name      = "redis"
    namespace = var.namespace
  }
  spec {
    selector = { app = "redis" }
    port {
      port = 6379
    }
  }
}

resource "kubernetes_pod" "kafka" {
  metadata {
    name      = "kafka"
    namespace = var.namespace
    labels    = { app = "kafka" }
  }
  spec {
    container {
      name  = "kafka"
      image = "apache/kafka:3.8.0"
      env {
        name  = "KAFKA_NODE_ID"
        value = "1"
      }
      env {
        name  = "KAFKA_PROCESS_ROLES"
        value = "broker,controller"
      }
      env {
        name  = "KAFKA_LISTENERS"
        value = "PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093"
      }
      env {
        name  = "KAFKA_ADVERTISED_LISTENERS"
        value = "PLAINTEXT://kafka.${var.namespace}.svc.cluster.local:9092"
      }
      env {
        name  = "KAFKA_LISTENER_SECURITY_PROTOCOL_MAP"
        value = "CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT"
      }
      env {
        name  = "KAFKA_CONTROLLER_QUORUM_VOTERS"
        value = "1@localhost:9093"
      }
      env {
        name  = "KAFKA_CONTROLLER_LISTENER_NAMES"
        value = "CONTROLLER"
      }
      env {
        name  = "KAFKA_INTER_BROKER_LISTENER_NAME"
        value = "PLAINTEXT"
      }
      env {
        name  = "CLUSTER_ID"
        value = "MkU3OEVBNTcwNTJENDM2Qk"
      }
      env {
        name  = "KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR"
        value = "1"
      }
      port {
        container_port = 9092
      }
    }
  }
}

resource "kubernetes_service" "kafka" {
  metadata {
    name      = "kafka"
    namespace = var.namespace
  }
  spec {
    selector = { app = "kafka" }
    port {
      port = 9092
    }
  }
}
