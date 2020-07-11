
## EBS Vs. EFS

![ebs](ebs.jpg)
![efs](./efs.jpg)
![storage](./default_storage.jpg)
![storage_options](./storage_options.jpg)


# EFS integration

https://aws.amazon.com/blogs/containers/developers-guide-to-using-amazon-efs-with-amazon-ecs-and-aws-fargate-part-1/



## Create EFS filesystem

Use AWS mgm console to create
* SecurityGroup for EFS
  ![SG](./sg.jpg)

* EFS file system
* EFS mount targets in each AZ.
  ![efs_mt ](./efs_mt.jpg)
  ![efs_mt_complete](./efs_mt_complete.jpg)

* EFS accesspoint
  ![efs_ap](./efs_ap.jpg)

## Extend taskExecutionRole

To be able to mount EFS targets, the taskExecutionRole needs the proper IAM permissions. Add the following JSON policy as _inline policy_ to the _taskExecutionRole_ , replacing the placeholders in:  
* EFS ARN
  * REGION
  * ACCOUNT_ID
  * fs-xxxxxx
* EFS Accesspoint ARN
  * REGION
  * ACCOUNT_ID
  * fsap-xxxxxxxxxxxxxx


```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "elasticfilesystem:ClientMount",
                "elasticfilesystem:ClientWrite"
            ],
            "Resource": "arn:aws:elasticfilesystem:REGION:ACCOUNT_ID:file-system/fs-xxxxxx",
            "Condition": {
                "StringEquals": {
                    "elasticfilesystem:AccessPointArn": "arn:aws:elasticfilesystem:REGION:ACCOUNT_ID:access-point/fsap-xxxxxxxxxxxxx"
                }
            }
        }
    ]
}
```

##  Add storage to task defintion
![efs_to_tdef](./efs_to_tdef.jpg)

Image:  coderaiser/cloudcmd

https://hub.docker.com/r/coderaiser/cloudcmd


```
docker pull coderaiser/cloudcmd:latest-alpine

docker run -t --rm -v ~:/root -v /:/mnt/fs -p 8000:8000 coderaiser/cloudcmd
docker run -t --rm -p 8001:8000 coderaiser/cloudcmd:latest-alpine
```


* add volumn to task defintion(*Note: NOT container*)
  ![efs_add_volume](./efs_add_volume.jpg)

* add mount point to container
![efs_mt_point](./efs_mt_point.jpg)

* create service and add container to ALB
  
![container_to_alb](./container_to_alb.jpg)
![alb_listener](./alb_listener.jpg)

![explore](./explore.jpg)

_Note_: the `/efs-share` is directly mounted to docker container file system, not on the undering EC2.


