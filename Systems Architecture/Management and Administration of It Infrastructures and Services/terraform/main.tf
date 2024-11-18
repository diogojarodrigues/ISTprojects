# Variable definitions
variable "project_id" {
  description = "project id"
}

variable "region" {
  description = "region"
}

variable "db_username" {
  description = "db username"
  default = "crypto"
}

variable "db_password" {
  description = "db password"
  default = "SuperSecretPassword"
}

variable "db_name" {
  description = "db name"
  default = "crypto"
}

data "google_client_config" "default" {
  depends_on = [module.cluster]
}

# Module Definitions
module "vpc" {
    source = "./vpc"
    project_id = var.project_id
    region = var.region
}

module "cluster" {
    source = "./cluster"
    project_id = var.project_id
    region = var.region
    network = module.vpc.network
    subnet = module.vpc.subnet
}

module "kubernetes" {
    source = "./kubernetes"
    token = data.google_client_config.default.access_token
    client_certificate = module.cluster.client_certificate
    client_key = module.cluster.client_key
    cluster_ca_certificate = module.cluster.cluster_ca_certificate
    host = module.cluster.host
    db_username = var.db_username
    db_password = var.db_password
    db_name = var.db_name
}
