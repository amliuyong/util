# Table of Contents

1. [Preface](README.md#markdown-header-preface)
2. [IAM Entities](README.md#markdown-header-iam-entities)
3. [Policy Evaluation Logic](README.md#markdown-header-policy-evaluation-logic)
4. [Sign-in URL](README.md#markdown-header-sign-in-url)
5. [Limits](README.md#markdown-header-limits)

* * *

# Preface

IAM is a Global AWS service that is being used to manage all the aspects that revolve around the security of the AWS Cloud platform. The AWS platform is PCI-DSS compliant (a standard for handling secure Credit Card transactions) and also supports Multi-Factors Authentication. The main entities that can be managed through IAM are: Users, Groups, Roles and Policies.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# IAM Entitites

Below an explaination of each individual IAM entity.

## Users

Users represents the main identities that access, and use the AWS platform, this includes normal users and applications. When a new user account is created the Administrator can choose if:

- The access is restricted to the Management Console.
- The access is restricted to the APIs (programmatic access). 
- Both access types are granted.

For programmatic access a new pair Access key and Secret key must be generated and these credentials can be downloaded locally as a CSV file. AWS doesn't keep a copy of these information so in case these gets lost the only option left to the Administrator is to regenerated them from scratch (and this may impat the application that's using it).

Access key and Secret key can only be used for programmatic access (APIs) whilst username and password for accessing the services via the AWS console.

The root account is the main AWS account, created by AWS, with full Administration privileges. AWS recommends not to use this account for the daily AWS operation but instead provision ad-hoc identities.

## Groups

Groups in AWS represents collection of identities that share the same set of permissions.

## Roles

An IAM role is an IAM entity that defines a set of permissions for making AWS service requests. Roles are not associated with a specific user or group but instead are **Assumed** by trusted entities such as IAM users, applications, or other AWS services (eg. EC2, S3, etc.). Roles can be assumed by calling the AWS Security Token Service (STS) **AssumeRole** APIs (or alternatively in case of Federated access with **AssumeRoleWithWebIdentity** and **AssumeRoleWithSAML**). These APIs return a set of temporary security credentials that applications can use to sign requests to AWS service APIs.

**IAM roles allow the customer to delegate access with defined permissions without having to share long-term access keys. **

## Policies

Policies are JSON documents that explicitly lists permissions to AWS resource, in its basic form a policy allows the customer to specify the following:

- **Actions**, what actions will be allowed/ or denied. Each AWS service has its own set of actions (eg. allow a user to use the Amazon S3 ListBucket action).
- **Resources**, which resources the customer will allow/ deny the action on (eg. a specific Amazon S3 buckets).
- **Effect**, what the effect will be when the user requests access, this can be **Allow** or **Deny**. The default is that all resources are denied

A policy document can have of one or more **statements**, each of which describes one set of permissions, each statement can be provided with a **Condition** block (optional). The policy header with the version is OPtional as well. 

```
{
	"Version": "2012-10-17",
	"Statement": [ 
		{
			"Sid": "firstStatement",
    		"Effect": "Allow",
    		"Action": "s3:ListBucket",
    		"Resource": "arn:aws:s3:::example_bucket"
  		},
 		{
   			"Sid": "firstStatement",
    		"Effect": "Allow",
    		"Action": [
      			"s3:List*",
        		"s3:Get*"
    		],
    		"Resource": [
      			"arn:aws:s3:::confidential-data",
      			"arn:aws:s3:::confidential-data/*"
    		],
    		"Condition": {"Bool": {"aws:MultiFactorAuthPresent": "true"}}
 		} 
	]
}
```

IAM policies can be of two types:

- **Managed policies**. Standalone policies that can be attached to multiple users, groups, and roles in an AWS account, these only apply to identities (users, groups, and roles) not resources and never include in their statements a Principal section. Customer can use two types of managed policies:

	- **AWS managed policies**. These are created and managed by AWS.

	- **Customer managed policies**. These are created by the customerand allow to have more precise over AWS managed policies.

- **Inline policies**. Policies that customer create and manage, and that are embedded directly into a single user, group, or role. In simple words an inline policy includes a Principal section.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Policy Evaluation Logic

For security reasons AWS follows an Explicit-Allow rule meaning that all the Actions are always denied by default and need to be explicitly allowed via Policies. Sometimes rules in a policy can conflict, in this case the following process gets used to determine if allow or not the Action to the resource:

1. Decision starts at **Deny**
2. Evaluate all applicable policies
3. Is there an explicit **Deny** ?
	1. Yes, then final decision is **Deny** (explicit Deny)
	2. No, then is there an **Allow** ?
		1. Yes, then final Decision is **Allow**
		2. No, then Final decision is **Deny**

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Sign-in URL

The Sign-in URL is used for accessing the AWS console, the URL has the following form:

https://<account_id>.signin.aws.amazon.com/console

To simplify access is also possible to set-up an Alias URL, in addition to the canonical one stated above, but is important to remember that the Alias must be unique within the AWS domain. 

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Federated Authentication

If customer already manage user identities outside of AWS, he can use IAM identity providers instead of creating IAM users in their AWS account. With an identity provider (IdP), customer can manage user identities outside of AWS and give these external user identities permissions to use AWS resources in the AWS account. Customer can choose between three methods:

1. **SAML based Federation**. This is used for integrating with Active Directory and other IdPs that support the SAML standard. Authentication flow works as follow:
	1. User authenticates using the external IdP.
	2. The IdP checks the user credentials towards its user store.
	3. The IdP generates a SAML assertion (XML format) that the browser posts to the AWS SAML sign-in page **https://signin.aws.amazon.com/saml**
	4. IAM validates the SAML assertions and uses the STS API **AssumeRoleWithSAML** to generate a set of temporary credentials to hand over to the user.
	5. User uses the temporary credentials to sign service requests.
2. **Web Identity Federation**. This is used for integration with Social logins (Facebook, Linkedin etc. etc.). Authentication flow works as follow:
	1. User authenticates with the external IdP (let's pretend Facebook).
	2. The IdP checks the user credentials towards its user store.
	3. The IdP generates a pair UserId and AccesToken.
	4. The pair produced in step3 is then posted to the **AssumeRoleWithWebIdentity** API endpoint to obtain a set of temporary credentials (Access Key Id, Access key Secret and Session Token).
	5. User uses the temporary credentials to sign service requests.
3. **Custom identity broker**. This is when the user wants to implement its own authentication mechanism. Authentication flow works as follow:
	1. The user implements an Identity broker that authenticates the users against its identity store.
	2. The identity broker upon user authentication uses internally the AssumedRole APpi to genertae a set of temporary credentials to hand over to the user.
	3. User uses the temporary credentials to sign service requests.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Limits

Few limits to remember:

- No more than 5000 users per account.	
- No more than 100 Groups per account.
- No more than 500 Roles per account.