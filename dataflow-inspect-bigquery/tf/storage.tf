resource "google_storage_bucket" "dummy_sensitive_data" {
  name          = "dummy-sensitive-data"
  location      = "us-east4"
  force_destroy = true
}

resource "google_storage_bucket_object" "dummy_sensitive_data" {
  name = "CCRecords_1564602825.csv"
  # object name should always have extension (e.g., csv) appended
  source = "../solution-test/CCRecords_1564602825.csv"
  bucket = google_storage_bucket.dummy_sensitive_data.name
}

resource "google_storage_bucket" "dataflow_inspect_bigquery" {
  name          = "dataflow-inspect-bigquery"
  location      = "us-east4"
  force_destroy = true
}

resource "google_storage_bucket_object" "dataflow_inspect_bigquery_template_spec_file" {
  name   = "template-spec.json"
  source = "../my-app/template-spec.json"
  bucket = google_storage_bucket.dataflow_inspect_bigquery.name
}
