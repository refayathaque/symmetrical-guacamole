# Adventures in GCP and tf

### Dataflow job to inspect bigquery for sensitive data (directory title: dataflow-inspect-bigquery)

#### Steps:

- Download dummy sensitive data from [here](https://cloud.google.com/architecture/creating-cloud-dlp-de-identification-transformation-templates-pii-dataset#downloading_the_sample_files) ✔️
- Create tf resources for bucket and for object (pick the first of five csvs as the object) ✔️
- Create tf resources for bigquery dataset, table and data transfer, also create bigquery schema json file ✔️
  - The `data_path_template` argument in `google_bigquery_data_transfer_config` resource should follow the format `gs://<bucket_url>/<object_name>` which you can get with [interpolation](https://www.terraform.io/docs/language/expressions/strings.html#interpolation) `${google_storage_bucket.<bucket_resource_name>.url}/${google_storage_bucket_object.<object_resource_name>.output_name}`
  - You'll have to manually invoke the transfer after tf is done provisioning the resource
- Create tf iam resources for bigquery to access the dummy sensitive data in storage and do the transfer
  - "When you load data into BigQuery, you need permissions that allow you to load data into new or existing BigQuery tables and partitions. If you are loading data from Cloud Storage, you'll also need access to the bucket that contains your [data."](https://cloud.google.com/bigquery-transfer/docs/cloud-storage-transfer#required_permissions)
