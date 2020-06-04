Motivation

The intent for a new major version and refactoring of the Cosmos DB OLTP Spark connector is to provide

- significant engineering improvements (adding sufficient unit test coverage, adding build pipeline with end-to-end gate tests, adding better documentation for the prime scenarios - including explanations in when to use OLTP vs. OLAP Spark connector etc.)
- moving the OLTP connector to the DataSourceV2 API (which will allow enabling scenarios like continuous processing/streams) for which Cosmos DB (backend/service) is very well suited - but using the V1 DataSource APIs prevents us form exposing these capabilities to Spark users.
- Unifying dependencies - currently the OLTP Spark connector depends on the Java V2 sync SDK, the Bulk Executor library (Java) and the V2 async SDK. Instead the goal is to only have a dependency to use the V4 SDK - which will provide the benefits made in the latest version of the SDK and reduce CRI and maintenance cost.



Scope

Below is an early proposal to define the scope of what a new major version would need to address.



Prioritization

I have added priorities to the individual stories and tasks below. Just clarifying here what they are meant to express. The lower the Priority index , the more important.

- 001 - 100: Blocker for GA and any public preview
- 100 - 200: Blocker for GA
- 200 - 300: Not necessarily a blocker for GA but customers/workloads are known that  would be blocked. So expected and desired to be delivered shortly after GA
- 300 - 400: Optional - post GA or next major version 



Stories / Tasks





