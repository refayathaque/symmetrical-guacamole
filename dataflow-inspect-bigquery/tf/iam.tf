resource "google_service_account" "bigquery_transfer_from_storage" {
  account_id = "bigquery-transfer-from-storage"
}

resource "google_project_iam_custom_role" "bigquery_transfer_from_storage" {
  permissions = ["bigquery.transfers.update", "bigquery.datasets.get", "bigquery.datasets.update", "storage.objects.get"]
  role_id     = "bigquery_transfer_from_storage"
  # ^[a-zA-Z0-9_\\.]{3,64}$
  title = "bigquery-transfer-from-storage"
}
# https://cloud.google.com/bigquery-transfer/docs/cloud-storage-transfer#required_permissions

resource "google_bigquery_dataset_iam_binding" "bigquery_transfer_from_storage" {
  dataset_id = google_bigquery_dataset.dummy_sensitive_data.dataset_id
  members = [
    "serviceAccount:${google_service_account.bigquery_transfer_from_storage.email}",
  ]
  role = google_project_iam_custom_role.bigquery_transfer_from_storage.id
}
