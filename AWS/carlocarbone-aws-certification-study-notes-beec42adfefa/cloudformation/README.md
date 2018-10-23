# Table of Contents

1. [Preface](README.md#markdown-header-preface)
2. [Anathomy of a CloudFormation template](README.md#markdown-header-anathomy-of-a-cloudFormation-template)
3. [Intrinsic Functions](README.md#markdown-header-intrinsic-functions)
4. [Pricing](README.md#markdown-header-pricing)
5. [TOP APIs](README.md#markdown-header-top-apis)

* * *

# Preface

AWS CloudFormation is a service that gives developers a way to create a collection of AWS resources in an orderly and predictable fashion via a well defined scripting language that can be based on JSON or YAML.

In CloudFormation developers create templates that are then uploaded into S3 and executed via the console or via the APIs. When a template is started a new Stack is being create and the outcome of this operation can be one one of the following two:

- The stack succeeded, all the AWS resources described in the template were properly provisioned in AWS.
- The stack failed, something went wrong during the execution, all the resources provisioned as part of the stack execution are automatically removed (Rollback).

There is no limit on the total number of templates that developers can create however there is a soft limit on the total number of Stacks that can be executed, default value is 200 (this soft limit can be however changed raising a support request with AWS). When a new stack need to be executed the following actions are required:

- A unique stack name must be provided.
- A template is selected.
- If the template declares input parameters then these parameters must be provided by the user.
- The stack is being created and when finished the template output information are returned to the user.

For every already existing stack a users can also decide to:

- Delete the stack entirely, in this case all the resources provisioned during the creation are disposed.
- Update an existing stack, in this case CloudFrmation computes a **diff** between the new template and the resources already allocated in the Stack and only applies the difference.

CloudFormation has built-in capabilities to install software when a stack is created and also work in conjunction with Chef and Puppet.

The policy section of a CloudFormation template can also be used to define strategies to backup data when a stack is deleted.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Anathomy of a CloudFormation template

```
{
  "AWSTemplateFormatVersion" : "version date",

  "Description" : "JSON string",

  "Metadata" : {
    template metadata
  },

  "Parameters" : {
    set of parameters
  },

  "Mappings" : {
    set of mappings
  },

  "Conditions" : {
    set of conditions
  },

  "Transform" : {
    set of transforms
  },

  "Resources" : {
    set of resources
  },

  "Outputs" : {
    set of outputs
  }
}
```

- **Format Version** (optional). Specifies the AWS CloudFormation template version that the template conforms to. The template format version is not the same as the API or WSDL version. The template format version can change independently of the API and WSDL versions.
- **Description** (optional). A text string that describes the template. This section must always follow the template format version section. No default value is set if this field is not provided.
- **Metadata** (optional). Objects that provide additional information about the template.
- **Parameters** (optional). Specifies values that you can pass in to your template at runtime (when you create or update a stack). You can refer to parameters in the Resources and Outputs sections of the template.
- **Mappings** (optional). A mapping of keys and associated values that you can use to specify conditional parameter values, similar to a lookup table. You can match a key to a corresponding value by using the Fn::FindInMap intrinsic function in the Resources and Outputs section.
- **Conditions** (optional). Defines conditions that control whether certain resources are created or whether certain resource properties are assigned a value during stack creation or update. For example, you could conditionally create a resource that depends on whether the stack is for a production or test environment.
- **Transform** (optional). For serverless applications (also referred to as Lambda-based applications), specifies the version of the AWS Serverless Application Model (AWS SAM) to use. When you specify a transform, you can use AWS SAM syntax to declare resources in your template. The model defines the syntax that you can use and how it is processed. You can also use the AWS::Include transform to work with template snippets that are stored separately from the main AWS CloudFormation template. You store your snippet files in an Amazon S3 bucket and then reuse the functions across multiple templates.
- **Resources** (required). Specifies the stack resources and their properties, such as an Amazon Elastic Compute Cloud instance or an Amazon Simple Storage Service bucket. You can refer to resources in the Resources and Outputs sections of the template.
- **Outputs** (optional). Describes the values that are returned whenever you view your stack's properties. For example, you can declare an output for an S3 bucket name and then call the aws cloudformation describe-stacks AWS CLI command to view the name.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Intrinsic Functions

Intrinsic functions in CloudFormation help developers to assign values to template properties that are not available until runtime, these can only be used in specific parts of a template: resource, outputs, metadata, and update policy attributes.
Top Intrinsic functions to remember:

Function | Description | Syntax
--- | --- | ---
Fn::FindInMap | **Returns the value corresponding to keys in a two-level map that is declared in the Mappings section.** | ``` { "Fn::FindInMap" : [ "MapName", "TopLevelKey", "SecondLevelKey"] } ```
Fn::GetAtt | **Returns the value of an attribute from a resource in the template.** | ``` { "Fn::GetAtt" : [ "logicalNameOfResource", "attributeName" ] } ```
Fn::GetAZs | Returns an array that lists Availability Zones for a specified region. | ``` { "Fn::GetAZs" : "region" } ```
Fn::Join | Appends a set of values into a single value, separated by the specified delimiter. If a delimiter is the empty string, the set of values are concatenated with no delimiter. | ``` { "Fn::Join" : [ "delimiter", [ comma-delimited list of values ] ] } ```
Fn::Ref | **Returns the value of the specified parameter or resource.** When you specify a parameter's logical name, it returns the value of the parameter. When you specify a resource's logical name, it returns a value that you can typically use to refer to that resource, such as a physical ID. | ```{ "Ref" : "logicalName" }```

**Example**: Registration of a LoadBalancer DNS with a Route53 domain. As at the time the developer creates the template the LodBalancer **DNSName** is not yet assigned an intrinsic function, Fn:GetAtt in this case, can be used as placeholder to reference the value of the **DNSName** output property that is obtained once the AWS resource is created.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Pricing

From a pricing perspective the AWS CloudFormation service is free of charge, user only pays the resources that are provisione as part of a stack creation. 

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# TOP APIs

Api | Description
--- | ---
CreateStack | Creates a stack as specified in the template. After the call completes successfully, the stack creation starts. You can check the status of the stack via the DescribeStacks API.
DeleteStack | Deletes a specified stack. Once the call completes successfully, stack deletion starts. Deleted stacks do not show up in the DescribeStacks API if the deletion has been completed successfully.
UpdateStack | Updates a stack as specified in the template. After the call completes successfully, the stack update starts. You can check the status of the stack via the DescribeStacks action.
DescribeStacks | Returns the description for the specified stack; if no stack name was specified, then it returns the description for all the stacks created. If the stack does not exist, an **AmazonCloudFormationException** is returned.
ListStacks | Returns the summary information for stacks whose status matches the specified StackStatusFilter. Summary information for stacks that have been deleted is kept for 90 days after the stack is deleted. If no StackStatusFilter is specified, summary information for all stacks is returned (including existing stacks and stacks that have been deleted).
ListStackResources | Returns descriptions of all resources of the specified stack. For deleted stacks, ListStackResources returns resource information for up to 90 days after the stack has been deleted.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Command line
Command                            | Description
---                                     | ---
aws cloudformation create-stack         | Creates a new Stack from a given template.
aws cloudformation list-stacks          | Get a list of any of the stacks created (even those which have been deleted up to 90 days).
aws cloudformation describe-stacks      | Get information on your running stacks.
aws cloudformation list-stack-resources | Lists a summary of each resource in the specified stack.
aws cloudformation delete-stack         | Deletes a runnig stack.