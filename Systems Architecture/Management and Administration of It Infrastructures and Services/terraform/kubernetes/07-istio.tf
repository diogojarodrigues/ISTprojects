resource "helm_release" "istio_base" {
  name  = "istio-base"
  repository = "https://istio-release.storage.googleapis.com/charts"
  chart = "base"

  timeout = 120
  cleanup_on_fail = true
  force_update    = true
  namespace       = "istio-system"


  depends_on = [kubernetes_namespace.istio_system]
}

resource "helm_release" "istiod" {
  name  = "istiod"
  repository = "https://istio-release.storage.googleapis.com/charts"
  chart = "istiod"

  timeout = 120
  cleanup_on_fail = true
  force_update    = true
  namespace       = "istio-system"

  set {
    name = "pilot.autoscaleEnabled"
    value = "true"
  }

  set {
    name = "pilot.autoscaleMin"
    value = 3
  }

  set {
    name = "pilot.autoscaleMax"
    value = 5
  }

  set {
    name = "pilot.replicaCount"
    value = 3
  }

  depends_on = [helm_release.istio_base]
}
