# Table of Contents

1. [Preface](README.md#markdown-header-preface)
2. [Pricing](README.md#markdown-header-pricing)
3. [Primary keys and Indexes](README.md#markdown-header-primary-keys-and-indexes)
4. [Query vs Scan](README.md#markdown-header-query-vs-scan)
5. [Streams](README.md#markdown-header-streams)
6. [Throughput Provisioning](README.md#markdown-header-throughput-provisioning)
7. [Concurrency and locking strategies](README.md#markdown-header-concurrency-and-locking-strategies)
8. [TOP APIs](README.md#markdown-header-top-apis)

* * *

# Preface

DynamoDB is a Regional AWS service that provides a fully managed NoSql Database in the Cloud. DynamoDB is capable of supporting both key-value and document data strucutre.
Information are always stored on SSD disks and are replicated across three geographically distinct data centers. Because of these distributed nature of DynamoDB is important to remember that the information may require time to be propagated, therefore two different types of Consistency models for reads operation are supported:

- **Eventual Consistent model**: the consistency across all the distributed copies of the data is reached usually within a second. This option offers the best performances but also means that a read operation, after a successful write, may not return consistent information.
- **Strongly Consistent model**: no delay is introduced so the result of a read operation always reflects all the writes operation that received a successful response prior to the read. This option does not however offer best performances.

The main elements that make up DynamoDB are:

- **Tables**: like tables in a traditional relation database, DynamoDB only allows 256 tables per Region, **this sia a soft limit and can be changed via AWS support**.
- **Items**: like rows in a traditional relation database.
- **Attributes**: that can be of a primitive (String, Numbers, Booleans, etc.) or complex type, like lists or other nested objects.

Data can be exported as a CSV file, howerver this functionality is limited to maximum of 100 records and also if the table contains nested documents, the columns in the csv files will contain json objects.

```
{
   "employeeId"  : 1 ,
   "name" : "Tom",
   "Surname" "Brown",
   "dateOfBirth": "1984-02-12",
   "address" {
      "firstLine" : "...",
      "secondLine": "...",
      "postCode": "..."
   }
}
```

DynamoDB support items of up to 400KB in size with a maximum of 35 nested attributes at the same time, no limit on the number of attributes that make up an item is set. **These are hard limits and cannot be changed.**

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Pricing

From a billing perspective two factors must be taken into consideration:

- The consistency model chosen for writing and reading items, 25 read and write capacities per month are free.
- The overall size of the data to be persisted, first 25GB per month is free.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Primary keys and Indexes

When storing data Amazon DynamoDB divides a table into multiple partitions and distributes the data based on the value of an hash function that uses the *partition key element* (aka hash-key) defined at table level. Two types of Primary key are supported:

-**Single attribute**. In this case a single attribute is chosen as Primary key and this value also acts as the *partition key element*. In this case the attribute value **cannot be duplicated within the table** (eg. employeeId).
-**Composite**. In this case two attributes are chosen, the first attribute acts as the **partition key element** whilst the second as **sort key** (aka range). In this case the partition key can have duplicates but the pair **(Partition Key, Sort Key>)** can't. Data in partitions is also sorted by sort-key.

When designing DynamoDB tables is always important to keep the *partition* concept in mind as if the workload is heavily unbalanced, meaning disproportionately focused on one or a few partitions, the operations will not achieve the overall provisioned throughput level.
DynamoDB supports two type of indexes:

-**Local Secondary Indexes**. These can only be created when the table is first created and uses the **same partition key** defined on the Primary key of the table but allows to define a **different sort key**. These types of indexes cannot be deleted or updated once table is created.
-**Global Secondary Indexes**. These can be created, updated and deleted even after the table is created and allows to define a new partition and optionally a new sort key on the table. Every Global Secondary Index must specify a different Read Throughput.

In terms of limitations DynamoDB only support 5 Local Secondary Indexes and 5 Global Secondary indexes per table. **These are hard limits and cannot t be changed.**

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Query vs Scan

A **Query** in DynamoDB is an operation that can be used to find items in a table using **only primary key** attribute values. In this case the search expression must contain the name of the attribute to use for filtering and a value optionally it can also include a sort-key expressison to further refine the response. By default a Query return all the attributes on the item but this behaviour can be changes setting a **ProjectionExpresison**.
The items returned as part of a Query operation are also always sorted by sort-key in ascending order, in order to reverse the order the **ScanIndexForward** option can be provided.
Query by default are always eventually consistent but thes behaviour can be changed to strongly consistent.

A **Scan** operation just examines every item in the table and returns all the attributes on the item, this behaviour can be changes setting a **ProjectionExpresison**. The Scan operation supports eventually consistent, default, and consistent reads.

For performance reason the developer should always try to design DynamoDB tables in a way that can benefit of indexes so that the Scan operation can be avoided.
Any item collection is also subject to a 10GB limit.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Streams

Streams allow to capture modification made on DynamoDB tables, modifications can be of three types: New items, Update items, Delete items.
Modification are kept alive for 24 hours and then are deleted, also these can be used to trigger Lambda functions to automate actions on data change.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Throughput Provisioning

In order to calculate the Throughput to used for read and writes operation the following formulas can be used:

- **Strong Consistent Read**: Item size approximated to the nearest 4KB increment and dived by 4. The result must be multiplied by the number of items to fetch per **second**.
- **Eventually Consistent Read**: Same as above but the result must be divided by 2.
- **Write**: Item size approximated to the nearest integer and multiplied to the nuimber of writes per **second**.

If the provisioned throughput is exceeded during a read or write operation then a **ProvisionedThroughputExceededException HTTP-400 error** is returned. User can provision 1 to 10.000 read and write units, but this is a **soft limit** and can be changed contacting the AWS support.

AWS also allows to reserve the read and write capacity at Regional level for an amount of time that goes from 1 up to 3 years (to save on the bill cost). In this case the minimum amount of capacity that can be provisioned is set to 100 units, however this capacity can be dynamically allocated on part or all the tables in the Region.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Concurrency and locking strategies

A common issue that may came up in DynamoDB is about the consistency of the data when multiple users try to perform write operation at the same time and for the same item.
DynamoDB doesn't support any locking mechanism and instead uses an Optimistic Locking strategy via one of the following two options:

- **ConditionalWrites**. Which uses a conditional expression that is verified immediately prior the write is executed.  If the condition is met then the item is written otherwise the operation is discarded and fails. ConditionalWrites are always idempotent meaning that if a certain operation is executed multiple time no effect will take place following the first call.
- **AtomicCounter**: That allows to increment/decrement a counter atomically. **AtomicCounters** are not idempotent so multiple calls will change the value of the attribute.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# TOP APIs

API | Consumes Read Write Capacity | Description
--- | --- | ---
CreateTable | No | Creates a table and specifies the primary index used for data access. **LimitExceededException** (HTTP-400 error) if the number of concurrent table requests exceeds the maximum allowed of 10. Also, for tables with secondary indexes, only one of those tables can be in the CREATING state at any point in time.
UpdateTable | No | Updates the provisioned throughput values for the given table.
DeleteTable | No | Deletes a table.
DescribeTable | No | Returns table size, status, and index information.
ListTables | No | Returns a list of all tables associated with the current account and endpoint.
PutItem | Yes | Creates a new item, or replaces an old item with a new item.
GetItem | Yes | Returns a set of attributes for the item with the given primary key.
BatchWriteItem | Yes | Inserts, replaces, and deletes multiple items across multiple tables in a single request, but not as a single transaction. Supports batches of up to 25 items to Put or Delete, with a maximum total request size of 16 MB.
BatchGetItem | Yes | Returns the attributes for multiple items from multiple tables using their primary keys. A single response has a size limit of 16 MB and returns a maximum of 100 items. Supports both strong and eventual consistency.

[*(back to the top)*](README.md#markdown-header-table-of-contents)