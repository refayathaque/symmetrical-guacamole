# Adventures in GCP and tf

### Dataflow job to inspect bigquery for sensitive data (directory title: dataflow-inspect-bigquery)

#### Steps:

- Download dummy sensitive data from [here](https://cloud.google.com/architecture/creating-cloud-dlp-de-identification-transformation-templates-pii-dataset#downloading_the_sample_files) ✔️
- Create tf resources for bucket and for object (pick the first of five csvs as the object) ✔️
- Create tf resources for bigquery dataset, table and data transfer, also create bigquery schema json file ✔️
  - The `data_path_template` argument in `google_bigquery_data_transfer_config` resource should follow the format `gs://<bucket_url>/<object_name>` which you can get with [interpolation](https://www.terraform.io/docs/language/expressions/strings.html#interpolation) `${google_storage_bucket.<bucket_resource_name>.url}/${google_storage_bucket_object.<object_resource_name>.output_name}`
  - You'll have to manually invoke the transfer after tf is done provisioning the resource
    - If you want to automate this you can, look at arguments like `schedule` in the tf [docs](Cp7&g[(RF:zPEH`2)
  - **Problem:** If you try to run the data transfer more than once (from the point of inception) it won't work and the log will say that it can't find the object - Workaround was setting the `write_disposition` tf argument in the `params` block to `"MIRROR"` - This has to be set from the beginning (when creating the `google_bigquery_data_transfer_config`) because it's immutable, you'll get an error like this:
  ```
  Immutable parameter write_disposition with value string_value: "APPEND"
  │   cannot be changed to string_value: "MIRROR"
  ```
- Create tf iam resources for bigquery to access the dummy sensitive data in storage and do the transfer
  - "When you load data into BigQuery, you need permissions that allow you to load data into new or existing BigQuery tables and partitions. If you are loading data from Cloud Storage, you'll also need access to the bucket that contains your [data."](https://cloud.google.com/bigquery-transfer/docs/cloud-storage-transfer#required_permissions)
- Create a [maven project](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html#creating-a-project) for the dataflow template image
  - `mvn archetype:generate -DgroupId=com.mycompany.app -DartifactId=my-app -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false`
    - [Archetypes](https://maven.apache.org/guides/introduction/introduction-to-archetypes.html)
    - "groupId uniquely identifies your project across all projects. A group ID should follow Java's package name rules. This means it starts with a reversed domain name you control. For example, com.mycompany.app, org.apache.maven, [org.apache.commons"](https://maven.apache.org/guides/mini/guide-naming-conventions.html#guide-to-naming-conventions-on-groupid-artifactid-and-version)
    - "artifactId is the name of the jar without version. If you created it, then you can choose whatever name you want with lowercase letters and no strange symbols. If it's a third party jar, you have to take the name of the jar as it's distributed eg. my-app, maven, commons-math"
    - [Maven Getting Started Guide](https://maven.apache.org/guides/getting-started/)
  - Test the project by building with `mvn package`, then run `java -cp target/my-app-1.0-SNAPSHOT.jar com.mycompany.app.App` - "Hello World!" will get logged
  - [Install](https://cloud.google.com/dataflow/docs/guides/installing-beam-sdk) the Apache Beam SDK, test that the dependencies were added properly by running `mvn package`
- Code the `App.java`, and create tf resources (if necessary), using the following as references/guides:
  - GCP flex template [docs](https://cloud.google.com/dataflow/docs/guides/templates/using-flex-templates#example-metadata-file)
    - Java [code](https://github.com/GoogleCloudPlatform/java-docs-samples/blob/main/dataflow/flex-templates/streaming_beam_sql/src/main/java/org/apache/beam/samples/StreamingBeamSql.java)
  - Apache beam programming [guide](https://beam.apache.org/documentation/programming-guide/)
    - Google BigQuery I/O [connector](https://beam.apache.org/documentation/io/built-in/google-bigquery/)
