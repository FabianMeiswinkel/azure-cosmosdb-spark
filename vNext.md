# Cosmos DB OLTP Spark connector vNext - Scenarios



## Motivation

The intent for a new major version and refactoring of the Cosmos DB OLTP Spark connector is to provide:

- significant engineering improvements (adding sufficient unit test coverage, adding build pipeline with end-to-end gate tests, adding better documentation for the prime scenarios - including explanations in when to use OLTP vs. OLAP Spark connector etc.)
- moving the OLTP connector to the DataSourceV2 API (which will allow enabling scenarios like continuous processing/streams) for which Cosmos DB (backend/service) is very well suited - but using the V1 DataSource APIs prevents us form exposing these capabilities to Spark users.
- Unifying dependencies - currently the OLTP Spark connector depends on the Java V2 sync SDK, the Bulk Executor library (Java) and the V2 async SDK. Instead the goal is to only have a dependency to use the V4 SDK - which will provide the benefits made in the latest version of the SDK and reduce CRI and maintenance cost.



## Scope

Below is an early proposal to define the scope of what a new major version would need to address.



#### Prioritization

I have added priorities to the individual stories and tasks below. Just clarifying here what they are meant to express. The lower the Priority index , the more important.

- 001 - 100: Blocker for GA and any public preview
- 100 - 200: Blocker for GA
- 200 - 300: Not necessarily a blocker for GA but customers/workloads are known that  would be blocked. So expected and desired to be delivered shortly after GA
- 300 - 400: Optional - post GA or next major version 



### Stories / Tasks

#### Cosmos DB OLTP Spark connector as a sink

Below are the use-cases where data is stored in Cosmos DB from a Spark job. The OLAP connector is read-only - so all scenarios below are exclusively relevant for the OLTP connector.



##### Bulk ingestion into Cosmos DB - Priority 120

Ingestion of large amount of data into Cosmos DB optimized on throughput vs. ingestion latency. Use for data migrations or to ingest data into Cosmos DB from other data sources (like from a data lake, text file, parquet file etc.). This use case works reasonably well with the current OLTP Spark connector already.

##### Near-real-time processing  - Priority 030

Ingestion of data into Cosmos DB for near real-time processing - ingestion latency as well as scalability are more important than overall throughput. Via Structured streaming with micro batches (the structured streaming also supported in DataSource V1) usual expected ingestion latency (of an entire micro batch) is in the order of hundreds of milliseconds to low digit seconds. To get even closer to real-time processing Spark also introduced continuous processing for DataSource V2 connectors - the goal here is to get ingestion latency in the order of < 100 milliseconds.  Only Connector that is supporting this scenario well yet is Kafka  - Cosmos DB backend capabilities are ideal for these use-cases. Customers with the < 100 milliseonds latency requirement are usually ok with the trade-offs the provisioned throughput model provides and our latency SLA is a strong argument. But to enable this scenario the Spark connector needs to be carefully optimized for it. 

Right now we only support Structured streaming with Micro batches - and even there we have several gaps and room for improving reliability (especially for managing concurrency dynamically and on how to signal back-pressure - like when throttling starts because the backend is the bottleneck) and to optimize latency.



#### Cosmos DB OLTP Spark connector as a Data source

After releasing the OLAP Spark connector any use-cases involving  processing of all data in Cosmos DB (scan jobs) or based on queries returning large amount of data would be better off using the OLAP connector - it will provide better throughput and significantly lower cost (no RU/s). For some period of time - while the OLAP connector is only available via Synapse - and not generally available in other Spark environments (like Databricks or HDInsights) there will still be a couple of customers using the OLTP Spark connector in these scenarios. The clear focus when there is a need to make trade-offs in the design is to optimize on selective reads (where filter push down results in small datasets) and changefeed processing (for any real-time scenarios where the OLAP ingestion latency is too high) over optimizing these broad scan scenarios.



##### Scans/Queries

In the current version of the OLTP Spark connector we provide one DataSource that depending on configuration can either read data from containers or form changefeed. Instead we will provide two distinct DataSources - one for reading/writing data and one for reading/processing the changefeed.

###### Non-Selective queries (e.g. expecting query result to contain thousands of records) - Priority 210

Any queries resulting in large datasets or full table scans.



###### Selective queries (e.g. filtered by logical partition key value) - Priority 110

Queries resulting in small datasets - definitely need an efficient way of pushing down filters for indexed properties (especially logical partition key). Filter push down for aggregates and order by can be useful - but I wouldn't consider this a must-have feature.



##### Changefeed

Accessing changefeed will be provided via a separate data source



###### Batch processing of changes - Priority 010

For processing changefeed in batch jobs (from the beginning, form a certain point in time or since the last execution of the processing batch job)



###### Near-real-time processing of changes - Priority 020

Provides access to the change-feed for near-real time processing - like in the current version of the OLTP connector via micro-batches as well as via continuous processing.





