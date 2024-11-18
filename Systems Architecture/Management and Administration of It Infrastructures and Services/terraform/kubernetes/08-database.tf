resource "helm_release" "database" {
  name  = "database"
  repository = "https://charts.bitnami.com/bitnami"
  chart = "postgresql"

  timeout = 120
  cleanup_on_fail = true
  force_update    = true
  namespace       = "database"

  set {
    name = "auth.database"
    value = var.db_name
  }

  set {
    name = "auth.username"
    value = var.db_username
  }

  set {
    name = "auth.password"
    value = var.db_password
  }

  set {
    name  = "primary.initdb.scripts"
    value = "init_dbs.sql: CREATE TABLE transactions (id SERIAL PRIMARY KEY); ALTER TABLE transactions ADD COLUMN log TEXT; ALTER TABLE transactions ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;"
  }

  depends_on = [kubernetes_namespace.database]
}
