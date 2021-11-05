package com.mycompany.app;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import com.google.api.services.bigquery.model.TableRow;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO.Write.CreateDisposition;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO.Write.WriteDisposition;

public class App 
{
    public interface MyOptions extends PipelineOptions 
    {
        @Description("BigQuery table to read data from, in the form " + "'project:dataset.table'.")
        @Default.String("project:dataset.table")
        String getInputTable();
        void setInputTable(String input);

        @Description("BigQuery table to write transformed data to, in the form " + "'project:dataset.table'.")
        @Default.String("project:dataset.table")
        String getOutputTable();
        void setOutputTable(String output);
        // Defining your configuration options via the command-line makes the code more easily portable across different runners.
        // can accept --input=<Bigquery table> and --output=<Bigquery table> as command-line arguments.
        // e.g., gcloud dataflow flex-template run "test" \ --parameters input="<dataset>.<table>" \ --parameters output="<dataset>.<table>"
        // ^ from https://github.com/GoogleCloudPlatform/java-docs-samples/tree/main/dataflow/flex-templates/streaming_beam_sql#running-a-flex-template-pipeline
    }

    public static void main( String[] args )
    {
        // Start by defining the options for the pipeline.
        // Beam SDKs include a command-line parser that you can use to set fields in PipelineOptions using command-line arguments.
        PipelineOptionsFactory.register(MyOptions.class);
        MyOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(MyOptions.class);

        // Then create the pipeline.
        Pipeline p = Pipeline.create(options);

        // Beam transforms use PCollection objects as inputs and outputs. As such, if you want to work with data in your pipeline, it must be in the form of a PCollection. After you’ve created your Pipeline, you’ll need to begin by creating at least one PCollection in some form. The PCollection you create serves as the input for the first operation in your pipeline.
        // To read from an external source, you use one of the Beam-provided I/O adapters. The adapters vary in their exact usage, but all of them read from some external data source and return a PCollection whose elements represent the data records in that source. Each data source adapter has a Read transform; to read, you must apply that transform to the Pipeline object itself.
        PCollection<TableRow> rows = p.apply("Read all data from bigquery table", 
            BigQueryIO.readTableRows()
                .from(options.getInputTable())
        );

        // return rows;

        System.out.println(rows);

        // Using DLP API to find sensitive data within the bigquery table rows and apply custom meta-data tags
        // The DoFn to perform on each element in the input PCollection.
        // The DoFn object that you pass to ParDo contains the processing logic that gets applied to the elements in the input collection. When you use Beam, often the most important pieces of code you’ll write are these DoFns - they’re what define your pipeline’s exact data processing tasks.
        static class ApplyMetadataTagToRowFn extends DoFn<TableRow> {
            @ProcessElement
            public void processElement(@Element TableRow row, OutputReceiver<TableRow> out) {
                // DLP API code goes here
                // Use OutputReceiver.output to emit the output element.
                out.output(row);
            }
        }

        PCollection<TableRow> taggedRows = rows.apply("Sensitive data identification and tagging",
            ParDo.of(new ApplyMetadataTagToRowFn())
        );

        // Writing to bigquery
        taggedRows.apply("Write all data to bigquery table", 
            BigQueryIO.writeTableRows()
                .to(options.getInputTable())
                .withCreateDisposition(CreateDisposition.CREATE_NEVER)
                .withWriteDisposition(WriteDisposition.WRITE_APPEND)
        );

        p.run().waitUntilFinish();
    }
}