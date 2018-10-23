# Table of Contents

1. [Preface](README.md#markdown-header-preface)
2. [Design considerations](README.md#markdown-header-design-considerations)
3. [Storage classes](README.md#markdown-header-storage-classes)
4. [Multipart Upload](README.md#markdown-header-multipart-upload)
5. [Static website hosting and CORS](README.md#markdown-header-static-website-hosting-and-cors)
6. [Security and Encryption](README.md#markdown-header-security-and-encryption)
    * [IAM policies](README.md#markdown-header-iam-policies)
    * [S3 bucket policies](README.md#markdown-header-s3-bucket-policies)
    * [ACLs](README.md#markdown-header-acls)
    * [Encryption](README.md#markdown-header-encryption)
7. [Pre Signed URLs](README.md#markdown-header-pre-signed-urls)
8. [Versioning](README.md#markdown-header-versioning)
9. [Cross Region Replication](README.md#markdown-header-cross-region-replication)
10. [Lifecycle management](README.md#markdown-header-lifecycle-management)
11. [Transfer Acceleration](README.md#markdown-header-transfer-acceleration)
12. [Logging](README.md#markdown-header-logging)

* * *

# Preface

S3 is a Regional AWS service that implements an object store in the Cloud where users can save their files. Objects are uploaded in remote containers, called **Buckets**, and every bucket is provided with a name that must be unique in the AWS domain, this is because AWS uses an universal namespace.
Bucket names must use the following naming convention:

- The name can be between 3 and 63 characters long, and can contain only lower-case characters, numbers, periods, and dashes.
- Each label in the bucket name must start with a lowercase letter or number.
- The bucket name cannot contain underscores, end with a dash, have consecutive periods, or use dashes adjacent to periods.
- The bucket name cannot be formatted as an IP address.

A unique key, that can be constructed to mimic a hierarchical structure, has to be provided when new files are uploaded these keys are also needed for object retrieval. 
Amazon S3 supports both virtual-hosted–style and path-style URLs to access a bucket.

In a virtual-hosted–style URL, the bucket name is part of the domain name in the URL. For example:  
```
http://bucket.s3.amazonaws.com
http://bucket.s3-aws-region.amazonaws.com.
```
In a path-style URL, the bucket name is not part of the domain (unless you use a region-specific endpoint).

```
https://s3-<region>.amazonaws.com/<bucket name>
```

Users can provision up to 100 buckets per AWS account, **this is a soft limit can be changed via AWS support**, and the size of a single file can vary between 0 bytes and 5TB, **this is an hard limit and cannot be changed**.
There is no formal limit on the number of files and/or total size that a bucket can have also, for files exceeding 100 MBytes, the Multi Part upload must be used (see later for a detailed description).

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Design considerations

Objects in S3 are made of: a key, a value (the bytes representing the file), a Version id (optional), some metadata and some sub-resources (which includes: Access Control Lists and Torrent, used to support the bit-torrent protocol).

**Amazon S3 in all Regions provide a read-after-write consistency for PUTS of new objects and eventual consistency for overwrite PUTS and DELETES. ** This basically means that when a new object is uploaded that becomes immediately available for consumption whilst when an object is deleted, or updated, some time is necessary for the changes to become effective, this is because changes have to be propagated over the distributed backend.
A successfully Operation in S3 always returns the HTTP 200 response code.

S3 has been designed to follow the lexicographic order of the keys when persisting and retrieving objects, this has an important impact on overall performance. When the number of operation for a specific bucket is intensive AWS can decide to create partitions over the object keys to allow a faster access to the information, in this case if the key used to persist the objects follow a sequential order there is an high probability that the partition is always the same and this may degrade the overall performances. To avoid this issue some level of randomness should be added to the key string when persisting objects.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Storage classes

S3 support different types of storage classes, 4 at the time of writing, each class provides different options and has different cost:

Storage Type              | Availability               | Durability                           | Description
---                       | ---                        | ---                                  | ---
Standard                  | 99.99% Availability        | 99.99999999999% Durability (11 x 9s) | Objects are always replicated for redundancy to other AWS facilities and are capable to sustain the loss of 2 facilities concurrently.
Infrequent Access         | 99.9% Availability         | 99.99999999999% Durability (11 x 9s) | **(1)** Objects are always replicated for redundancy to other AWS facilities and are capable to sustain the loss of 2 facilities concurrently.  **(2)** A fee applies for object retrieval. **(3)** Cheaper than Standard but only use if the access to information is infrequent. |
Reduced Redundant Storage | 99.99% Availability        | 99.99% Durability                    | **(1)** Able to sustain the loss of 1 facility. **(2)** Cheaper service but only use when is always possible to re-create the information from scratch.
Glacier                   | 99.99999999999% Durability | N/A                                  | **(1)** Cheaper service but no SLA is guaranteed and object retrieval may take from 4 to 5 hours in total.

The pricing model for S3 services charges the following elements: storage, number of requests, storage management, data-transfer and transfer-acceleration (through Content Delivery Network aka CDN).

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Multipart Upload

The Multipart upload API enables developers to upload large objects in parts. This API can be used to upload new large objects or make a copy of an existing object.
Multipart uploading is a three-step process: initiation, parts upload, completion.

Upon receiving the complete multipart upload request, Amazon S3 constructs the object from the uploaded parts, and developers can then access the object usual. A list of all in-progress multipart uploads can be requested. The multipart API supports the stop and resume mechanism.
The maximum size of the payload that a single PUT request can handle with the AWS S3 APIs is set to 5GB.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Static website hosting and CORS

S3 can be used to host a static website enabling an option that can be set at bucket-level. When the Static websire option is enabled the following URL is generated:

```http://<bucket_name>.s3-website.<region>.amazonaws.com```

If a static website hosted in an S3 buckets tries to access resources into another bucket via Javascript, by default, an error is returned as S3 doesn't allow Cross Origin Requests. There is however the possibility to enable the CORS option in the target bucket to allow a specific domain to access the bucket resources.

Instead of accessing the website by using an Amazon S3 website endpoint, customers can use their own domains, such as example.com to serve your content via Amazon Route 53, however this option is only suported at the root domain (example.com and www.example.com).

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Security and Encryption

From a Security perspective two aspects must be taken into consideration:

 1. How documents get accessed by users.
 2. How information are persisted and maintained at rest and when are in transit over the network.

By default all the newly created buckets are **PRIVATE** by default, administrator can then use **IAM policies**, **Bucket policies** ad **ACLs** to control access to content from other users, roles and groups.
**Bucket ownership cannot be transferred**.

## IAM policies

Specify what actions are allowed or denied on what AWS resources, these can then be attach to IAM users, groups, or roles, which are then subject to the permissions defined.

```
{
    "Version": "2012-10-17",
    "Statement":[{
        "Effect": "Allow",
        "Action": "s3:*",
        "Resource": ["arn:aws:s3:::my_bucket","arn:aws:s3:::my_bucket/*"]
    }]
}
```

**Notice that in an IAM policy the Principal is not specified as is the policy itself that gets attached via IAM to the user, role and or group.**

## S3 bucket policies

These policies are attached only to S3 buckets and specify what actions are allowed or denied for which principals on the bucket that the policy is attached to. **The permissions specified in the bucket policy apply to all the objects in the bucket.**

```
{
    "Version":"2012-10-17",
    "Statement":[{
    "Effect":"Allow",
        "Principal": "arn:aws:iam::111122223333:user/Carlo"},
        "Action": "s3:*",
        "Resource": ["arn:aws:s3:::my_bucket","arn:aws:s3:::my_bucket/*"]
    }]
}
```

**Notice that in a Bucket policy the Principal is mandatory.**

## ACLs

ACLs are sub-resources that are attached to every S3 bucket and object and defines which AWS accounts or groups are granted access and the type of access. Under certain circumstances ACLs might better meet user needs as allow to manage permissions on individual objects within a bucket (more granular control). As a general rule, AWS recommends using S3 bucket policies or IAM policies for access control. S3 ACLs is a legacy access control mechanism that predates IAM.

## Encryption

- **Encryption in transit**. That is when the file moves over the network from one point to another, S3 always encrypts file in transit using the HTTPS protocol.
- **Encryption at rest**. Which refers to the file persisted on the AWS servers. In this case S3 supports four types of mechanisms:
    - **Server Side**
        - **SSE-S3**. Amazon handles key management and key protection using multiple layers of security. This option has to be chosen when is acceptable to have Amazon managing keys.
        - **SSE-C**. Amazon S3 performs the encryption and decryption of the objects while the user **retains control of the keys used to encrypt objects**. With SSE-C users  don�t need to implement or use a client-side library to perform the encryption and decryption of objects but **are in charge of managing the keys that are used to encrypt and decrypt objects**.
        - **SSE-KMS**. AWS Key Management Service (AWS KMS), similare to SS3-S3 but with extra level of security built in. A master key envelop is introduced to further protect the encryption keys and Cloud trail to audit actions. AWS KMS is also integrated with other AWS services including: EBS, s3, Amazon Redshift, Amazon Elastic Transcoder, Amazon WorkMail,RDS,.
    - **Client Side**. Encryption is fully  managed client side using specific encryption libraries.

**A bucket can also either contain encrypted an unencrypted content. To specify that a file has to be encrypted at rest the **x-amz-server-side-encryption** header can be used when uploading the object.**
**AES-256 is used for content encryption.**

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Pre Signed URLs

As said all objects by default are private and only the object owner has permission to access these objects.  However, the object owner can optionally share objects with others by creating a pre-signed URL, using their own security credentials, to grant time-limited permission to download the objects.
When a pre-signed URL is created for an object, following information must be provide:
- The security credentials
- A bucket name and an object key
- The HTTP method (GET to download the object)
- The expiration date and time.

The pre-signed URLs are valid only for the specified duration. Anyone who receives the pre-signed URL can then access the object.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Versioning

Versioning is a bucket-level feature that allows to keep multiple variants of an object in the same bucket and can be used to preserve, retrieve, and restore every version of every object stored in an Amazon S3 bucket.
**Once a bucket is version-enable, it can never return to an un-versioned state, however is possible to suspend versioning on that bucket.**

The versioning state applies to all of the objects in a version-enable bucket. However is important to remember that objects stored in the bucket **before** enabling the versioning have an initial version ID of null. When versioning is enabled existing objects in the bucket do not change what instead changes is how Amazon S3 handles the objects in future requests.
When an object is deleted into a version-enabled bucket a delete marker is created and the object is not visible anymore, upon deletion of the delete marker the object would be again visible in S3.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Cross Region Replication

Cross-region replication is a bucket-level feature that enables automatic, asynchronous copying of objects across buckets in **different AWS regions**. In the Cross Region Replication users must decide which bucket has to be replicated and must provide the destination bucket where they want the objects replicated to. A subset of objects with specific key name prefixes can be selected as well. The object replicas in the destination bucket are exact replicas of the objects in the source bucket, they have the same key names and the same metadata. Amazon S3 encrypts all data in transit across AWS regions using SSL during content replication. In order to enable CRR following pre-requisites must be met:

- The source and destination buckets must be versioning-enabled.
- The source and destination buckets must be in different AWS regions.
- Objects can be replicated from a source bucket to only one destination bucket.
- Amazon S3 must have permission to replicate objects from that source bucket to the destination bucket on your behalf. These permissions can be granted by creating an IAM role that Amazon S3 can assume.

Is also important to remember that:

- Any new objects created after replication is configured will me migrated whilst objects existing prior the replication configuration are not migrated by default.
- If the objects created is encrypted then the replicated copy of the object is also encrypted using server-side encryption using the Amazon S3-managed encryption key.
- Amazon S3 replicates only objects in the source bucket for which the bucket owner has permission to read objects and read ACLs.
- Any object ACL updates are replicated, although there can be some delay before Amazon S3 can bring the two in sync.
- S3 replicates object tags, if any.
- If a DELETE request is made without specifying an object version ID, Amazon S3 adds a delete marker, which cross-region replication replicates to the destination bucket.
- If a DELETE request specifies a particular object version ID to delete, Amazon S3 deletes that object version in the source bucket, but it does not replicate the deletion in the destination bucket (in other words, it does not delete the same object version from the destination bucket).
- Upon restoration of a deletion marker on the source bucket the deletion action doesn't get cascaded to the destination bucket so the object stays marked as deleted.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Lifecycle Management

Lifecycle configuration enables users to specify the lifecycle management of objects in a bucket. The configuration is a set of one or more rules, where each rule defines an action for Amazon S3 to apply to a group of objects. These actions can be classified as follows:

- **Transition actions**. In which users define when objects transition to another storage class. For example, you may choose to transition objects to the STANDARD_IA (IA, for infrequent access) storage class 30 days after creation, or archive objects to the GLACIER storage class one year after creation.
- **Expiration actions**. In which you specify when the objects expire. Then Amazon S3 deletes the expired objects on your behalf.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Transfer Acceleration
 
Transfer Acceleration is a bucket level feature that enables fast, easy, and secure transfers of files over long distances between clients and an S3 bucket taking advantage of the Amazon CloudFront’s globally distributed edge locations network. As the data arrives at an edge location, data is routed to Amazon S3 over an optimized network path. 
When using Transfer Acceleration, additional data transfer charges may apply also in addition to the canonical S3 bucket URL a new accelerated URL is created as follow:

```
bucketname.s3-accelerate.amazonaws.com
```

In order to take advantages of the acceleration feature this new URL must be used by the client applications. Is also important to remember that **the name of the bucket used for Transfer Acceleration must be DNS-compliant and must not contain periods (".")**.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Logging

Logging is a bucket level feature that allows to track all the request made to a bucket. Each access log record provides details about a single access request such as:

- The requester.
- The name of the bucket.
- The request time.
- The request action.
- The response status.
- The error code if any.

To enable access logging user must:

1. Turn on the log delivery feature on the bucket for which the user wants Amazon S3 to track the access logs.
2. Specify the name of the target bucket where Amazon S3 save the access logs as objects.
2. Grant the Amazon S3 Log Delivery group write permission on the bucket where logs are saved.

[*(back to the top)*](README.md#markdown-header-table-of-contents)