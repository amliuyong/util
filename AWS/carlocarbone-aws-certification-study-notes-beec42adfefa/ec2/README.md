# Table of Contents

1. [Preface](README.md#markdown-header-preface)
2. [Provisioning options](README.md#markdown-header-provisioning-options)
3. [Instance types](README.md#markdown-header-instance-types)
4. [Instance Store](README.md#markdown-header-instance-store)
5. [EBS Volumes](README.md#markdown-header-ebs-volumes)
6. [Snapshots](README.md#markdown-header-snapshots)
7. [EFS](README.md#markdown-header-efs)
8. [AWS CLI and SDK](README.md#markdown-header-aws-cli-and-sdk)
9. [Lambda](README.md#markdown-header-lambda)
10. [Elastic Load Balancer](README.md#markdown-header-elastic-load-balancer)
11. [User data](README.md#markdown-header-user-data)
12. [Instance Metadata](README.md#markdown-header-instance-metadata)
13. [Miscellaneous notes about the provisioning of EC2 instances](README.md#markdown-header-miscellaneous-notes-about-the-provisioning-of-ec2-instances)
14. [TOP APIs](README.md#markdown-header-top-apis)

* * *

# Preface

EC2 is a Regional AWS service that can be used to provision resizable compute capacity in the Cloud. 

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Provisioning options

EC2 supports the following provisioning options:

- **On Demand**. Resources are provisioned on demand and paid via a fixed-rate hourly schema. This schema is advised for short term, spiky and/or unpredictable workload applications that cannot be interrupted. Also this can be used for all the application that are being developed and tested on EC2 for the first time. AWS always charges an entire hour even if the instance is terminated before, only exception applies for spot instances (see below).
- **Reserved**. Resources are reserved and paid in advance for a certain amount of time (1 to 3 years). This option offers significant discount on the hourly charge schema and is advised for applications with steady state or predictable usage and also when payment upfront is possible.
- **Spot**. Allows to bid on the price of the compute capacity providing great saving on the hourly schema. Key terminology is as follow:
	- **Spot price**. Is the price of the instance in a specific time of the day.
	- **Bid price**. Is the price that the user wants to pay for allocating the computing capacity.
If the Spot price goes above the bid price then the instance is terminated automatically and  the partial hour not charged at all. If instead the user decides to terminate the instance on its own then the entire hour is charged. This schema is advised for urgent computing needs and for applications that have flexible start and end times.
- **Dedicated hosts**. In this case a physical EC2 server is dedicated for the application. This is advised for regulatory requirements that may not support multi tenant-virtualization (like government organisations that doesn't want to move fully in cloud). This option is good for licensing that doesn't support multi-tenancy scheme or cloud deployments. Dedicated instances can be purchased via an On-Demand or Reserved schema.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Instance types

EC2 instances are grouped into families and each family is named with a single alphabet letter followed by a number that represents the family generation.

Family | Description
--- | ---
D2 | Dense Storage Optimized instances
R4 | Memory optimized instances
M4 | General purpose instances
C4 | Compute Optimised instances
G2 | Graphic intensive instances
I2 | IO Intensive instances
F1 | Field programmable Gate instances
T2 | General purpose low cost instances
P2 | General purpose GPU instances
X1 | Memory Extreme Optimised instances

In orde to remember the family types the acronim DrMcGiftPx can be used. **These information are subject to change so always check the AWS FAQ for the latest updates.**

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Instance Store

EC2	instances can access storage from disks that are physically attached to the host computer, this type of storage is referred to as instance store (aka Ephemeral Store). Instance store provides temporary **block-level** storage for instances, it can be provisioned **only when the instance is launched** and **can't be detached from the instance** and attached to a different one. The data in an instance store persists only during the lifetime of its associated instance. If an instance reboots (intentionally or unintentionally), data in the instance store persists. However, data in the instance store is lost if:

- The underlying disk drive fails.
- The instance stops.
- The instance terminates.

The max size that can be allocated for an instance store is set to 10TB.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# EBS Volumes

EBS is a block based storage that can be used to create volumes that are attached to EC2 instances and used to mount File Systems. EBS volumes are resiliant AWS components, they comes with built-in redundancy, and lives into a specific AWS Availability Zone. EBS volumes are grouped into two main categories **SSD** and **Magnetic**, and those categories are classified as follow:

Type | Description
-- | --
SSD, General Purpose | For applications that uses up to 10.000 IOPS.
SSD, Provisioned IOPS | For applications that uses more than 10.000 IOPS up to 20.000.
Magnetic, Throughput Optimised | Optimised for big data, data warehousing and log processing. **This volume cannot be mounted as a boot volume.**
Magnetic, Cold HDD | Less frequently accessed data. **This volume cannot be mounted as a boot volume.**
Magnetic, Standard | Cheap used for less frequently accessed data. **Possibility to mount the volume as a boot volume**

EBS Volumes cannot be mounted to multiple EC2 instances at the same time for this purpose EFS should be used. Data stored on an Amazon EBS volume can persist independently of the life of the instance while the instance store doesn't. The max size that an EBS volume can have is currently set to 16TB.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Snapshots

Snapshots are point in time pictures of a specific Volume and are saved as incremental updates in S3. When a Snapshot is created is possible to create a new Volume based on the Snapshot and in this case is possible to change the Volume type. Snapshots taken from encrypted volumes are encrypted automatically and when the Snapshot is restored on a volume it will be again encrypted. 

Snapshots can be shared with other AWS accounts and published on the Marketplace but in case a Snapshot is published to the public it cannot be encrypted. Volumes 

Snapshots can be taken anytime so there is no need to shut down the underly EC2 instance.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# EFS

Elastic File System is a File Storage Service for EC2 instances that allows to have an elastic block storage capable of adapting its dimension to the application needs, this is different from EBS where the capacity has to be provisioned in advance, cost wise AWS only charges for the storage used. 

EFS volumes can scale up to Petabytes and also support the Network File System protocol (NFS) therefore can be attached to multiple EC2 instances at the same time. 

Data is stored in multiple Availability zones within a region and therefore service has a Read after Write consistency model. EC2 instances where we are planning to use the EFS file system must be in the same Security Group selected when the EFS was first created

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# AWS CLI and SDK

A set of APIs is also available to programmatically interact with the EC2 services. These APIs allows to automate the instance creation and all the common ops activities (like reboot, start and stop). APIs worth to mention are:

Command | Description
--- | ---
aws describe-images | To describe the images that are available to us for creating instances.
aws describe-instances | To describe the instances that are currently provisioned and running.
aws-run-instances | To launch a brand new EC2 instance.
aws start-instances | To start an instance that was previously stopped.
aws stop-instances | To stop an instance that is currently running.
aws terminate-instances | To terminate an instance that is currently running.

APIs can be consumed in several different ways. AWS provides a specific SDK for different type of languages, currently supported are: Android, iOS, Javascript, Java, .NET,Node.js, PHP, Python, Ruby, Go,C++. 

Default region for some SDK is US-EAST-1 

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Lambda

Is an AWS compute service that allow developers to upload and run their code in response of certain events without caring about the underlying server and software component architecture. Languages available are: Node.js, Java, Python, C#. First million request is free further million requests cost 0.20$ also function length and amount of memory processed is billed.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Elastic Load Balancer

ELBs are a virtual Appliance that are capable of distributing traffic between multiple EC2 instances. AWS supports 2 types of load balancers:

- Application Load Balancers that operates at application level (TCP/IP layer 7).
- Classic Load Balancer that operates at transport level (TCP/IP layer 4).

When a new classic ELB is created few information have to be provided:

- A VPC where the ELB will be provisioned.
- A Security Group.
- The Listener configuration where is possible to configure the external and internal port along with the protocol to use.
- Ping Protocol, Ping port and Ping Path. Represent respectively the protocol the port and the path that the ELB will use when checking if an instance is healthy or not.
- A Response Timeout. The amount of time to wait before timing-out (2 to 6 seconds).
- An Interval. The amount of time to wait between checks (5 to 30 second).
- An Unhealthy Threshold. The number of total checks to fail before marking the instance as Offline (2 to 10).
- An Healthy Threshold. The number of total checks to succeed before marking the instance as Online (2 to 10).
- The instances to use for Load Balancing.
- Tagging

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# User data

User data is an option available when launching a new EC2 instance and is used to provide a script that can be used to configure the instance during lunch time. 

**User data is executed only at launch** if customer stop an instance, modify the user data, and start the instance again, the new user data is not executed automatically.

User data is limited to 16 KB, this limit applies to the data in raw form, not base64-encoded form, and must be base64-encoded. The Amazon EC2 console can perform the base64 encoding on behalf of the client or accept base64-encoded input.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Instance Metadata

Every EC2 instace has some metadata that can be retrieved programmatically via the following URL.

```
http://169.254.169.254/latest/meta-data/
```

This URL is only accessible from the instance and not from the outside.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Miscellaneous notes about the provisioning of EC2 instances

1. AMIs are only valid in the Region where they have been first created.
2. Protect against accidental termination is set to off by default.
3. CloudWatch detailed monitoring is set to off by default.
4. Auto assign Public IP address oftion is set by default to "Use subnet settings".
5. Shutdown behaviour is set to by default to "Stop".
6. **As per [AWS Feb, 9 2017 note](https://aws.amazon.com/about-aws/whats-new/2017/02/new-attach-an-iam-role-to-your-existing-amazon-ec2-instance/) roles can now be attached and/or replaced on existing instances.**
7. The "Delete on termination" option for the root volume is set to true by default.
8. When a new EC2 instane is provisioned customer must either create a new access Key or select an existing one. This key is used to access the instance. When accessing via putty is important to remember that permission on the key file must be set to **400** otherwise an error will be shown. 

[*(back to the top)*](README.md#markdown-header-table-of-contents)
