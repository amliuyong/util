
# Cross Account Access - Delegation

- create a user (bob) in Account-A (xxxxxxxxx-a), this is called *Identity Account*

- create a cross-account role (role name: Developer) in account-B
  //1. Trust Relationship of the role
  ```json
  {
      "Version": "2012-10-17",
      "Statement": {
          "Effect": "Allow",
          "Principal": {
              "AWS": "arn:aws:iam::xxxxxxxxx-a:root"
          },
          "Action": "sts:AssumeRole",
          "Condition" : {}
      }
  }
     
  ```
  //2. add some policies (e.g. S3FullAccess) to role 'Developer'
  
- allow user: bob to switch to account-B role
   // attach below iam policy to user: bob in account-B
   // note: bob does not need S3 access policy
   ```json
    {
      "Version": "2012-10-17",
      "Statement": {
          "Effect": "Allow",
          "Action": "sts:AssumeRole",
          "Resource": "arn:aws:iam::xxxxxxxxx-b:role/Developer"
      }
  }

   ```

- find the sigin link for bob in account-B, the role page of 'xxxxxxxxx-b:role/Developer'

    some thing like below:
    https://signin.aws.amazon.com/switchrole?roleName=Developer&account=xxxxxxxxx-b


- AWS CLI assume-role
```
    aws sts assume-role --role-arn arn:aws:iam::xxxxxxxxx-b:role/Developer --role-session-name tmp
```


# External ID - Secure Delegation

```json
  {
      "Version": "2012-10-17",
      "Statement": {
          "Effect": "Allow",
          "Principal": {
              "AWS": "arn:aws:iam::xxxxxxxxx-a:root"
          },
          "Action": "sts:AssumeRole",
          "Condition" : {
              "StringEquals": {
                  "sts:ExternalId": "090259"
              }
          }
      }
  }
     
```
Once the external ID is set, when someone uses the AWS CLI or AWS API to assume that role, they must provide the external ID.

You cannot switch roles at the AWS console to a role that has an external ID

```
   aws sts assume-role --role-arn arn:aws:iam::xxxxxxxxx-b:role/Developer --external-id 090259 --role-session-name tmp
```

# Instance metadata service 

`curl 169.254.169.254/latest/meta-data`


one time SSH public Key for login EC2 from web browser:

`curl 169.254.169.254/latest/meta-data/managed-ssh-keys/active-keys/ec2-user`


Temp security-credentials

`curl 169.254.169.254/latest/meta-data/iam/security-credentials/first-iam-role/`
 

 blocking access `169.254.169.254` at user level on EC2
  - `dev` - not allow
  - `root` - allow
  ```
  iptables -L

  iptables --append OUTPUT --proto tcp --destination 169.254.169.254 --match owner ! --uid-owner root --jump REJECT

  ```


  # IAM Policy Variables

  https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_variables.html


  - `${aws:username}`
  
  ```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": [
                "iam:*AccessKey*"
            ],
            "Effect": "Allow",
            "Resource": [
                "arn:aws:iam::888913816489:user/${aws:username}"
            ]
        }
    ]
}
  ```
 - `aws:PrincipalTag`
  
```json
{
  "Version": "2012-10-17",
  "Statement": {
    "Effect": "Allow",
    "Action": "iam:*AccessKey*",
    "Resource": "arn:aws:iam::ACCOUNT-ID-WITHOUT-HYPHENS:user/*",
    "Condition": {"StringEquals": {"aws:PrincipalTag/job-category": "iamuser-admin"}}
  }
}
```

-  `iam:ResourceTag`
```json
{
  "Version": "2015-01-01",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "iam:*",
      "Resource": "*",
      "Condition": {
        "StringLike": {
          "iam:ResourceTag/costCenter": [ "12345", "67890" ]
        }
      }
    }
  ]
}
```

## Information available in all requests

 - `aws:CurrentTime`

- `aws:EpochTime` 

- `aws:TokenIssueTime`

- `aws:PrincipalType` This value indicates whether the principal is an account, user, federated, or assumed role—see the explanation that follows later.

- `aws:SecureTransport` This is a Boolean value that represents whether the request was sent using SSL.

- `aws:SourceIp` 

- `aws:UserAgent` 

- `aws:userid`

- `aws:username`

- `ec2:SourceInstanceARN `

## Service-specific information
- `s3:prefix`

- `s3:max-keys`

- `s3:x-amz-acl`

- `sns:Endpoint`

- `sns:Protocol`


## Where you can use policy variables
You can use policy variables in the `Resource element` and in string comparisons in the` Condition element`.

- the following Resource element refers to only a bucket that is named the same as the value in the requesting user's department tag.

### Resource element
```
    "Resource": ["arn:aws:s3:::bucket/${aws:PrincipalTag/department}"]

```

#### Condition element
 The following policy example allows full access to IAM resources, but only if the tag costCenter is attached to the resource. The tag must also have a value of either 12345 or 67890. If the tag has no value, or has any other value, then the request fails.

```json
{
  "Version": "2015-01-01",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "iam:*",
      "Resource": "*",
      "Condition": {
        "StringLike": {
          "iam:ResourceTag/costCenter": [ "12345", "67890" ]
        }
      }
    }
  ]
}
```
```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Principal": {"AWS": "999999999999"},
    "Effect": "Allow",
    "Action": "sns:*",
    "Condition": {"StringLike": {"sns:endpoint": "https://example.com/${aws:username}/*"}}
  }]
}
```

# Policy

### Identity-based policies

- Alice - Read and Write
- Bob - Read and Write
- John - No policy

#### Resource-based policies
S3 Bucket A
 - Alice: List, Read, Write
 - Bob: Read
 - Jonh: Full acess


| IAM user |  Identity-based policies |Resource-based policies (S3 Bucket A)|  Final Operations  | Resource |
|:----------|:-----------:|:---------:| :--------: | ------------|
| Alice | Read, Write | List, Read, Write |List, Read, Write | S3 Bucket A |
| Bob | Read, Write | Read |Read, Write | S3 Bucket A |
| John | No policy | Full Access |Full Access | S3 Bucket A |


#### Policy On S3 Bucket

1. Alice has full access to the s3 bucket enven Alice does not have policy to access S3

2. Other users who has s3 access can access the bucket

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PolicyOnS3Bucket",
            "Effect": "Allow",
            "Principal": {
                "AWS": "arn:aws:iam::888913816489:user/Alice"
            },
            "Action": "s3:*",
            "Resource": [
                "arn:aws:s3:::the-demo-s3-bucket",
                "arn:aws:s3:::the-demo-s3-bucket/*"
            ]
        }
    ]
}

```

# Principal and NotPrincial
**Principal**
Principal Field can include various aspects like:

- IAM user
- IAM role
- IAM Service Name ("datapipeline.amazonaws.com")
- Federated Users
  

**NotPrincial** 

- Explicitly denied to all principals except for the nones specified
- Even `admin` user cannot access the bucket
 
 ```json
{
    "Version": "2012-10-17",
    "Statement": [{
        "Effect": "Deny",
        "NotPrincipal": {"AWS": [
            "arn:aws:iam::888913816489:user/Alice"
            "arn:aws:iam::888913816489:user/Bob"
        ]},
        "Action": "s3:*",
        "Resource": [
            "arn:aws:s3:::BUCKETNAME",
            "arn:aws:s3:::BUCKETNAME/*"
        ]
    }]
}
 ```




# Condition

https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_condition_operators.html

- IpAddress
```json
{
    "Version": "2012-10-17",
    "Statement": {
        "Effect": "Allow",
        "Action": "*",
        "Resource": "*",
        "Condition": {
            "IpAddress": {
                "aws:SourceIp": "115.99.177.174/32"
            }
        }
    }
}
```

- NotIpAddress
```json
{
    "Version": "2012-10-17",
    "Statement": {
        "Effect": "Allow",
        "Action": "*",
        "Resource": "*",
        "Condition": {
            "NotIpAddress": {
                "aws:SourceIp":  [
                    "115.99.177.0/24",
                    "11.99.17.0/24"
                ]
            }
        }
    }
}
```
 - BoolIfExists
  
 ```json
{
    "Version": "2012-10-17",
    "Statement": [{
        "Effect": "Deny",
        "Principal": {"AWS": [
            "arn:aws:iam::888913816489:user/Alice"
            "arn:aws:iam::888913816489:user/Bob"
        ]},
        "Action": "s3:*",
        "Resource": [
            "arn:aws:s3:::BUCKETNAME",
            "arn:aws:s3:::BUCKETNAME/*"
        ]
    }],

     "Condition": {
        "BoolIfExists" : {
            "aws:MultiFactorAuthPresent" : "false"
        }
    }
}
 ```

- DateGreaterThan 

```json
{
    "Condition": {
        "DateGreaterThan" : {
            "aws:CurrentTime" : "2019-12-25T12:00:000Z"
        }
    }
}

```

 - the principal making the request must be tagged with the iamuser-admin job category.
```json
{
  "Version": "2012-10-17",
  "Statement": {
    "Effect": "Allow",
    "Action": "iam:*AccessKey*",
    "Resource": "arn:aws:iam::ACCOUNT-ID-WITHOUT-HYPHENS:user/*",
    "Condition": {"StringEquals": {"aws:PrincipalTag/job-category": "iamuser-admin"}}
  }
}
```

- manage his or her own "home directory" in an Amazon S3 bucket
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:ListAllMyBuckets",
        "s3:GetBucketLocation"
      ],
      "Resource": "arn:aws:s3:::*"
    },
    {
      "Effect": "Allow",
      "Action": "s3:ListBucket",
      "Resource": "arn:aws:s3:::BUCKET-NAME",
      "Condition": {"StringLike": {"s3:prefix": [
        "",
        "home/",
        "home/${aws:username}/"
      ]}}
    },
    {
      "Effect": "Allow",
      "Action": "s3:*",
      "Resource": [
        "arn:aws:s3:::BUCKET-NAME/home/${aws:username}",
        "arn:aws:s3:::BUCKET-NAME/home/${aws:username}/*"
      ]
    }
  ]
}
```
 
 - requester can list up to 10 objects in example_bucket at a time.

```json
{
  "Version": "2012-10-17",
  "Statement": {
    "Effect": "Allow",
    "Action": "s3:ListBucket",
    "Resource": "arn:aws:s3:::example_bucket",
    "Condition": {"NumericLessThanEquals": {"s3:max-keys": "10"}}
  }
}
```

- Use a Null condition operator to check if a condition key is present at the time of authorization
- The following example shows a condition that states that the user must not be using temporary credentials (the key must not exist) for the user to use the Amazon EC2 API.
```json
{
  "Version": "2012-10-17",
  "Statement":{
      "Action":"ec2:*",
      "Effect":"Allow",
      "Resource":"*",
      "Condition":{"Null":{"aws:TokenIssueTime":"true"}}
  }
}
```


# STS ( AWS Secure Token Service)

 - IAM users
 - Federated Users 


## use long time credentials to generate tempprary credentials

```
aws sts get-session-token --duration-seconds 900 
```
Output:
```json
{
    "Credentials": {
        "AccessKeyId": "AKIAIOSFODNN7EXAMPLE",
        "SecretAccessKey": "wJalrXUtnFEMI/K7MDENG/bPxRfiCYzEXAMPLEKEY",
        "SessionToken": "AQoEXAMPLEH4aoAH0gNCAPyJxz4BlCFFxWNE1OPTgk5TthT+FvwqnKwRcOIfrRh3c/LTo6UDdyJwOOvEVPvLXCrrrUtdnniCEXAMPLE/IvU1dYUg2RVAJBanLiHb4IgRmpRV3zrkuWJOgQs8IZZaIv2BXIa2R4OlgkBN9bkUDNCJiBeb/AXlzBBko7b15fjrBs2+cTQtpZ3CYWFXG8C5zqx37wnOE49mRl/+OtkIKGO7fAE",
        "Expiration": "2020-05-19T18:06:10+00:00"
    }
}

```

## Cross account assume-role

```
    aws sts assume-role --role-arn arn:aws:iam::xxxxxxxxx-b:role/Developer --role-session-name tmp
```

# Federation

Federation allows external identities (Federated Users) to have secure access in your AWS account without having to create any IAM users.

These external identities can come from:
- Corporate Identity Provider( AD, IPA)
- Web Identity Provider (Facebook, Google, Amazone, Gognito or OpenID) 

LDAP - Lightweight Directory Access Protocal
 - Microsoft Active Directory
 - RedHat Identity Mangement / freeIPA


Components:
- User
- Identitiy Broker
- Micorsoft AD (users pool)
- AWS 

### Authentication Flow:

1. `User` signs in to **Identitiy Broker**
2.  **Identitiy Broker** authenticates user to ` Micorsoft AD`
3.  Then **Identitiy Broker** calls `AWS STS Service` to get `Authentication Response (tempprary credentials)`
4.  **Identitiy Broker** forwards the `Authentication Response` to `User`
5.  Now, `User` signs in with the `Response Token` via AWS CLI or AWS Console

#### build the AWS Console login URL using `tempprary credentials`

https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_providers_enable-console-custom-url.html#STSConsoleLink_manual

```python
sts_connection = boto3.client('sts')

assumed_role_object = sts_connection.assume_role(
    RoleArn="arn:aws:iam::ACCOUNT-ID-WITHOUT-HYPHENS:role/ROLE-NAME",
    RoleSessionName="AssumeRoleSession",
)

# Step 3: Format resulting temporary credentials into JSON
url_credentials = {}
url_credentials['sessionId'] = assumed_role_object.get('Credentials').get('AccessKeyId')
url_credentials['sessionKey'] = assumed_role_object.get('Credentials').get('SecretAccessKey')
url_credentials['sessionToken'] = assumed_role_object.get('Credentials').get('SessionToken')
json_string_with_temp_credentials = json.dumps(url_credentials)

# Step 4. Make request to AWS federation endpoint to get sign-in token. Construct the parameter string with
# the sign-in action request, a 12-hour session duration, and the JSON document with temporary credentials 
# as parameters.
request_parameters = "?Action=getSigninToken"
request_parameters += "&SessionDuration=43200"
if sys.version_info[0] < 3:
    def quote_plus_function(s):
        return urllib.quote_plus(s)
else:
    def quote_plus_function(s):
        return urllib.parse.quote_plus(s)
request_parameters += "&Session=" + quote_plus_function(json_string_with_temp_credentials)
request_url = "https://signin.aws.amazon.com/federation" + request_parameters
r = requests.get(request_url)
# Returns a JSON document with a single element named SigninToken.
signin_token = json.loads(r.text)

# Step 5: Create URL where users can use the sign-in token to sign in to 
# the console. This URL must be used within 15 minutes after the
# sign-in token was issued.
request_parameters = "?Action=login" 
request_parameters += "&Issuer=Example.org" 
request_parameters += "&Destination=" + quote_plus_function("https://console.aws.amazon.com/")
request_parameters += "&SigninToken=" + signin_token["SigninToken"]
request_url = "https://signin.aws.amazon.com/federation" + request_parameters

# Send final URL to stdout
print (request_url)

```


# SAML for SSO (single sign-on)

SAML - Security Assertion Markup Language
XML based 

Components:
- Identity Provider (IdP)
- Service Provider (SP) e.g AWS

### Authentication Flow:
1. `User` open the `IdP` URL and enter `username/password` and select the `appropriate app`
2. IdP send `ASML assertion (XML)` response to User
3. User POST `SAML assertion (XML)` to the SP (AWS) SAML sign-in URL and SP will validatte those assertion
    e.g one AWS SAML sign-in URL: `https://ap-southeast-1.sigin.aws.amazon.com/saml`
4. SP will construct temporary creential, and construnct sign in URL for the console and send back to the user, rediect to AWS console
   

## AWS Single Sign-On (SSO)
### create AWS SSO 
https://ap-northeast-2.console.aws.amazon.com/singlesignon/home?region=ap-northeast-2#/

1. Create Gruop: `PowerUsers`
2. Create below users and add users to Group: `PowerUsers`

    Sign-in URL: https://d-9b6720f27e.awsapps.com/start, Username: Alice, Password: xxxxxxx
    Sign-in URL: https://d-9b6720f27e.awsapps.com/start, Username: Yong, Password: xxxxxx

3. AWS accounts -> Permission Sets --> Create Permission set 
  Create Permission Set: `CustomPolicy`
4. Go to accounts/organization/details/717087451485
   Assign Uers --> Assign Group: `PowerUsers` to Permission Set: `CustomPolicy`

5. Try to login https://d-9b6720f27e.awsapps.com/start

### Configuring the AWS CLI to use AWS Single Sign-On
https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-sso.html
```
yongliu@local:~$ aws configure sso
SSO start URL [None]: https://d-9b6720f27e.awsapps.com/start
SSO Region [None]: ap-northeast-2
The only AWS account available to you is: 717087451485
Using the account ID 717087451485
The only role available to you is: CustomPolicy
Using the role name "CustomPolicy"
CLI default client Region [ap-northeast-2]:
CLI default output format [json]:
CLI profile name [CustomPolicy-717087451485]: sso

To use this profile, specify the profile name using --profile, as shown:

aws s3 ls --profile sso
yongliu@local:~$
yongliu@local:~$
yongliu@local:~$ aws s3 ls --profile sso
2020-11-12 17:11:50 a717087451485-codepipeline-for-photowall
2020-11-14 21:33:15 a717087451485-photowall-image-dev
2020-11-14 21:34:49 a717087451485-photowall-image-stage
yongliu@local:~$
```

# AWS Cognito - Federation

- Authentication
- Authorization
- user mangement

## Features
1. User Pools - taskes care of the tntire authentication/authorization    
2. Identity Pool - privides the functionality of federation for users in user pools



# AWS Directory Service - Directory on the Cloud

There are three import componets:
- AWS Managed Microsoft AD
- Simple AD
- AD Connector
- Amazon Cognito User Pools


# S3 Bucket Policy

###  Condition based on IP Address

```json
{
  "Version": "2012-10-17",
  "Id": "S3PolicyId1",
  "Statement": [
    {
      "Sid": "IPAllow",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:*",
      "Resource": "arn:aws:s3:::examplebucket/*",
      "Condition": {
         "IpAddress": {"aws:SourceIp": "54.240.143.0/24"}
      } 
    } 
  ]
}
```
### Cross Account access

Policy on accountA's bucket
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "111",
            "Effect": "Allow",
            "Principal": {
                "AWS": "arn:aws:iam::<accountB>:root"
            },
            "Action": "s3:*",
            "Resource": [
                "arn:aws:s3:::kplabs-demo-crossover",
                "arn:aws:s3:::kplabs-demo-crossover/*"
            ]
        }
    ]
}
```

`bucket-owner-full-control`
```
aws s3 cp test.txt s3://accountA-bucket/ --acl bucket-owner-full-control --profile accountB
```

# Presigned URLs

`aws s3 presign s3://<bucket>/<key> --expires-in 600`


# Bucket versioning

- allpy to all of the objects in the bucket
- when enable, cannot return to an unversioned state, however, you can suspend versioning.

# Cross-Region Replication
- Both source and destination buckets must have versioning enabled


# S3 Object Lock

- Governance Mode  -- can be deleted
- Gompliance Mode  -- can not be deleted even root user

# MFA

Policy attached to IAM user
```json
{
    "Version": "2012-10-17",
    "Statement": [{
        "Effect": "Deny",
        "Action":  [
            "ec2:StopInstances",
            "ec2:TerminateInstances"
        ],
        "Resource": "*"
    }],

     "Condition": {
        "BoolIfExists" : {
            "aws:MultiFactorAuthPresent" : "false"
        }
    }
}

```
### login AWC CLI with MFA token-code
```
aws sts get-session-token --serial-number arn:aws:iam::795574780076:mfa/sampleuser --token-code 774221

```
need to add the temp credentials to file  ~/.aws/credentials with a new profile


# IAM Permission Boundary

- To control the maximum permissions the user to have
- Boundary does not give the permissions
- Effective Permissions = Identity-based Policy && Permissions Boundary && Organizations SCP
- Any explicit deny is the final deny

# IAM and S3

| Policy Levels | Sample ARN | Description |
|:--------------|:---------|:-----------|
| Bucket Level | arn:aws:s3:::examplebucket | Operation at bucket level |
| Object Level | arn:aws:s3:::examplebucket/* | Operation at object level |
| Bucket and Object Level | arn:aws:s3:::examplebucket* | Have security risk |
