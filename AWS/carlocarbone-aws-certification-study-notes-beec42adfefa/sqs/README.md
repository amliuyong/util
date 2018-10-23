# Table of Contents

1. [Preface](README.md#markdown-header-preface)
2. [Pricing](README.md#markdown-header-pricing)
3. [Standard queue](README.md#markdown-header-standard-queue)
4. [Long vs Short Poll](README.md#markdown-header-long-vs-short-poll)
5. [SNS SQS Fan Out](README.md#markdown-header-sns-sqs-fan-out)
6. [FIFO Queuesl](README.md#markdown-header-fifo-queues)
7. [TOP APIs](README.md#markdown-header-top-apis)

* * *

# Preface

SQS is an AWS Regional service that provides queuing capabilities in the Cloud and is mainly being used to decouple Applications.
In SQS users define queues where producers can send messages that then gets processed by consumers (like EC2 instances, Lambada, etc.), the maximum message size is set to 256KB, **this is an hard limit** and cannot be changed.

SQS uses a poll mechanism meaning that message consumers have to cyclically check if there is a message to consume on the queue. This has a direct impact on the billing cost as it is calculated based on the total number of requests that are sent to the SQS endpoint.
If more than 256KB need to be sent on an SQS queue in a single message, AWS advises to save these information first in S3 and use a correlation identifier on the message itself to help consumers to lookup the object later on.

At the time of writing, AWS provides two main types of queues:

- Standard (available in all regions)
- FIFO (currently available in the US West (Oregon), US East (Ohio), US East (N. Virginia), and EU (Ireland) regions).

AWS SQS doesn't provide a priority mechanism, so if there is a need to process certain message before others a different privileged queue should be created.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Pricing

From a billing perspective cost is calculated based on the total number of requests, every request can be up to 64KB in size, first million requests per month is free.

**Example**: A message is 200KB in size, how many requests in total will be billed to put the message on the queue?
Solution: 200KB / 64KB = 3.125 which needs to be rounded by excess so a total of 4 requests will be billed.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Standard queue

On a Standard queue, when a consumer picks up a message, a Timer is started, a sort of StopWatch, and the message becomes temporarily unavailable for other consumers (message is in-flight state).
If the **VisibilityTimeout** expires (Timer > VisibilityTimeout) the message becomes again visible. This means that SQS guarantees the message delivery but can't guarantee that the message is consumed only once.
Order is also undetermined as SQS uses a distributed architecture where the data is replicated across multiple AWS facilities for resilience and performance purposes.

The **VisbilityTimeout** for a single message can also be changed on the fly via an API call, **ChangeMessageVisibility**, in this case the change only affects the message and not the entire queue.

When a new Standard queue is created the following attributes have to be provided:

Property | Min value | Max value | Default value | Description
--- | --- | --- | --- | ---
DelaySeconds | 0 minutes | 15 minutes | 0 seconds | The time in seconds that the delivery of all messages in the queue will be delayed
MessageRetentionPeriod | 1 minute | 14 days | 4 days | The number of seconds Amazon SQS retains a message
ReceiveMessageWaitTimeSeconds | 1 second | 20 seconds | 0 seconds | Specifies the duration, in seconds, that the ReceiveMessage action call waits until a message is in the queue in order to include it in the response, as opposed to returning an empty response if a message is not yet available.
VisibilityTimeout | 0 seconds | 12 hours | 30 seconds | The length of time during which a message will be unavailable once a message is delivered to the queue. ** If the value is set to 0 then the message will be always available to consumer unless explicitly deleted.**

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Long vs Short Poll

As already explained in the previous sections, messages sent to SQS are distributed over several AWS facilities and a polling mechanism is used by consumers to pick up new messages to process. Polling introduces an high number of requests being sent to the SQS endpoint with a considerable impact on the overall bill cost. This happens because AWS, by default uses a Short Polling strategy, sampling messages to delivery only from a small sub-set of the servers in the AWS facility; in this case most of the requests sent by the consumers return an empty response.
In order to minimise the overall cost a Long polling strategy may be used instead. When long polling is enabled, **ReceiveMessageWaitTimeSeconds** set to 20 seconds, AWS will sample the messages to delivery from all the server in the AWS facility allowing to reduce the number of empty responses.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# SNS SQS Fan Out

Is a mechanism that can be used to **glue** together AWS SNS and SQS services. In simple words an SQS queue can be subscribed to a SNS topic and receive messages as soon as they are delivered on it. This is particularly useful when multiple actions needs to be performed on the same message.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# FIFO Queues

In a FIFO queue, the order is strictly guaranteed and messages get delivered exactly once.AWS has a mechanism in place to deduplicate messages sent by multiple producers over a 5 minute time window. 
A limit of 300 transactions per seconds on this type of queues is in place. FIFO queues in AWS also supports message grouping that allow to further partition messages in a FIFO queue based on a Group ID. Multiple producers can send messages to the same FIFO queue with the same group ID but by design only a single consumers is allowed to consume messages from that group ID.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# TOP APIs

API | Description
--- | ---
CreateQueue | Creates a new standard or FIFO queue.
DeleteQueue | Deletes the queue specified by the QueueUrl, regardless of the queue's contents. If the specified queue doesn't exist, Amazon SQS returns a successful response.
ListQueues | Returns a list of your queues (up to 1000).
SendMessage | Delivers a message to the specified queue.
SendMessageBatch | Delivers up to ten messages to the specified queue.
PurgeQueue |Deletes the messages in a queue specified by the QueueURL parameter. When you purge a queue, the message deletion process takes up to 60 seconds.

[*(back to the top)*](README.md#markdown-header-table-of-contents)