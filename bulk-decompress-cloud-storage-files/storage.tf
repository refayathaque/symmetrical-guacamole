resource "google_storage_bucket" "bulk_decompress_cloud_storage" {
  name          = "bulk-decompress-cloud-storage"
  location      = "us-east4"
  force_destroy = true
}

resource "google_storage_bucket_object" "sample_file_1" {
  name   = "compressed/Affy_test_data.tar.gz"
  source = "compressed-files/Affy_test_data.tar.gz"
  bucket = google_storage_bucket.bulk_decompress_cloud_storage.name
}

resource "google_storage_bucket_object" "sample_file_2" {
  name   = "compressed/External_test_data.tar.gz"
  source = "compressed-files/External_test_data.tar.gz"
  bucket = google_storage_bucket.bulk_decompress_cloud_storage.name
}

resource "google_storage_bucket_object" "sample_file_3" {
  name   = "compressed/yeast_orfs.new.20040422.gz"
  source = "compressed-files/yeast_orfs.new.20040422.gz"
  bucket = google_storage_bucket.bulk_decompress_cloud_storage.name
}
