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

variable "namespace" {
  default = "observability"
}

variable "metrics_collector_image_tag" {
  default = "latest"
}

variable "config_api_image_tag" {
  default = "latest"
}
