provider "kubernetes" {
  host                   = "https://${var.host}"

  token                  = var.token
  client_certificate     = base64decode(var.client_certificate)
  client_key             = base64decode(var.client_key)
  cluster_ca_certificate = base64decode(var.cluster_ca_certificate)
}


provider "helm" {
  kubernetes {
    host = "https://${var.host}"

    token                  = var.token
    client_certificate     = base64decode(var.client_certificate)
    client_key             = base64decode(var.client_key)
    cluster_ca_certificate = base64decode(var.cluster_ca_certificate)
  }
}

