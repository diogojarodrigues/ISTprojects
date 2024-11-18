resource "google_container_cluster" "primary" {
  name     = "${var.project_id}-gke"
  location = var.region
  deletion_protection = false
  network = var.network
  subnetwork = var.subnet
  
  addons_config {
    network_policy_config {
      disabled = true
    }
  }

  node_pool {
    name = "default-pool"
    initial_node_count = 1

    autoscaling {
      total_min_node_count = 1
      total_max_node_count = 8
    }

    node_config {
      oauth_scopes = [
        "https://www.googleapis.com/auth/logging.write",
        "https://www.googleapis.com/auth/monitoring",
      ]

      disk_size_gb = 20

      labels = {
        env = var.project_id
      }

      preemptible  = true # Note: preemptible nodes shouldn't be used for production workloads
      machine_type = "n1-standard-2"
      tags         = ["gke-node", "${var.project_id}-gke"]
      metadata = {
        disable-legacy-endpoints = "true"
      }
    }
  }
}
