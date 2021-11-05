resource "google_dataflow_job" "bulk_decompress_cloud_storage" {
  name              = "bulk-decompress-cloud-storage"
  template_gcs_path = "gs://dataflow-templates-us-central1/latest/Bulk_Decompress_GCS_Files"
  temp_gcs_location = "${google_storage_bucket.bulk_decompress_cloud_storage.url}/tmp_dir"
  parameters = {
    inputFilePattern  = "${google_storage_bucket.bulk_decompress_cloud_storage.url}/compressed/*.gz"
    outputDirectory   = "${google_storage_bucket.bulk_decompress_cloud_storage.url}/decompressed"
    outputFailureFile = "${google_storage_bucket.bulk_decompress_cloud_storage.url}/decompressed/failed.csv"
  }
}
