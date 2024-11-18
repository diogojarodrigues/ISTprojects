resource "helm_release" "prometheus" {
  name  = "prometheus"
  repository = "https://prometheus-community.github.io/helm-charts"
  chart = "prometheus"

  timeout = 120
  cleanup_on_fail = true
  force_update    = true
  namespace       = "monitoring"

  values = [ file("${path.module}/monitoring/prometheus/values.yaml") ]

  depends_on = [kubernetes_namespace.monitoring]
}

resource "helm_release" "grafana" {
  name  = "grafana"
  repository = "https://grafana.github.io/helm-charts"
  chart = "grafana"

  timeout = 120
  cleanup_on_fail = true
  force_update    = true
  namespace       = "monitoring"

  values = [ file("${path.module}/monitoring/grafana/values.yaml") ]

  depends_on = [kubernetes_namespace.monitoring]
}

