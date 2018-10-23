# Table of Contents

1. [Preface](README.md#markdown-preface)
2. [SWF vs SQS](README.md#markdown-header-swf-vs-sqs)
3. [Limits](README.md#markdown-header-limits)

* * *

# Preface

Amazon Simple Workflow Service (SWF) is a web service that makes it easy to coordinate work across distributed application components. The main components of SWF are:


- **Domains**. Are named container that holds workflow processes and data. When a new workflow type or activity type is registered this needs to be  associate with a domain name in which all of the workflow activity takes place. Workflows and activities can only communicate with workflows and activities that exist within the same domain, and task lists that are used within a domain are distinct from task lists that exist in a separate domain, even if the task list has the same name as one that is being used in the other domain.
- **Workflows (aka Decider)**. It represents a sequence of steps required to perform a specific task. The steps needn't be strictly sequential; a workflow can consist of tasks that run sequentially, in parallel, synchronously or asynchronously. How the workflow behaves depends largely upon the business logic. **Because workflows contain code that responds to events that are managed by the Amazon SWF service, making decisions about what steps to take and how workflow execution proceeds, a workflow is also commonly referred to as a decider. Workflows are also responsible for passing data from and to any activities and child workflows that it runs.**
- **Activities**. Represents a step, or single unit of work, in a workflow. An activity can calculate a value based on input data, receive input from a web application, wait for a human task to be completed, or perform any other action that represents a step in your workflow.
- **Task Lists**. A task list is a logical entity used by Amazon SWF to manage events for workflows and activities. When a new workflow or activity is registered, is possible to provide it with a task list name that can be referred to in order to receive tasks for that workflow or activity.
- **Workers**. Workflow and Activity workers are responsible for receiving tasks from Amazon SWF and in taking appropriate actions to start a workflow or schedule an activity to be run. They are each configured with a task list to poll on.
- **Workflow Execution.** A workflow execution refers to an individual execution of a workflow.

Workers and Deciders can be deployed either in the cloud (e.g. Amazon EC2 or Lambda) or on machines behind corporate firewalls.
A workflow starter is any application that can initiate workflow executions.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# SWF vs SQS

- Amazon SWF API actions are task-oriented. Amazon SQS API actions are message-oriented.
- Amazon SWF keeps track of all tasks and events in an application. Amazon SQS requires users to implement their own application-level tracking, especially if application uses multiple queues.
- The Amazon SWF Console and visibility APIs provide an application-centric view that lets search for executions, drill down into an execution’s details, and administer executions. Amazon SQS 
- Tasks are always received and processed in the exact same order as they have been defined. SQS can't benefit of such functionality.

[*(back to the top)*](README.md#markdown-header-table-of-contents)

* * *

# Limits

- Maximum registered domains – 100.
- Maximum workflow and activity types – 10,000 each per domain.
- Maximum workflow execution time – 1 year

[*(back to the top)*](README.md#markdown-header-table-of-contents)