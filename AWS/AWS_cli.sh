
# find my ip
http://api.ipify.org/


aws s3 sync ./ s3://mort-aws-test/


aws configure --profile user2

====
EC2
====

https://docs.aws.amazon.com/cli/latest/reference/ec2/index.html

aws ec2 run-instances --image-id ami-1a2b3c4d \
--count 1 --instance-type c3.large \
--key-name MyKeyPair \
--security-groups MySecurityGroup

aws ec2 terminate-instances --instance-ids i-1234567890abcdef0

aws ec2 start-instances --instance-ids i-1234567890abcdef0


aws ec2 describe-instances --filters Name=tag-key,\
Values=aws:cloudformation:stack-name Name=tag-value,\
Values=wordpress --output text \
--query Reservations[0].Instances[0].PublicIpAddress

aws ec2 terminate-instances --instance-ids $InstanceId

aws ec2 describe-instances --filters "Name=tag:Name,\
Values=jenkins-multiaz" "Name=instance-state-code,Values=16" \
--query "Reservations[0].Instances[0].[InstanceId, PublicIpAddress]"
[
"i-5e4f68f7",
"54.88.118.96"
]

aws ec2 create-image --instance-id $InstanceId --name jenkins-multiaz
{
"ImageId": "ami-0dba4266"
}

aws ec2 describe-images --image-id $ImageId --query "Images[].State"

aws ec2 deregister-image --image-id $ImageId

aws ec2 describe-images --owners self \
--query Images[0].[ImageId,BlockDeviceMappings[0]\
.Ebs.SnapshotId]

aws ec2 delete-snapshot --snapshot-id $SnapshotId

http://169.254.169.254/latest/meta-data/

--
#====
#Create Users
#====

$ aws iam create-group --group-name "admin"
$ aws iam attach-group-policy --group-name "admin" \
--policy-arn "arn:aws:iam::aws:policy/AdministratorAccess"
$ aws iam create-user --user-name "myuser"
$ aws iam add-user-to-group --group-name "admin" --user-name "myuser"
$ aws iam create-login-profile --user-name "myuser" --password "$Password"

#Account ID
aws iam get-user --query "User.Arn" --output text


#upload your SSL certificate to IAM
aws iam upload-server-certificate \
--server-certificate-name my-ssl-cert \
--certificate-body file://my-certificate.pem \
--private-key file://my-private-key.pem \
--certificate-chain file://my-certificate-chain.pem


#===
#Installing security updates on all running EC2 instances
#===

PUBLICNAMES=$(aws ec2 describe-instances \
--filters "Name=instance-state-name,Values=running" \
--query "Reservations[].Instances[].PublicDnsName" \
--output text)
for PUBLICNAME in $PUBLICNAMES; do
ssh -t -o StrictHostKeyChecking=no ec2-user@$PUBLICNAME \
"sudo yum -y --security update"
done


# Installs only security updates on server start

[...]
"Server": {
"Type": "aws::EC2::Instance",
"Properties": {
[...]
"UserData": {"Fn::Base64": {"Fn::Join": ["", [
"#!/bin/bash -ex\n",
"yum -y --security update\n"
]]}}
}
}
[...]


#stop the instance
echo "aws ec2 stop-instances --instance-ids i-3dd4f812" | at now + 5 minutes


=====
= S3
=====

#create bucket
aws s3 mb s3://awsinaction-$YourName

#upload data
aws s3 sync $Path s3://awsinaction-$YourName/backup



#downlaod from s3
aws s3 cp --recursive s3://awsinaction-$YourName/backup $Path


#enable versioning
aws s3api put-bucket-versioning --bucket awsinaction-$YourName \
--versioning-configuration Status=Enabled

aws s3api list-object-versions --bucket awsinaction-$YourName

#add bucket-policy
aws s3 mb s3://elb-logging-bucket-$YourName
aws s3api put-bucket-policy --bucket elb-logging-bucket-$YourName \
--policy file://policy.json

#https://s3.amazonaws.com/awsinaction/chapter12/policy.json

#clean up
aws s3 rb --force s3://awsinaction-awittig
aws s3 rm s3://imagery-$AccountId --recursive


#config s3 bucket as website

aws s3api put-bucket-policy --bucket $BucketName \
--policy file://$PathToPolicy/bucketpolicy.json

cat bucketpolicy.json
{
  "Version":"2012-10-17",
  "Statement":[
    {
      "Sid":"AddPerm",
      "Effect":"Allow",
      "Principal": "*",
      "Action":["s3:GetObject"],
      "Resource":["arn:aws:s3:::$BucketName/*"]
    }
  ]
}

aws s3 website s3://$BucketName --index-document helloworld.html

aws s3 website s3://url2png-$YourName --index-document index.html \
--error-document error.html


http://$BucketName.s3-website-us-east-1.amazonaws.com

#download from s3
wget https://s3.amazonaws.com/awsinaction/chapter9/wordpress-import.sql


====
= EBS
====
#list ebs
sudo fdisk -l

#create a file system
sudo mkfs -t ext4 /dev/xvdf

#mount the device
sudo mkdir /mnt/volume/
sudo mount /dev/xvdf /mnt/volume/


df -h
sudo umount /mnt/volume/


#create-snapshot

aws --region us-east-1 ec2 describe-volumes \
--filters "Name=size,Values=5" --query "Volumes[].VolumeId" \
--output text

fsfreeze -f /mnt/volume/  #Freeze all writes

aws --region us-east-1 ec2 create-snapshot --volume-id $VolumeId

aws --region us-east-1 ec2 describe-snapshots --snapshot-ids $SnapshotId

fsfreeze -u /mnt/volume/

aws --region us-east-1 ec2 delete-snapshot --snapshot-id $SnapshotId


=====
cloudformation
=====

#create
aws cloudformation create-stack --stack-name wordpress --template-url \
https://s3.amazonaws.com/awsinaction/chapter9/template.json \
--parameters ParameterKey=KeyName,ParameterValue=mykey \
ParameterKey=AdminPassword,ParameterValue=test1234 \
ParameterKey=AdminEMail,ParameterValue=your@mail.com


aws cloudformation describe-stacks --stack-name wordpress

aws cloudformation describe-stacks --stack-name wordpress \
--query Stacks[0].Outputs[0].OutputValue --output text


#update
aws cloudformation update-stack --stack-name wordpress --template-url \
https://s3.amazonaws.com/awsinaction/chapter9/template-snapshot.json \
--parameters ParameterKey=KeyName,UsePreviousValue=true \
ParameterKey=AdminPassword,UsePreviousValue=true \
ParameterKey=AdminEMail,UsePreviousValue=true


aws cloudformation update-stack --stack-name jenkins-multiaz \
--template-url https://s3.amazonaws.com/awsinaction/\
chapter11/multiaz-ebs.json --parameters \
ParameterKey=JenkinsAdminPassword,UsePreviousValue=true \
ParameterKey=AMISnapshot,ParameterValue=$ImageId


#clean up
aws cloudformation delete-stack --stack-name wordpress


=====
RDS
=====


aws rds describe-db-instances

mysqldump -u $UserName -p $DatabaseName --host $Host > dump.sql


aws rds describe-db-instances --query DBInstances[0].Endpoint


mysql --host $DBHostName --user wordpress -p < wordpress-import.sql


#create a db snapshot

aws rds create-db-snapshot --db-snapshot-identifier \
wordpress-manual-snapshot \
--db-instance-identifier awsinaction-db


aws rds describe-db-snapshots \
--db-snapshot-identifier wordpress-manual-snapshot


#Copying an automated snapshot as a manual snapshot

aws rds describe-db-snapshots --snapshot-type automated \
--db-instance-identifier awsinaction-db \
--query DBSnapshots[0].DBSnapshotIdentifier \
--output text

aws rds copy-db-snapshot --source-db-snapshot-identifier \
$SnapshotId --target-db-snapshot-identifier \
wordpress-copy-snapshot



#find out the subnet group of the existing database
aws cloudformation describe-stack-resource \
--stack-name wordpress --logical-resource-id DBSubnetGroup \
--query StackResourceDetail.PhysicalResourceId --output text

#create a new database based on the manual snapshot
aws rds restore-db-instance-from-db-snapshot \
--db-instance-identifier awsinaction-db-restore \
--db-snapshot-identifier wordpress-manual-snapshot \
--db-subnet-group-name $SubnetGroup


#jump back to any point in time from the backup retention period
aws rds restore-db-instance-to-point-in-time \
--target-db-instance-identifier awsinaction-db-restore-time \
--source-db-instance-identifier awsinaction-db \
--restore-time $Time --db-subnet-group-name $SubnetGroup


#copy a snapshot to another region
aws rds copy-db-snapshot --source-db-snapshot-identifier \
arn:aws:rds:us-east-1:$AccountId:snapshot:\
wordpress-manual-snapshot --target-db-snapshot-identifier \
wordpress-manual-snapshot --region eu-west-1



# clean up

aws rds delete-db-instance --db-instance-identifier \
awsinaction-db-restore --skip-final-snapshot

aws rds delete-db-snapshot --db-snapshot-identifier \
wordpress-manual-snapshot


aws --region eu-west-1 rds delete-db-snapshot --db-snapshot-identifier \
wordpress-manual-snapshot


#create read replication
aws rds create-db-instance-read-replica \
--db-instance-identifier awsinaction-db-read \
--source-db-instance-identifier awsinaction-db


#promotes the read-replication to a *standalone* master database
aws rds promote-read-replica --db-instance-identifier awsinaction-db-read



====
DynamoDB
====

# create-table

aws dynamodb create-table --table-name app-entity \
--attribute-definitions AttributeName=id,AttributeType=S \
--key-schema AttributeName=id,KeyType=HASH \
--provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5



aws dynamodb describe-table --table-name todo-user


aws dynamodb create-table --table-name todo-task \
--attribute-definitions \
AttributeName=uid,AttributeType=S \
AttributeName=tid,AttributeType=N \
--key-schema AttributeName=uid,KeyType=HASH \
AttributeName=tid,KeyType=RANGE \
--provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5


aws dynamodb describe-table --table-name todo-task


aws dynamodb update-table --table-name todo-task \
--attribute-definitions \
AttributeName=uid,AttributeType=S \
AttributeName=tid,AttributeType=N \
AttributeName=category,AttributeType=S \
--global-secondary-index-updates '[{\
"Create": {\
"IndexName": "category-index", \
"KeySchema": [ \
{"AttributeName": "category", "KeyType": "HASH"}, \
{"AttributeName": "tid",      "KeyType": "RANGE"}], \
"Projection": {"ProjectionType": "ALL"}, \
"ProvisionedThroughput": {"ReadCapacityUnits": 5, \
                          "WriteCapacityUnits": 5}\
}}]'


aws dynamodb describe-table --table-name=todo-task \
--query "Table.GlobalSecondaryIndexes"


aws dynamodb get-item --table-name todo-user \
--key '{"uid": {"S": "michael"}}' \
--return-consumed-capacity TOTAL \
--query "ConsumedCapacity"
{
"CapacityUnits": 0.5,
"TableName": "todo-user"
}

aws dynamodb get-item --table-name todo-user \
--key '{"uid": {"S": "michael"}}' \
--consistent-read --return-consumed-capacity TOTAL \
--query "ConsumedCapacity"
{
"CapacityUnits": 1.0,
"TableName": "todo-user"
}


aws dynamodb create-table \
--table-name employee \
--attribute-definitions \
AttributeName=Employee_ID,AttributeType=S \
AttributeName=Employee_Name,AttributeType=S \
--key-schema AttributeName=Employee_ID, KeyType=HASH
--provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1

aws dynamodb put-item \
--table-name employee \
--item \
'{"Employee_ID": {"S": "10001"}, "Employee_Name": {"S": "Vipul Tankariya"},
"Country": {"S": "India"}}' \
--return-consumed-capacity TOTAL

aws dynamodb query --table-name employee --key-conditions file://condition-file.json


If an item is more than 4 KB in size, it requires additional read operations
If an item is less than 4 KB in size, it still requires 1 read capacity units

# A strongly consistent query requires one read capacity unit per 4 KB of item size.
strong read capacity = roundUP(itemSize / 4) * num
eventual read capacity = (roundUP(itemSize / 4) * num)/2

#A write operation needs one write capacity unit per 1 KB of item size

If an item is larger than 1 KB, it requires additional write capacity units
If an item is less than 1 KB in size, it still requires 1 write capacity unit

write capacity = roundUP(totalSize) * num


each item can have a maximum size of 400K;
no limit on the number of items in a table

======
regions and zones
======


aws ec2 describe-regions

aws ec2 describe-availability-zones --region $Region



====
ELB
====
dig elb-1079556024.us-east-1.elb.amazonaws.com
# nslookup on windows


====
SQS
====

aws sqs create-queue --queue-name url2png
{
"QueueUrl": "https://queue.amazonaws.com/878533158213/url2png"
}

aws sqs get-queue-attributes \
--queue-url $QueueUrl \
--attribute-names ApproximateNumberOfMessages


aws sqs send-message --queue-url qurl,  --message-body  "this is a message" --region  aregion


aws sqs delete-queue --queue-url $QueueUrl


Message size: 
Minimum size: 1 byte, maximum size: 256 KB.
Maximum 12 hours visibility timeout for a message

FIFO queue maximum 300 messages per second without batch, and 3,000 message per second with a maximum batch size of 10

Queue Name is case sensitive, maximum of up to 80 characters. 
FIFO queu Name must end with .fifo suffix

=====
autoscaling
=====

aws autoscaling put-scheduled-update-group-action \
--scheduled-action-name ScaleTo4 \
--auto-scaling-group-name webapp \
--start-time "2016-01-01T12:00:00Z" \
--desired-capacity 4


aws autoscaling put-scheduled-update-group-action \
--scheduled-action-name ScaleTo2 \
--auto-scaling-group-name webapp \
--recurrence "0 20 * * *" \
--desired-capacity 2

#Recurrence is defined in Unix cron syntax format
* * * * *
| | | | |
| | | | +- day of week (0 - 6) (0 Sunday)
| | | +--- month (1 - 12)
| | +----- day of month (1 - 31)
| +------- hour (0 - 23)
+--------- min (0 - 59)

#send 10,000 requests to the load balancer using two threads.
ab -n 10000 -c 2 $UrlLoadBalancer


====
ECR
====
https://docs.aws.amazon.com/AmazonECS/latest/developerguide/docker-basics.html

aws ecr create-repository --repository-name hello-world

docker tag hello-world aws_account_id.dkr.ecr.us-east-1.amazonaws.com/hello-world

aws ecr get-login --no-include-email

docker push aws_account_id.dkr.ecr.us-east-1.amazonaws.com/hello-world

cat hello-world-task-def.json 
{
    "family": "hello-world",
    "containerDefinitions": [
        {
            "name": "hello-world",
            "image": "aws_account_id.dkr.ecr.us-east-1.amazonaws.com/hello-world",
            "cpu": 10,
            "memory": 500,
            "portMappings": [
                {
                    "containerPort": 80,
                    "hostPort": 80
                }
            ],
            "entryPoint": [
                "/usr/sbin/apache2",
                "-D",
                "FOREGROUND"
            ],
            "essential": true
        }
    ]
}

aws ecs register-task-definition --cli-input-json file://hello-world-task-def.json

aws ecs run-task --task-definition hello-world



====
lambda
====
deploy:

"scripts": {
  "test": "run-local-lambda --file index.js –-event tests/event.json",
  "deploy": "aws lambda update-function-code --function-name arn:aws:lambda:us-east-1:038221756127:function:transcode-video --zip-file fileb://Lambda-Deployment.zip",
  "predeploy": "zip -r Lambda-Deployment.zip * -x *.zip *.json *.log"
}


aws lambda delete-alias --function-name return-response --name production

aws lambda update-function-code \
--function-name arn:aws:lambda:us-east-1:038221756127:function:transcode-video \
--zip-file fileb://Lambda-Deployment.zip

aws lambda invoke --function-name get-video-list output.txt


zip -r index.zip * -x *.zip *.json *.log
aws lambda create-function --function-name cli-function  \
--handler index.handler  \
--memory-size 128 --runtime nodejs4.3  \
--role arn:aws:iam::038221756127:role/lambda-s3-execution-role \
--timeout 3  \
--zip-file fileb://index.zip --publish

publish-version
--function-name <value>
[--code-sha-256 <value>]
[--description <value>]
[--revision-id <value>]
[--cli-input-json <value>]
[--generate-cli-skeleton <value>]

 invoke
--function-name <value>
[--invocation-type <value>]
[--log-type <value>]
[--client-context <value>]
[--payload <value>]
[--qualifier <value>]
outfile <value>

list-functions, list-aliases, list-versions-by-function


====
sam
=====
Using the aws Serverless Application Model (aws SAM)
https://docs.aws.amazon.com/lambda/latest/dg/serverless_app.html
https://docs.aws.amazon.com/lambda/latest/dg/serverless-deploy-wt.html

https://github.com/awslabs/aws-sam-cli

pip install --user aws-sam-cli

sam init --runtime nodejs

sam local invoke "Ratings" -e event.json

echo '{"message": "Hey, are you there?" }' | sam local invoke "Ratings"

sam local generate-event <service>

Service:
- S3
- Kinesis
- DynamoDB
- Cloudwatch Scheduled Event
- Cloudtrail
- API Gateway

sam local generate-event s3 --bucket <bucket> --key <key> | sam local invoke <function logical id>

# sam package command will zip your code artifacts, 
# upload to S3 and produce a SAM file that is ready to be 
# deployed to Lambda using aws CloudFormation

sam package \
   --template-file sam-app/template.yaml \
   --output-template-file serverless-output.yaml \
   --s3-bucket s3-bucket-name

# sam deploy command will deploy the packaged SAM template to CloudFormation.

sam deploy \
   --template-file serverless-output.yaml \
   --stack-name new-stack-name \
   --capabilities CAPABILITY_IAM


sam local start-api
sam local start-api --profile some_profile
sam local start-api --env-vars env.json
TABLE_NAME=mytable sam local start-api
sam local invoke --log-file ./output.log

# Invoke a function locally in debug mode on port 5858
$ sam local invoke -d 5858 <function logical id>

# Start local API Gateway in debug mode on port 5858
$ sam local start-api -d 5858

sam local invoke --docker-volume-basedir /c/Users/shlee322/projects/test "Ratings"

====
plocy
=====

{
    "Version": "2012-10-17",
    "Statement": [{
        "Sid": "VisualEditor0",
        "Effect": "Allow",
        "Action": "s3:PutObjectAcl",
        "Resource": "arn:aws:s3:::mort-aws-test1/*",
        "Condition": {
            "DateLessThan": {
                "aws:CurrentTime": "2016-10-12T12:00:00Z"
            },
            "IpAddress": {
                "aws:SourceIp": "127.0.0.1"
            }
        }
    }]
}

{
    "Version": "2012-10-17",
    "Id": "123",
    "Statement": [{
        "Effect": "Deny",
        "Principal": "*",
        "Action": "s3:*",
        "Resource": "arn:aws:s3:::my-bucket/*",
        "Condition": {
            "Bool": {
                "aws:SecureTransport": false
            }
        }
    }]
}

# Trust Relationship
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": [
          "apigateway.amazonaws.com",
          "lambda.amazonaws.com"
        ]
      },
      "Action": "sts:AssumeRole"
    }
  ]
}

#SQS policy
{
    "Version": "2008-10-17",
    "Id": "MyID",
    "Statement": [{
        "Sid": "ExampleID",
        "Effect": "Allow",
        "Principal": {
            "aws": "*"
        },
        "Action": [
            "SQS:SendMessage"
        ],
        "Resource": "SQS-ARN",
        "Condition": {
            "ArnLike": {
                "aws:SourceArn": "arn:aws:s3:*:*:YOUR_BUCKET_NAME"
            }
        }
    }]
}

#Example of an IAM policy that you attach to the destination SNS topic.

{
    "Version": "2008-10-17",
    "Id": "example-ID",
    "Statement": [{
        "Sid": "example-statement-ID",
        "Effect": "Allow",
        "Principal": {
            "Service": "s3.amazonaws.com"
        },
        "Action": [
            "SNS:Publish"
        ],
        "Resource": "SNS-ARN",
        "Condition": {
            "ArnLike": {
                "aws:SourceArn": "arn:aws:s3:*:*:bucket-name"
            }
        }
    }]
}

#Upload policy on a user
{
    "Version": "2012-10-17",
    "Statement": [{
            "Effect": "Allow",
            "Action": [
                "s3:ListBucket"
            ],
            "Resource": [
                "arn:aws:s3:::YOUR_UPLOAD_BUCKET_NAME"
            ]
        },
        {
            "Effect": "Allow",
            "Action": [
                "s3:PutObject"
            ],
            "Resource": [
                "arn:aws:s3:::YOUR_UPLOAD_BUCKET_NAME/*"
            ]
        }
    ]
}







