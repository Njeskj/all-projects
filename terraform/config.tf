# ponytail: Terraform here manages ONLY ConfigMap/Secret in this namespace —
# Deployments/Services stay owned by k8s/ + ArgoCD to avoid dual-writer
# conflicts (see tasks/plan.md). Idempotent: re-apply after no drift = no changes.

resource "kubernetes_config_map" "metrics_collector_config" {
  metadata {
    name      = "metrics-collector-config"
    namespace = var.namespace
  }
  data = {
    KAFKA_BROKERS = "kafka.${var.namespace}.svc.cluster.local:9092"
  }
}

resource "kubernetes_config_map" "config_api_config" {
  metadata {
    name      = "config-api-config"
    namespace = var.namespace
  }
  data = {
    SPRING_REDIS_HOST           = "redis.${var.namespace}.svc.cluster.local"
    SPRING_REDIS_PORT           = "6379"
    SPRING_KAFKA_BOOTSTRAP_SERVERS = "kafka.${var.namespace}.svc.cluster.local:9092"
    METRICS_COLLECTOR_GRPC_HOST = "metrics-collector.${var.namespace}.svc.cluster.local"
    METRICS_COLLECTOR_GRPC_PORT = "28090"
  }
}

resource "kubernetes_secret" "config_api_secret" {
  metadata {
    name      = "config-api-secret"
    namespace = var.namespace
  }
  type = "Opaque"
  data = {
    # ponytail: plaintext dev creds in-repo for a local kind cluster; replace
    # with a real secret manager / sealed-secrets before any shared environment.
    SPRING_DATASOURCE_URL      = base64encode("jdbc:postgresql://postgres.${var.namespace}.svc.cluster.local:5432/configapi")
    SPRING_DATASOURCE_USERNAME = base64encode("postgres")
    SPRING_DATASOURCE_PASSWORD = base64encode("postgres")
  }
}
