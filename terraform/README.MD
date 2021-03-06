
## Install

brew install warrensbox/tap/tfswitch


## Authenticate against AWS

Four places terraform looks for credentials to authenticate against AWS

1.Static credentials declared in provider section of code

2.Export as environment variables

```
  export AWS_ACCESS_KEY="access_key"
  export AWS_SECTRET_KEY="secret_access_key"
  export AWS_DEFAULT_REGION="us-east-1"
```

3.Shared credentials (~/.aws/credentials)

4.EC2 role (such as Jenkins build server or any ec2 instance used for resource provisioning )


## Terraform command

```

terraform init 
terraform validate

terraform plan

terraform apply 
terraform apply -auto-approve

terraform apply -var="region=us-east-1"
terraform apply -var-file="myproject.tfvars"
export TF_VAR_region=us-east-1

terraform state list
terraform state show

terraform output

terraform destroy

terraform fmt
  
terraform taint  - Manually mark a resource for recreation
  
terraform get  - update versioning module
  
terraform graph

```


## Example of user data

```
   user_data = <<-EOF
     #!/bin/bash
     exec > >(tee /var/log/user-data.log|logger -t user-data -s 2>/dev/console) 2>&1
     /usr/bin/apt-get update
     DEBIAN_FRONTEND=noninteractive /usr/bin/apt-get upgrade -yq
     /usr/bin/apt-get install apache2 -y
     /usr/sbin/ufw allow in "Apache Full"
     /bin/echo "Hello world " >/var/www/html/index.html
     instance_ip=`curl http://169.254.169.254/latest/meta-data/public-ipv4`
     echo $instance_ip >>/var/www/html/index.html
     EOF
```

## Interploations

```
  ${var.ami}
  ${count.index}
  ${count.index + 1}
  ${self.private_id}
  ${var.Map[Key]} ==> ${var.ami[us-east-1]}
                  ==> "${lookup(var.instance_type, terraform.workspace)}"

  ${var.List} ==> ${var.subnets}
  ${aws_instance.myec2.id}
  
  Moudle.Name.Output ==>  ${module.webservers.dns_name_elb}
  
  path.Type  ==> ${path.module}

  ${terraform.workspace}
  
  Condtion? true_value: false_value

  resource "aws_instance" "web" {
     subnet = "${var.env_type == "production"? var.prod_subnet: var.dev_subnet}"
   }

   file("user_data.sh")
   
   Concat(lis1, list2, list3) ==> concat("${var.server_sg}", "${var.client_sg}", "${var.db_sg}")


   Format(format, args) ==> format("%.1s", "${var.instacne_type}")  # get first letter

   

```

## Variables

```

variable "region" {
   default = "us-east-1",
   description ="description of the var"
}

variable "myvar" {
   type = map
   default = {
     key1="val1",
     key2="val2"
   }
}

variable "myvar" {
   type = map|list|string|number|bool|set|tuple|object 
}


```
### How to find the variables

1. Env TF_VAR_
2. terraform.tfvars
3. terraform.tfvars.json
4. any *.auto.tfvar or *.auto.tfvars.json
5. -var and -var-file on the command line

## Outputs

```
output "elb_endpoint" {
    description = "description of output"
    value = [ "${aws_elb.my_first_elb.dns_name}" ]
    sensitive = false|true
    depends_on = [
      aws_security_group.webserver_sg
    ]
}

  # to access from other module
  ${module.webservers.elb_endpoint}

```


## Readonly Remote state

```
data "terraform_remote_state" "global_sg" {
   backend = "S3"

   config = {
      bucket = "remote_bucket_name"
      key = "global_sg/terraform.tfstate"
      region = "use-east-1"
   } 
}

# to access remote secrity group id:

 vpc_security_group_ids = [
     "${aws_security_group.webserver_sg.id}",
     "${data.terraform_remote_state.global_sg.outputs.global_sg_id}"
 ]

```

## Data Source

[Provider Data Sources](https://www.terraform.io/docs/providers/aws/d/region.html)

```
aws_arn
aws_availability_zone
aws_availability_zones
aws_billing_service_account
aws_caller_identity
aws_ip_ranges
aws_partition
aws_region
aws_regions

```


## Module

- Variables in module become module inputs

- Use separate resource, instead of inline block resource for security group

```
module "webservers" {
 # source = "git::https://github.com/cloudiac18/ultimate-terraform-course-for-devops.git//Section-05-modules/modules/webservers-elb-asg?ref=v0.0.11"

  source = "../../module/webservers-elb-asg"

  
  # variables in the submodule

  instance_type = "t2.micro"
  environment = "prod"
  cluster = "prod"
  vpc_id = "vpc-cd8735b7"
}


user_data=file("${path.module}/user_data.sh")


```

### Moudle Versioning
```
source = "git::<git_repo_name>.git//<folder_name>?ref=<version>"

source = "git::https://github.com/cloudiac18/ultimate-terraform-course-for-devops.git//Section-05-modules/modules/webservers-elb-asg?ref=v0.0.11"
```

*when you update module, must run `terraform get`*



## Terraform State

```
terraform {
    backend s3 {
       encrypt=true
       bucket = "terraformiac-mystach-tfstate"
       key = "global_security_group/terraform.tfstate"
       region = "us-east-1"
       dynamodb_table = "terraform-state"
    }
}

```

### State Locking

dynamodb_table *terraform-state*  is for locking. 
primary key (string): *LockID* 

## AWS Service

#### route53

create a route53 record to point to EC2 private id

```
resource "aws_route53_zone" "my_private_zone" {
   name = "myr53zone.com"
}

resource "aws_route53_record" "webserver" {
    zone_id = "${aws_route53_zone.my_private_zone.zone_id}"
    name    = "web.myr53zone.com"
    type    = "A"
    ttl     = "300"
    records = ["${aws_instance.hellow-world.private_ip}"]
}

```

#### ebs

```
resource "aws_ebs_volume" "web-ebs" {
    availability_zone = "${var.az}"
    size = 10
    type = "gp2"
    tags = {
        Name = "webserver_data"
     } 
}
  
resource "aws_volume_attachment" "web-ebs-attach" {
    device_name = "/dev/sdd"
    volume_id = "${aws_ebs_volume.web-ebs.id}"
    instance_id="${aws_instance.hellow-world.id}"
}
```


### vpc
![AWS Nat Gateway in VPC](./aws_vpc_nat.png)

[Code](./terraform_advance/05_working_vpc-subnet-igw-natgw/)


## Provisioner

 - local-exec: run locally where `terraform apply` run
  
  ```
   provisioner "local-exec" {
      command = "echo ${self.private_ip} > webserver_private_ip.txt"
      # command = "echo ${self.public_ip} > webserver_public_ip.txt"
      on_failure = continue
   }
  ``` 

 - remote-exec, run command instead of user_data
```
   provisioner "remote-exec" { 
       inline = [
           "sudo /usr/bin/apt-get update",
           "sudo DEBIAN_FRONTEND=noninteractive /usr/bin/apt-get upgrade -yq",
           "sudo /usr/bin/apt-get install apache2 -y",
           "sudo /usr/sbin/ufw allow in 'Apache Full' ",
           "sudo /bin/chmod 757  /var/www/html/index.html",
           "sudo /bin/echo 'Hello world' >/var/www/html/index.html",
           "instance_ip=`curl http://169.254.169.254/latest/meta-data/local-ipv4`",
           "sudo echo $instance_ip >>/var/www/html/index.html", 
        ]
        connection {
           type = "ssh"
           user = "ubuntu"
           private_key=file("./terraform.pem")
           host = self.public_ip
       }
   }

```


## Workspace

workspace commands:

```
terraform workspace show
terraform workspace list

terraform workspace new prod
terraform workspace select prod

```

use workspace in code: `${terraform.workspace}`

```
resource "aws_instance" "hellow-world" {
     ami = "${var.ami}" 
     instance_type = "${lookup(var.instance_type, terraform.workspace)}"
     vpc_security_group_ids = ["${aws_security_group.webserver_sg.id}"]
     key_name = "terraform"
     tags = {
     	 Name = "Hello world-${terraform.workspace}"
     }
```     

## using tfvars as parameters

```
terraform init -var-file="silicon_valley.tfvars" -backend-config="key=silicon_valley/terraform.tfstate"  -reconfigure

terraform plan -var-file="silicon_valley.tfvars"

terraform apply -var-file="silicon_valley.tfvars"


```

## user data to install jenkins 

```
 user_data = <<EOC
#!/bin/bash -xe
exec > >(tee /var/log/user-data.log|logger -t user-data -s 2>/dev/console) 2>&1
/usr/bin/wget -q -O - https://pkg.jenkins.io/debian/jenkins.io.key | sudo apt-key add -
/bin/sh -c 'echo deb http://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
/usr/bin/apt-get update
DEBIAN_FRONTEND=noninteractive /usr/bin/apt-get upgrade -yq
/usr/bin/apt-get install default-jdk -y
/usr/bin/apt-get install jenkins -y
/bin/systemctl start jenkins
/bin/systemctl status jenkins
/usr/sbin/ufw allow 8080
/usr/sbin/ufw status
/usr/bin/apt install python3-pip -y
pip3 install awscli
apt install unzip
wget -q https://releases.hashicorp.com/terraform/0.11.6/terraform_0.11.6_linux_amd64.zip
unzip terraform_0.11.6_linux_amd64.zip
mv terraform /usr/local/bin/terraform
terraform version
echo "#########   all commands executed successfuly !! ########## "
EOC

```