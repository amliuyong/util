# Table of Contents

1. [Preface](README.md#markdown-header-preface)
2. [The Storage Gateway interfaces](README.md#markdown-headerthe-storage-gateway-interfaces)

* * *

# Preface

The AWS Storage Gateway service enables hybrid storage between on-premises environments and the AWS Cloud. It seamlessly integrates on-premises enterprise applications and workflows with Amazon’s block and object cloud storage services through industry standard storage protocols. AWS Storage Gateway supports three storage interfaces: file, volume and tape. Customers have two touchpoints to use the service: the AWS Management Console and a gateway virtual machine (VM). The process is as follow:

1. Sign-Up with the AWS Storage Gateway.
2. Download a gateway with a file, volume, or tape interface from the AWS Storage Gateway Management Console.
3. Install the gateway and associate it with the AWS Account through the AWS activation process.
4. After activation, configure the gateway to connect to the appropriate storage type. For file gateway, configure file shares that are mapped to selected S3 buckets, using IAM roles. For volume gateway, create and mount volumes as iSCSI devices. For tape gateway, connect your backup application to create and manage tapes.

Once configured, customer can start using the gateway to write and read data to and from AWS storage. Customer can also monitor the status of their data transfer and the storage interfaces through the AWS Management Console. Additionally, he can use the API or SDK to programmatically manage application’s interaction with the gateway.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# The Storage Gateway interfaces

## The file interface

File gateway provides a virtual file server, which enables to store and retrieve Amazon S3 objects through standard file storage protocols. File gateway allows  existing file-based applications or devices to use secure and durable cloud storage without needing to be modified. With file gateway, customer configured S3 buckets will be available as Network File System (NFS) mount points. Applications read and write files and directories over NFS, interfacing to the gateway as a file server. In turn, the gateway translates these file operations into object requests on the S3 buckets. Most recently used data is cached on the gateway for low-latency access, and data transfer between the data center and AWS is fully managed and optimized by the gateway. Once in S3, customer can access the objects directly or manage them using features such as S3 Lifecycle Policies, object versioning, and cross-region replication. File gateway can run on-premises or in EC2.

## The volume interface

Volume gateway provides an iSCSI target, which enables customer to create volumes and mount them as iSCSI devices from on-premises or EC2 application servers. The volume gateway runs in either a cached or stored mode.

- In the cached mode (legacy name is Gateway Cached Volumes), the primary data is written to S3, while retaining frequently accessed data locally in a cache for low-latency access.
- In the stored mode (legacy name is Gateway-Stored Volumes), primary data is stored locally and the entire dataset is available for low-latency access while asynchronously backed up to AWS.

In either mode, customer can take point-in-time snapshots of their volumes and store them in Amazon S3, enabling to make space-efficient versioned copies of volumes for data protection and various data reuse needs.

## The tape interface

Tape gateway (legacy name is Gateway Virtual Tape Library) presents customer backup application with a virtual tape library (VTL) interface, consisting of a media changer and tape drives. Customer can create virtual tapes in their virtual tape library using the AWS Management Console. Backup application can read data from or write data to virtual tapes by mounting them to virtual tape drives using the virtual media changer. Virtual tapes are discovered by customer backup application using its standard media inventory procedure. Virtual tapes are available for immediate access and are backed by Amazon S3. Is also possible to archive tapes in Amazon Glacier.

[*(back to the top)*](README.md#markdown-header-table-of-contents)