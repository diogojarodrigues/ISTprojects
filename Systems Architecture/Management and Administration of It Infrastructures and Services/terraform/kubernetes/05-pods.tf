resource "kubernetes_deployment" "bitcoin" {
  metadata {
    name = "bitcoin"

    labels = {
      app  = "bitcoin"
      tier = "backend"
    }

    namespace = kubernetes_namespace.application.id
  }

  spec {
    selector {
      match_labels = {
        app  = "bitcoin"
        tier = "backend"
      }
    }

    template {
      metadata {
        labels = {
          app  = "bitcoin"
          tier = "backend"
        }
      }
      spec {
        container {
          image = "pereira0x/bitcoin-service:latest"
          name  = "bitcoin"
          image_pull_policy = "Always"

          port {
            container_port = 80
          }

          env {
            name = "DB_USERNAME"
            value = var.db_username
          }

          env {
            name = "DB_PASSWORD"
            value = var.db_password
          }

          env {
            name = "DB_NAME"
            value = var.db_name
          }

          env {
            name = "DB_HOST"
            value = "database-postgresql.database.svc.cluster.local"
          }

          resources {
            requests = {
              cpu    = "200m"
            }
            limits = {
              cpu    = "500m"
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_deployment" "solana" {
  metadata {
    name = "solana"

    labels = {
      app  = "solana"
      tier = "backend"
    }

    namespace = kubernetes_namespace.application.id
  }

  spec {
    selector {
      match_labels = {
        app  = "solana"
        tier = "backend"
      }
    }

    template {
      metadata {
        labels = {
          app  = "solana"
          tier = "backend"
        }
      }
      spec {
        container {
          image = "pereira0x/solana-service:latest"
          name  = "solana"
          image_pull_policy = "Always"

          port {
            container_port = 80
          }

          env {
            name = "DB_USERNAME"
            value = var.db_username
          }

          env {
            name = "DB_PASSWORD"
            value = var.db_password
          }

          env {
            name = "DB_NAME"
            value = var.db_name
          }

          env {
            name = "DB_HOST"
            value = "database-postgresql.database.svc.cluster.local"
          }

          resources {
            requests = {
              cpu    = "200m"
            }
            limits = {
              cpu    = "500m"
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_deployment" "monero" {
  metadata {
    name = "monero"

    labels = {
      app  = "monero"
      tier = "backend"
    }

    namespace = kubernetes_namespace.application.id
  }

  spec {
    selector {
      match_labels = {
        app  = "monero"
        tier = "backend"
      }
    }

    template {
      metadata {
        labels = {
          app  = "monero"
          tier = "backend"
        }
      }
      spec {
        container {
          image = "pereira0x/monero-service:latest"
          name  = "monero"
          image_pull_policy = "Always"

          port {
            container_port = 80
          }

          env {
            name = "DB_USERNAME"
            value = var.db_username
          }

          env {
            name = "DB_PASSWORD"
            value = var.db_password
          }

          env {
            name = "DB_NAME"
            value = var.db_name
          }

          env {
            name = "DB_HOST"
            value = "database-postgresql.database.svc.cluster.local"
          }

          resources {
            requests = {
              cpu    = "200m"
            }
            limits = {
              cpu    = "500m"
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_deployment" "ethereum" {
  metadata {
    name = "ethereum"

    labels = {
      app  = "ethereum"
      tier = "backend"
    }

    namespace = kubernetes_namespace.application.id
  }

  spec {
    selector {
      match_labels = {
        app  = "ethereum"
        tier = "backend"
      }
    }

    template {
      metadata {
        labels = {
          app  = "ethereum"
          tier = "backend"
        }
      }
      spec {
        container {
          image = "pereira0x/ethereum-service:latest"
          name  = "ethereum"
          image_pull_policy = "Always"

          port {
            container_port = 80
          }

          env {
            name = "DB_USERNAME"
            value = var.db_username
          }

          env {
            name = "DB_PASSWORD"
            value = var.db_password
          }

          env {
            name = "DB_NAME"
            value = var.db_name
          }
          

          env {
            name = "DB_HOST"
            value = "database-postgresql.database.svc.cluster.local"
          }

          resources {
            requests = {
              cpu    = "200m"
            }
            limits = {
              cpu    = "500m"
            }
          }
        }
      }
    }
  }
}


resource "kubernetes_deployment" "frontend" {
  metadata {
    name = "frontend"

    labels = {
      app  = "frontend"
      tier = "frontend"
    }

    namespace = kubernetes_namespace.application.id
  }

  spec {
    selector {
      match_labels = {
        app  = "frontend"
        tier = "frontend"
      }
    }

    template {
      metadata {
        labels = {
          app  = "frontend"
          tier = "frontend"
        }
      }
      spec {
        container {
          image = "pereira0x/frontend-service:latest"
          name  = "frontend"
          image_pull_policy = "Always"

          port {
            container_port = 80
          }

          resources {
            requests = {
              cpu    = "200m"
            }
            limits = {
              cpu    = "500m"
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_horizontal_pod_autoscaler" "bitcoin-autoscaler" {
  metadata {
    name = "bitcoin-autoscaler"
    namespace = kubernetes_namespace.application.id
  }
  spec {
    max_replicas = 50
    min_replicas = 3
    scale_target_ref {
      api_version = "apps/v1"
      kind = "Deployment"
      name = kubernetes_deployment.bitcoin.metadata.0.name
    }
    target_cpu_utilization_percentage = 50
  }
}

resource "kubernetes_horizontal_pod_autoscaler" "solana-autoscaler" {
  metadata {
    name = "solana-autoscaler"
    namespace = kubernetes_namespace.application.id
  }
  spec {
    max_replicas = 50
    min_replicas = 3
    scale_target_ref {
      api_version = "apps/v1"
      kind = "Deployment"
      name = kubernetes_deployment.solana.metadata.0.name
    }
    target_cpu_utilization_percentage = 50
  }
}

resource "kubernetes_horizontal_pod_autoscaler" "monero-autoscaler" {
  metadata {
    name = "monero-autoscaler"
    namespace = kubernetes_namespace.application.id
  }
  spec {
    max_replicas = 50
    min_replicas = 3
    scale_target_ref {
      api_version = "apps/v1"
      kind = "Deployment"
      name = kubernetes_deployment.monero.metadata.0.name
    }
    target_cpu_utilization_percentage = 50
  }
}

resource "kubernetes_horizontal_pod_autoscaler" "ethereum-autoscaler" {
  metadata {
    name = "ethereum-autoscaler"
    namespace = kubernetes_namespace.application.id
  }
  spec {
    max_replicas = 50
    min_replicas = 3
    scale_target_ref {
      api_version = "apps/v1"
      kind = "Deployment"
      name = kubernetes_deployment.ethereum.metadata.0.name
    }
    target_cpu_utilization_percentage = 50
  }
}


resource "kubernetes_horizontal_pod_autoscaler" "frontend-autoscaler" {
  metadata {
    name = "frontend-autoscaler"
    namespace = kubernetes_namespace.application.id
  }
  spec {
    max_replicas = 100
    min_replicas = 5
    scale_target_ref {
      api_version = "apps/v1"
      kind = "Deployment"
      name = kubernetes_deployment.frontend.metadata.0.name
    }
    target_cpu_utilization_percentage = 50
  }
}
