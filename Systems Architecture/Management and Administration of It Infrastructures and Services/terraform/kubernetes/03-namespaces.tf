resource "kubernetes_namespace" "application" {
  metadata {
    name = "application"

    labels = {
      istio-injection = "enabled"
    }
  }

  depends_on = [helm_release.istio_base]
}

resource "kubernetes_namespace" "monitoring" {
  metadata {
    name = "monitoring"
  }
}

resource "kubernetes_namespace" "database" {
  metadata {
    name = "database"
  }
}

resource "kubernetes_namespace" "istio_system" {
  metadata {
    name = "istio-system"
  }
}
