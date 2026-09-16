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
  default = "booking"
}

variable "inventory_image_tag" {
  default = "fb431cd"
}

variable "payment_image_tag" {
  default = "3b65de7"
}
