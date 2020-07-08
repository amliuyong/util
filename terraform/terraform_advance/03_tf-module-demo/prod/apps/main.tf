module "webservers" {
  # source = "git::https://github.com/cloudiac18/ultimate-terraform-course-for-devops.git//Section-05-modules/modules/webservers-elb-asg?ref=v0.0.11"
  source = "../../module/webservers-elb-asg"


  # Variables in module become Module inputs

  instance_type = "t2.micro"
  environment   = "prod"
  cluster       = "prod"
  vpc_id        = "vpc-cd8735b7"

}


# add a new ingress aws_security_group_rule to module.webservers.my_module_sg_id

resource "aws_security_group_rule" "allow_mytest" {
  type = "ingress"
  # use the moudle ouput 
  security_group_id = module.webservers.my_module_sg_id

  from_port   = "20000"
  to_port     = "20000"
  protocol    = "tcp"
  cidr_blocks = ["0.0.0.0/0"]
}
