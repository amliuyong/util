# Table of Contents

1. [Preface](README.md#markdown-header-preface)
2. [Supported Platforms](README.md#markdown-header-supported-platforms)
3. [Managed Platform Updates](README.md#markdown-header-managed-platform-updates)
4. [Limits](README.md#markdown-header-limits)

* * *

# Preface

Elastic Beanstalk is an AWS service that allows to quickly deploy and manage applications in the AWS Cloud without worrying about the infrastructure that runs those applications.
AWS Elastic Beanstalk reduces management complexity without restricting choice or control.The Main elements that can be provisioned via AWS Elastic Beanstalk are:

- Operating system
- Application stack (see next paragraph for the supported platforms)
- Database and Storage
- Availability Zone, for multi AZ-Deployment
- Load Balancer

Once the application stack is created the customer can:

- Login access to Amazon EC2 instances for troubleshooting.
- Access CloudWatch monitoring and configure the system to and get notifications on application health and other important events.
- Adjust application server settings (e.g., JVM settings) and pass environment variables
- Run other application components, such as a memory caching service, side-by-side in Amazon EC2
- Access log files without logging in to the application servers

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Supported Platforms

Following platforms are currently being supported by AWS BeansTalk:

- Apache Tomcat for Java applications
- Apache HTTP Server for PHP applications
- Apache HTTP Server for Python applications
- Nginx or Apache HTTP Server for Node.js applications
- Passenger or Puma for Ruby applications
- Microsoft IIS 7.5, 8.0, and 8.5 for .NET applications
- Java SE
- Docker
- Go

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Managed Platform Updates

Customers can opt-in to have theirs AWS Elastic Beanstalk environments automatically updated to the latest version of the underlying platform running the application during a specified maintenance window.
However this option must be explicitly enable in the Configuration tab of the Elastic Beanstalk console (managed platform updates option, also possible via CLI and SDK). After setting has been enabled the customer can configure which types of updates to allow and when updates can occur (maintenance window).
A maintenance window is a weekly two-hour-long time slot during which AWS Elastic Beanstalk will initiate platform updates if managed platform updates is enabled and a new version of the platform is available.

Platforms are versioned using the pattern: MAJOR.MINOR.PATCH (e.g., 2.0.0). Each portion is incremented as follows:

- MAJOR version when there are incompatible changes.
- MINOR version when there is additional functionality added in a backward-compatible manner.
- PATCH version when there are backward-compatible bug fixes.

Managed platform updates are applied using an immutable deployment mechanism that ensures that no changes are made to the existing environment until a parallel fleet of Amazon EC2 instances, with the updates installed, is ready to be swapped with the existing instances, which are then terminated. In addition, if the Elastic Beanstalk health system detects any issues during the update, traffic is redirected to the existing fleet of instances, ensuring minimal impact to end users of your application.
Major version updates can be applied at any time using the AWS Elastic Beanstalk management console, API, or CLI, the following options are available:

- Apply the update in-place on an existing environment.
- Create a clone of an existing environment with the new platform version.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Limits
Customer can create up to 75 applications and 1,000 application versions. By default, is possible to run up to 200 environments across all the applications.

[*(back to the top)*](README.md#markdown-header-table-of-contents)
