resource "kubernetes_service" "bitcoin" {
  metadata {
    name = "bitcoin"

    labels = {
      app  = "bitcoin"
      tier = "backend"
    }

    namespace = kubernetes_namespace.application.id
  }

  spec {
    selector = {
      app  = "bitcoin"
      tier = "backend"
    }

    port {
      port        = 80
      target_port = 80
    }

    type = "ClusterIP"
  }
}

resource "kubernetes_service" "solana" {
  metadata {
    name = "solana"

    labels = {
      app  = "solana"
      tier = "backend"
    }

    namespace = kubernetes_namespace.application.id
  }

  spec {
    selector = {
      app  = "solana"
      tier = "backend"
    }

    port {
      port        = 80
      target_port = 80
    }

    type = "ClusterIP"
  }
}

resource "kubernetes_service" "ethereum" {
  metadata {
    name = "ethereum"

    labels = {
      app  = "ethereum"
      tier = "backend"
    }

    namespace = kubernetes_namespace.application.id
  }

  spec {
    selector = {
      app  = "ethereum"
      tier = "backend"
    }

    port {
      port        = 80
      target_port = 80
    }

    type = "ClusterIP"
  }
}

resource "kubernetes_service" "monero" {
  metadata {
    name = "monero"

    labels = {
      app  = "monero"
      tier = "backend"
    }

    namespace = kubernetes_namespace.application.id
  }

  spec {
    selector = {
      app  = "monero"
      tier = "backend"
    }

    port {
      port        = 80
      target_port = 80
    }

    type = "ClusterIP"
  }
}

resource "kubernetes_service" "frontend" {
  metadata {
    name = "frontend"

    labels = {
      app  = "frontend"
      tier = "frontend"
    }

    namespace = kubernetes_namespace.application.id
  }

  spec {
    selector = {
      app  = "frontend"
      tier = "frontend"
    }

    port {
      port        = 80
      target_port = 80
    }
    type = "LoadBalancer"
  }
}