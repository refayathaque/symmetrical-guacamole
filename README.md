# Adventures in GCP and tf

### Dataflow job to inspect bigquery for sensitive data (directory title: dataflow-inspect-bigquery)
#### Steps:
  - Download dummy sensitive data from [here](https://cloud.google.com/architecture/creating-cloud-dlp-de-identification-transformation-templates-pii-dataset#downloading_the_sample_files)
  - Create tf resources for bucket and for object (pick the first of five csvs as the object)
  - Create tf resources for bigquery dataset, table and data transfer, also create bigquery schema json file