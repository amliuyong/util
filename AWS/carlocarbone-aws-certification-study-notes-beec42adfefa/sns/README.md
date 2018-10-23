# Table of Contents

1. [Preface](README.md#markdown-header-preface)
2. [Subscription mechanism](README.md#markdown-header-subscription-mechanism)
3. [Billing](README.md#markdown-header-billing)
4. [Anathomy of an SNS notification](README.md#markdown-header-anathomy-of-an-sns-notification)
5. [Message attributes](README.md#markdown-header-message-attributes)
6. [Mobile Push](README.md#markdown-header-mobile-push)
7. [TOP APIs](README.md#markdown-header-top-apis)

* * *

# Preface

SNS is an AWS Regional service provided by AWS that can be used to send notifications. SNS uses **Topics** as the main message channels and a push mechanism to deliver notifications to topic subscribers.
This means that subscribers are notified in real time and don't have to check for new messages like in SQS. Supported transport protocols at the time of writing for SNS are:

- HTTP & HTTPS.
- Email & Email JSON.
- SQS.
- SMS, Mobile Push.
- Lambda.

Users can provision up to 100,000 topics with up to 10 million subscribers per topic, this limit can be changed via AWS support. Topic name can also only contain up to 256 characters in total.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Subscription mechanism

When a new subscriber decides to join a topic its subscription has to be first confirmed, a Token is always included in the subscription request and is valid for **3 days**, multiple subscribers are allowed on the same topic and all of them will receive the notification upon confirmation.
Subscription confirmations works differently based on the transport protocol chosen:

- For HTTP/HTTPS notifications, Amazon SNS will first POST the confirmation message (containing a token) to the specified URL. The application monitoring the URL will have to call the **ConfirmSubscription** API with the token included in the request.
- For Email and Email-JSON notifications, Amazon SNS will send an email to the specified address containing an embedded link. The user will need to click on the embedded link to confirm the subscription request.
- For SQS notifications, Amazon SNS will enqueue a challenge message containing a token to the specified queue. The application monitoring the queue will have to call the **ConfirmSubscription** API with the token.

When a new message is sent on a topic the user can decide to customise the message format for a specific transport or let AWS manage the format. The reason why SNS support either Email and Email JSON is because the first is meant to be used by end-users whilst the second by Applications.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Billing

With Amazon SNS, there is no minimum fee and you pay only for what you use based on the transport chosen. Cost can be seens on the AWS website. With the exception of SMS messages, Amazon SNS messages can contain up to 256 KB of text data, including XML, JSON and unformatted text.
Each 64KB chunk of published data is billed as 1 request. For example, a single API call with a 256KB payload will be billed as four requests.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Anathomy of an SNS notification

# | Description
--- | ---
Type | The type of message. For a subscription confirmation, the type is SubscriptionConfirmation.
MessgaeId | A Universally Unique Identifier, unique for each message published.
TopicArn | The Amazon Resource Name (ARN) for the topic that this message was published to.
Subject | The Subject parameter specified when the notification was published to the topic. Note that this is an optional parameter. If no Subject was specified, then this name/value pair does not appear in this JSON document.
Message | The Message value specified when the notification was published to the topic.
Timestamp | The time (GMT) when the subscription confirmation was sent.
SignatureVersion | Version of the Amazon SNS signature used.
Signature | Base64-encoded "SHA1withRSA" signature of the Message, MessageId, Type, Timestamp, and TopicArn values.
SignatureCertURL | The URL to the certificate that was used to sign the message.
UnsubscribeURL | A URL that you can use to unsubscribe the endpoint from this topic.
MessageAttributes | Attributes attached to the notification (mostly for mobile push).

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Message attributes

Message attributes are used to further enrich the messages delivered to Amazon SQS endpoints via SNS. These attributes are optional, separated from, but sent along with, the message body and restricted in number (up to 10 attributes per message).
Each message attribute consists of three main elements:
- A Name. The message attribute name can contain the following characters: A-Z, a-z, 0-9, underscore(_), hyphen(-), and period (.). The name must not start or end with a period, and it should not have successive periods. The name is case sensitive and must be unique among all attribute names for the message. The name can be up to 256 characters long. The name cannot start with "AWS." or "Amazon." (or any variations in casing) because these prefixes are reserved for use by Amazon Web Services.
- A Type. String, Number, and Binary.
- A Value. The user-specified message attribute value.

**Name, type, and value must not be empty or null. In addition, the message body should not be empty or null.**
All parts of the message attribute, including name, type, and value, are included in the message size restriction, which is currently 256 KB.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Mobile Push

SNS is capable of sendind notification to mobile devices. Currently, the following push notifications platforms are supported:

- Amazon Device Messaging (ADM)
- Apple Push Notification Service (APNS)
- Google Cloud Messaging (GCM)
- Windows Push Notification Service (WNS) for Windows 8+ and Windows Phone 8.1+
- Microsoft Push Notification Service (MPNS) for Windows Phone 7+
- Baidu Cloud Push for Android devices in China

To begin using Amazon SNS mobile push notifications, you need the following:

- Step 1: Request Credentials from Mobile Platforms: To use Amazon SNS mobile push, you must first request the necessary credentials from the mobile platforms.
- Step 2: Request Token from Mobile Platforms: You then use the returned credentials to request a token for your mobile app and device from the mobile platforms. The token you receive represents your mobile app and device.
- Step 3: Create Platform Application Object: The credentials and token are then used to create a platform application object () from Amazon SNS.
- Step 4: Create Platform Endpoint Object: The is then used to create a platform endpoint object () from Amazon SNS.
- Step 5: Publish Message to Mobile Endpoint: The is then used to publish a message to an app on a mobile device. F

** A bulk upload of existing device tokens to Amazon SNS, either via the console interface or API is possible.**

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Top APIs

API | Description
--- | ---
CreateTopic | Creates a topic to which notifications can be published. Users can create at most 100,000 topics.
DeleteTopic | Deletes a topic and all its subscriptions.
ListTopics | Returns a list of the requester's topics. Each call returns a limited list of topics, up to 100. If there are more topics, a NextToken is also returned. Use the NextToken parameter in a new ListTopics call to get further results.
ListSubscriptions | Returns a list of the requester's subscriptions. Each call returns a limited list of subscriptions, up to 100. If there are more subscriptions, a NextToken is also returned. Use the NextToken parameter in a new ListSubscriptions call to get further results.
Publish | Sends a message to an Amazon SNS topic.
Subscribe | Prepares to subscribe an endpoint by sending the endpoint a confirmation message.
Unsubscribe | Deletes a subscription.
ConfirmSubscription | Verifies an endpoint owner's intent to receive messages by validating the token sent to the endpoint by an earlier Subscribe action.
CreatePlatformApplication | Creates a platform application object for one of the supported push notification services
CreatePlatformEndpoint | Creates an endpoint for a device and mobile app on one of the supported push notification services

[*(back to the top)*](README.md#markdown-header-table-of-contents)