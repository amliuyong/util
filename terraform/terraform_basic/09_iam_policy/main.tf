resource "aws_iam_user" "myUser" {
  name = "John"
}

resource "aws_iam_policy" "customPolicy" {
  name = "MyCustomPolicy"

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "VisualEditor0",
            "Effect": "Allow",
            "Action": [
                "glue:GetCrawler",
                "glue:BatchGetDevEndpoints",
                "glue:GetTableVersions",
                "glue:GetPartitions",
                "glue:UpdateCrawler",
                "glacier:ListParts",
                "glue:GetDevEndpoint",
                "glue:UpdateTrigger",
                "glue:GetTrigger",
                "glue:GetJobRun",
                "glacier:PurchaseProvisionedCapacity",
                "glue:GetJobs",
                "glue:DeleteCrawler",
                "glue:GetTriggers",
                "glacier:DeleteVault",
                "glacier:DeleteArchive",
                "glue:GetWorkflowRun",
                "glue:GetMapping",
                "glue:GetPartition",
                "glue:DeleteConnection",
                "glacier:ListMultipartUploads",
                "glue:BatchDeleteConnection",
                "glacier:SetVaultNotifications",
                "glue:StartCrawlerSchedule",
                "glacier:CompleteMultipartUpload",
                "glacier:ListVaults",
                "glue:GetClassifiers",
                "glacier:CreateVault",
                "glue:BatchDeletePartition",
                "glue:DeleteTableVersion",
                "glue:CreateTrigger",
                "glue:CreateUserDefinedFunction",
                "glue:StopCrawler",
                "glue:StopTrigger",
                "glue:DeleteJob",
                "glue:GetCatalogImportStatus",
                "glue:DeleteDevEndpoint",
                "glacier:DeleteVaultNotifications",
                "glue:CreateJob",
                "glue:GetTableVersion",
                "glue:GetConnection",
                "glue:ResetJobBookmark",
                "glue:CreatePartition",
                "glue:UpdatePartition",
                "glue:BatchGetPartition",
                "glue:GetTags",
                "glue:GetTable",
                "glue:GetDatabase",
                "glue:GetDataflowGraph",
                "glue:BatchGetCrawlers",
                "glue:CreateDatabase",
                "glue:BatchDeleteTableVersion",
                "glue:GetPlan",
                "glue:GetJobRuns",
                "glue:BatchCreatePartition",
                "glue:GetDataCatalogEncryptionSettings",
                "glacier:AbortMultipartUpload",
                "glue:CreateClassifier",
                "glue:GetWorkflowRunProperties",
                "glue:UpdateTable",
                "glue:DeleteTable",
                "glue:DeleteWorkflow",
                "glue:GetSecurityConfiguration",
                "glue:GetResourcePolicy",
                "glue:CreateScript",
                "glue:UpdateWorkflow",
                "glue:GetUserDefinedFunction",
                "glue:StartWorkflowRun",
                "glue:StopCrawlerSchedule",
                "glue:GetUserDefinedFunctions",
                "glue:PutResourcePolicy",
                "glue:GetClassifier",
                "glue:TagResource",
                "glue:UpdateDatabase",
                "glacier:InitiateJob",
                "glue:GetTables",
                "glue:CreateTable",
                "glue:DeleteResourcePolicy",
                "glacier:ListTagsForVault",
                "glue:BatchStopJobRun",
                "glue:DeleteUserDefinedFunction",
                "glue:CreateConnection",
                "glue:CreateCrawler",
                "glue:DeleteSecurityConfiguration",
                "glue:GetDevEndpoints",
                "glue:BatchGetWorkflows",
                "glue:BatchGetJobs",
                "glue:StartJobRun",
                "glue:BatchDeleteTable",
                "glacier:UploadMultipartPart",
                "glue:UpdateClassifier",
                "glue:CreateWorkflow",
                "glue:DeletePartition",
                "glue:GetJob",
                "glue:GetWorkflow",
                "glue:GetConnections",
                "glue:GetCrawlers",
                "glue:CreateSecurityConfiguration",
                "glue:PutWorkflowRunProperties",
                "glue:DeleteDatabase",
                "glue:StartTrigger",
                "glue:ImportCatalogToGlue",
                "glacier:ListJobs",
                "glue:PutDataCatalogEncryptionSettings",
                "glue:StartCrawler",
                "glacier:InitiateMultipartUpload",
                "glue:UntagResource",
                "glue:UpdateJob",
                "glacier:UploadArchive",
                "glue:DeleteClassifier",
                "glue:UpdateUserDefinedFunction",
                "glue:GetSecurityConfigurations",
                "glue:GetDatabases",
                "glue:UpdateConnection",
                "glue:BatchGetTriggers",
                "glue:CreateDevEndpoint",
                "glue:UpdateDevEndpoint",
                "glue:GetWorkflowRuns",
                "glue:DeleteTrigger",
                "glue:GetCrawlerMetrics",
                "glacier:ListProvisionedCapacity"
            ],
            "Resource": "*"
        }
    ]
}
EOF

}

resource "aws_iam_policy_attachment" "policyBind" {
  name       = "attachment"
  users      = ["${aws_iam_user.myUser.name}"]
  policy_arn = "${aws_iam_policy.customPolicy.arn}"
}
