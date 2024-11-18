output "region" {
  value       = var.region
  description = "GCloud Region"
}

output "project_id" {
  value       = var.project_id
  description = "GCloud Project ID"
}

output "kubernetes_cluster_name" {
  value       = module.cluster.name
  description = "GKE Cluster Name"
  sensitive = true
}

output "kubernetes_cluster_host" {
  value       = module.cluster.host
  description = "GKE Cluster Host"
  sensitive = true
}

output "kubernetes_cluster_ca_certificate" {
  value       = module.cluster.cluster_ca_certificate
  sensitive   = true
  description = "GKE Cluster CA Certificate"
}
