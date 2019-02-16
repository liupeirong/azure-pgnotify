# azure-pgnotify
When you use a managed Cloud database, often times you are not the superuser of the database. So it may not be possible to use features such as [Logical Decoding](https://www.postgresql.org/docs/9.5/logicaldecoding-explanation.html) for CDC (Change Data Capture). This example demonstrates how to capture changes when using Azure Database for PostgreSQL using pg_notify.

![Alt text](/images/azurepgnotify.png?raw=true "Azure pg_notify")

This app listens on the specified pg_notify channel, and sends each notification to Azure Event Hubs through Kafka producer API.  Azure EventHub is configured with [Capture]() which automatically stores receieved events in Azure Blob Storage.  You can then run, for example, Azure Databricks Spark job, to analyze and query the CDC events.

### Define pg_notify
In PostgreSQL, define a pg_notify function. For example, the following function converts a inserted or updated row into JSON format to send to pg_notify:
```sql
CREATE OR REPLACE FUNCTION public."process_event_notify"()
RETURNS trigger
LANGUAGE plpgsql
COST 100
VOLATILE NOT LEAKPROOF 
AS $BODY$
DECLARE
BEGIN
  PERFORM pg_notify('{channel_name}', row_to_json(NEW)::text);
  RETURN NEW;
END;
$BODY$;
```
### Set trigger in PostgreSQL
Define a trigger in the table which you want to track changes. For example, the following trigger runs pg_notify for every insert into the target table:
```sql
CREATE TRIGGER process_event_update
AFTER INSERT 
ON public."{table_name}"
FOR EACH ROW
EXECUTE PROCEDURE public."process_event_notify"();
```
### Set up Kafka on Event Hubs and enable Capture
1. Create a [Kafka enabled Event Hub](https://docs.microsoft.com/en-us/azure/event-hubs/event-hubs-create-kafka-enabled) in Azure. 
2. Set up [Event Hubs Capture](https://docs.microsoft.com/en-us/azure/event-hubs/event-hubs-capture-overview#setting-up-event-hubs-capture). 

### Test pg_notify
Go to PostgreSQL, and insert a row into the table on which you defined the above trigger.  You should be able to see the inserted row showing up as a new blob in the destination Azure Blob Storage specified in the Event Hubs Capture above.  Note that the file will appear either after the specified time has elapsed or the specified size of the captured data has been reached. 

### Simple spark notebook to query captured change feeds
You can now write a Spark job to query the captured data in Blob Storage.  For example, the following simple Spark job will return each captured row inserted into the Postgres table. 
```scala
import org.apache.spark.sql.types._

val storagePath = "wasbs://{blob_container}@{account}.blob.core.windows.net/{eventhub}/{eventhub_capture}/*/*/*/*/*/*";
val df = spark.read.format("avro").load(storagePath)

display(df.select($"Body".cast(StringType)))
```