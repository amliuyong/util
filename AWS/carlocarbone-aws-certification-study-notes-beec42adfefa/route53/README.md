# Table of Contents

1. [Preface](README.md#markdown-header-preface)
2. [Supported Route53 DNS records](README.md#markdown-header-supported-route53-dns-records)
3. [The Alias record](README.md#markdown-header-the-alias-record)
4. [Routing Policies](README.md#markdown-header-routing-policies)

* * *

# Preface

A Domain Name System is a service that is used to translate human readable domain names into IP addresses (eg. www.website.com -> 10.1.34.23). A DNS can be seens as a huge database of names and their respective addresses. 

A domain name is made up of different strings separated by dots, the first string of a domain name, reading right to left is also known as *top level domain name*, the second string as *second level domain name* and so on. *Top level domain names* are controlled worldwide by IANA via a special root name database whilst domain names within a top level domain are assigned by Domain Registrars and must, of course, be unique (these are held in the WhoIS registry). 

**Route53 is the DNS solution provided by AWS and is Global a global Service.** Let's explore some key terminology:

A **DNS Zone** is a container of all the DNS records for a specific domain. For example: example.com, www.example.com, blog.example.com, and mail.example.com are four **DNS Records** inside a single **DNS Zone** for the example.com domain. Most DNS record requires at least three pieces of information: Record Name, Record Value/Data, Time to Live (TTL). 

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Supported Route53 DNS records

At the time of writing Route53 supports the follwoing DNS records.

## SOA, Start of Authority Record

A SOA-Record contains important management information about the zone, especially regarding the zone transfer. 

## A, IPv4 Address Record

This is the most basic type of DNS record and is used to point a domain, or subdomain, to an IPv4 address.

Name | TTL | Type | Data
--- | --- | --- | ---
www.example.com | 86400 | A | 192.168.1.100
mail.example.com | 86400 | A | 192.168.2.300

## AAAA, IPv6 Address Record

This is like an A record but for IPv6 addresses.

Name | TTL | Type | Data
--- | --- | --- | ---
www.example.com | 86400 | A | 2600:1800:5::9
mail.example.com | 86400 | A | 2600:1800:5::10

## CNAME, Canonical Name Record

These are used to point a domain or subdomain to another hostname.

Name | TTL | Type | Data (AliasTo)
--- | --- | --- | ---
docs.example.com | 86400 | CNAME | documents.example.com
docs.example.com | 86400 | CNAME | downloads.example.com

## MX, Mail Exchange Record

These are used to route email according the domain owners preference. The MX record itself specifies which server(s) to attempt to use to deliver mail to when this type of request is made to the domain. 

Name | TTL | Type | Priority | Data  
--- | --- | --- | --- | ---
example.com | 86400 | MX | 10 | mailhost1.example.com
example.com | 86400 | MX | 20 | mailhost2.example.com
example.com	| 86400 | MX | 30 | mailhost3.example.com

## NS, Name Server Record

These are used to delegate a subdomain to a set of name servers.

Name | TTL | Type | Data
--- | --- | --- | ---
example.com | 86400 | NS |ns1.example.com.
example.com | 86400 | NS | ns2.example.com.
example.com | 86400 | NS | ns3.example.com.
example.com | 86400 | NS | ns4.example.com.

## TXT, Text record

A TXT record is used to store any text-based information that can be grabbed when necessary.

Name | TTL | Type | Data (Text)
--- | --- | --- | ---
example.com | 86400 | TXT | ...
example.com | 86400 | TXT | ...

## NAPTR, Name authority pointer record

Used mostly in the internet telephony.

## PTR, Pointer record

PTR records are used for the Reverse DNS lookup. Using the IP address you can get the associated domain/hostname.

## SPF, Sender policy framework

Sender Policy Framework (SPF) records allow domain owners to publish a list of IP addresses or subnets that are authorized to send email on their behalf. The goal is to reduce the amount of spam and fraud by making it much harder for malicious senders to disguise their identity.

## SRV, Service locator

The SRV record is used to identify computers that host specific services. SRV resource records are used to locate for instance domain controllers for Active Directory.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# The Alias Record

In addition to the DNS records described above Amazon Route 53 offers a special type of DNS record called **Alias**. Alias records are used to map resource record sets in the DNS zone to the following AWS services:

- Amazon Elastic Load Balancing load balancers.
- Amazon CloudFront distributions.
- AWS Elastic Beanstalk environments.
- Amazon S3 buckets that are configured as websites. 

Alias records work like a CNAME record as customer can map one DNS name (www.example.com) to another 'target' DNS name (elb1234.elb.amazonaws.com). They differ from a CNAME record in that they are not visible to resolvers. Resolvers only see the A record and the resulting IP address of the target record. Also is not possible to create a CNAME record for naked domain name (a naked domain name for www.example.com is example.com) for that and A or Alias record must be used.

Alias record are needed because of the **elasticity** of the cloud infrastructure. In simple words the IP addresses associated to the DNS provided by the services  described above, think to an elastic load balancer DNS, is extremely dynamic, addresses keep changing in the backend, Alias records are capable of detecting those changes dynamically and keep in syncro the related DNS record. Another important difference with CNAMES is that requests to Alias are free whilst requests to CNAMES are subject to a charge.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Routing Policies

When customer create a resource record set a routing policy can be selected, this determines how Amazon Route 53 responds to DNS queries. Route53 supports several routing policies that will be explained below.

## Simple routing policy

This is the simplest routing policy available and is used when there is a a single resource that performs a given function for the selected domain, for example, a web server that serves content for the example.com website.

To create this type of policy the customer has to:

1. Create a new record-set.
2. Set the routing policy to Simple.
3. Select the resource to target. 

## Weighted routing policy

This is used when we want to route traffic to multiple resources in proportions that are specified by us, for example, 30% to an ELB in a region and the remaining 70% into another ELB located into another region. The traffic is distributed according the weights over a period of time, usually the day. This means that the behaviour over smaller time windows, like 30 minutes or 1 hour, is not deterministic.

To create this type of policy the customer has to:

1. Create a record-set for each resource to consume.
2. For each record-set set the routing policy type to Weighted.
3. Specify a value for the weight (this is an integer between 0 and 255). 

## Latency routing policy

This is used when we have resources in multiple locations and we want to route traffic to the resource that provides the best latency.

To cretae this type of policy the customer has to:

1. Create a record-set for each resource to consume.
2. For each record-set set the routing policy type to Latency.
3. Select the Region for the resource in the record-set.

## Failover routing policy

This is used when we want to configure active-passive failover. Route53 uses health-checks to determin if a resource is available and to decide where to direct the traffic.

To create this type of policy the customer has to :

1. Create a Route53 health check for the active resource to consume.
2. Create a record-set for the primary resource to consume.
3. Set the routing policy type to Failover.
4. Select the Primary radio button option to specify that the resource is our primary site.
5. Select the health check to use for monitoring the resource, configured in step 1.
6. Create another record-set for the secondary resource to consume.
7. Set the routing policy type to Failover.
8. Select the Secondary radio button option to specify that the resource is our secondary site and leave all the other options as default.

##Geolocation routing policy

This is used when we want to route traffic based on the location of your users.

To cretae this type of policy the customer has to:

1. Create a new record-set for the resource to consume.
2. Set the routing policy type to Geolocation.
3. Specify the Location that will be served by the resource in the record-set.
4. Iterate step 1 to 3 to add extra locations.

A default location is available for selection to cascade all the traffic that don't match the policies specified by the customer. 

[*(back to the top)*](README.md#markdown-header-table-of-contents)