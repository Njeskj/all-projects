terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.31"
    }
  }
}

provider "kubernetes" {
  config_path    = var.kubeconfig_path
  config_context = "kind-platform"
}

variable "kubeconfig_path" {
  default = "~/.kube/config"
}

variable "namespace" {
  default = "observability"
}

variable "metrics_collector_image_tag" {
  default = "latest"
}

variable "config_api_image_tag" {
  default = "latest"
}
