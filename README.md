# azure-pgnotify
When you use a managed Cloud database, often times you are not the superuser of the database. So it may not be possible to use features such as [Logical Decoding](https://www.postgresql.org/docs/9.5/logicaldecoding-explanation.html) for CDC (Change Data Capture). This example demonstrates how to capture changes when using Azure Database for PostgreSQL using pg_notify.

### Define pg_notify

### Set trigger in PostgreSQL

### Set up Kafka on EventHub

### Enable Eventhub capture

### Capture an insert

### Simple spark notebook to query captured change feeds:

```scala
import org.apache.spark.sql.types._

val storagePath = "wasbs://{blob_container}@{account}.blob.core.windows.net/{eventhub}/{eventhub_capture}/*/*/*/*/*/*";
val df = spark.read.format("avro").load(storagePath)

display(df.select($"Body".cast(StringType)))
```