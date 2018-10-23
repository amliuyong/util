# Table of Contents

1. [Preface](README.md#markdown-header-preface)
2. [Types of Snowball](README.md#markdown-header-types-of-snowball)

* * *

# Preface

Snowball is a petabyte-scale data transport solution that uses secure **appliances** to transfer large amounts of data into and out of the AWS cloud. 

With Snowball, customer don’t need to write any code or purchase any hardware to transfer data, they simply create a job in the AWS Management Console and a Snowball appliance is automatically shipped to him. Once it arrives, customer attaches the appliance to the local network, downloads and run the Snowball client to establish a connection, and then use the client to select the file directories that need to transfer to the appliance. The client will then encrypt and transfer the files to the appliance at high speed. Once the transfer is complete and the appliance is ready to be returned, the E Ink shipping label will automatically update and customer can track the job status via Amazon Simple Notification Service (SNS), text messages, or directly in the Console.

**When the appliace is returned to AWS, and data integrated in the cloud, phisical disk in the Snowball are securely formatted so that other customers can't access others data.**

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Types of Snowball

AWS supports three types of Snowball solutions:

1. **Simple**, this is the basic appliance that the customer can request and allows to store up to 80TB worth of data.
2. **Edge**, this appliance is an enhancment of the Simple solution and allows to store up to 100TB worth of data with the possibility to execute computing operation via Lambda functions.
3. **SnowMobile**, this solution if for very large exabyte-scale data transports and allows to store up to 100PB worth of data. In this case the hardware appliance is represented by an entire data centre installed on a truck.

[*(back to the top)*](README.md#markdown-header-table-of-contents)