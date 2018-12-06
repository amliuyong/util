#!/bin/bash

yum update -y
yum install httpd -y

cd /etc/httpd/conf

cp httpd.conf httpdconfbackup.conf

rm -rf httpd.conf

wget https://s3-eu-west-1.amazonaws.com/acloudguru-wp/httpd.conf

cd /var/www/html

echo "healthy" > healthy.html

wget https://wordpress.org/latest.tar.gz

tar -xzf latest.tar.gz

cp -r wordpress/* /var/www/html/

chown -R apache:apache /var/www/html


sudo service httpd start
sudo chkconfig httpd on



sudo systemctl start httpd
sudo systemctl enable httpd
sudo systemctl status httpd


"ApacheServer": {
"Type": "AWS::EC2::Instance",
"Properties": {
"ImageId": "ami-1ecae776",
"InstanceType": "t2.micro",
"KeyName": {"Ref": "KeyName"},
"NetworkInterfaces": [{
"AssociatePublicIpAddress": "false",
"DeleteOnTermination": "true",
"SubnetId": {"Ref": "SubnetPrivateApache"},
"DeviceIndex": "0",
"GroupSet": [{"Ref": "SecurityGroup"}]
}]
"UserData": {"Fn::Base64": {"Fn::Join": ["", [
"#!/bin/bash -ex\n",
"yum -y install httpd24-2.4.12\n",
"service httpd start\n"
]]}}
}
}

sudo yum install -y epel-release

sudo yum install -y varnish


sudo systemctl start varnish
sudo systemctl enable varnish
sudo systemctl status varnish

sudo varnishd -V



sudo vim /etc/varnish/varnish.params
VARNISH_LISTEN_PORT=80





sudo vi /etc/varnish/default.vcl

Tell Varnish to get the content on port 8080.

backend default {
.host = "127.0.0.1";
.port = "8080";
}


sudo apachectl restart
sudo systemctl restart varnish



backend default {
.host = "yongliu-s3-bucket.s3-website-us-east-1.amazonaws.com";
.port = "80";
}


http://ec2-52-23-167-162.compute-1.amazonaws.com:6081

http://yongliu-s3-bucket.s3-website-us-east-1.amazonaws.com


aws s3 cp s3://yongliu-s3-bucket/abc.txt .


{
  "Version":"2012-10-17",
  "Statement":[{
    "Sid":"PublicReadGetObject",
        "Effect":"Allow",
      "Principal": "*",
      "Action":["s3:GetObject"],
      "Resource":["arn:aws:s3:::yongliu-s3-bucket/*"
      ]
    }
  ]
}

You can grant public read permission to your objects by using either a bucket policy or an object ACL.
To make an object publicly readable using an ACL, grant READ permission to the AllUsers group,


<Grant>
  <Grantee xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:type="Group">
    <URI>http://acs.amazonaws.com/groups/global/AllUsers</URI>
  </Grantee>
  <Permission>READ</Permission>
</Grant>




