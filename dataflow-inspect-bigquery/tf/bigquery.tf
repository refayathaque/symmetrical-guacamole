resource "google_bigquery_dataset" "dummy_sensitive_data" {
  dataset_id = "dummy_sensitive_data"
  # The ID must contain only letters (a-z, A-Z), numbers (0-9), or underscores (_). The maximum length is 1,024 characters. No dashes.
  location = var.region
}

resource "google_bigquery_table" "dummy_sensitive_data" {
  dataset_id          = google_bigquery_dataset.dummy_sensitive_data.dataset_id
  deletion_protection = false
  schema              = file("bigquery-schema.json")
  table_id            = "dummy_sensitive_data"
  # required regular expression: ^([A-z0-9_$]*|{([A-z0-9\"%+\-|\\\.;])+})*$ No dashes.
}

output "bigquery_table_id" {
  value = google_bigquery_table.dummy_sensitive_data.id
}

resource "google_bigquery_data_transfer_config" "dummy_sensitive_data" {
  data_source_id         = "google_cloud_storage"
  destination_dataset_id = google_bigquery_dataset.dummy_sensitive_data.dataset_id
  display_name           = "dummy-sensitive-data"
  location               = var.region
  params = {
    data_path_template              = "${google_storage_bucket.dummy_sensitive_data.url}/${google_storage_bucket_object.dummy_sensitive_data.output_name}"
    destination_table_name_template = google_bigquery_table.dummy_sensitive_data.table_id
    field_delimiter                 = ","
    file_format                     = "CSV"
    max_bad_records                 = 0
    skip_leading_rows               = 1
    write_disposition               = "MIRROR"
  }
  schedule_options {
    disable_auto_scheduling = true
  }
}

# https://github.com/batect/updates.batect.dev/blob/fc5666360296907a18999b0cbf5535ef32ae5419/infra/event_table/transfer.tf
# https://github.com/ashwini2206soni/bigquery_data_transfer/blob/719d0df6679d0d2f1656435a7d16a575aff41143/terraform/main.tf
