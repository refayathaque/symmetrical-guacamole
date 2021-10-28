package com.mycompany.app;

import org.apache.beam.examples.snippets.transforms.io.gcp.bigquery.BigQueryMyData.MyData;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptor;

public class App 
{
    public interface MyOptions extends PipelineOptions 
    {
        @Description("BigQuery table to read data from, in the form " + "'project:dataset.table'.")
        @Default.String("dataset.table")
        String getInputTable();
        void setInputTable(String input);

        @Description("BigQuery table to write transformed data to, in the form " + "'project:dataset.table'.")
        @Default.String("dataset.table")
        String getOutputTable();
        void setOutputTable(String output);
        // Defining your configuration options via the command-line makes the code more easily portable across different runners.
        // pipeline can accept --input=<Bigquery table> and --output=<Bigquery table> as command-line arguments.
        // e.g., gcloud dataflow flex-template run "test" \ --parameters input="<dataset>.<table>" \ --parameters output="<dataset>.<table>" - got this from https://github.com/GoogleCloudPlatform/java-docs-samples/tree/main/dataflow/flex-templates/streaming_beam_sql#running-a-flex-template-pipeline
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
        PCollection<MyData> rows = p.apply("Read all data from bigquery table", BigQueryIO.readTableRows().from(options.getInputTable())).apply("TableRows to MyData", MapElements.into(TypeDescriptor.of(MyData.class)).via(MyData::fromTableRow));

        // return rows;

        pipeline.run()
    }
}
