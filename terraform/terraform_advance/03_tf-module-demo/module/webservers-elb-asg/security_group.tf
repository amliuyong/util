
#
# inline Block resource

# resource "aws_security_group" "elb_sg" { 

#     ingress {
#         from_port = var.server_port
#         to_port = var.server_port
#         protocol = "tcp"
#         cidr_blocks = [ var.my_public_ip ]
#     }

#    egress {
#        from_port       = 0
#        to_port         = 0
#        protocol        = "-1"
#        cidr_blocks     = ["0.0.0.0/0"]
#      }    
# }



# use seperated aws_security_group_rule, instead of inline Block resource,  can be updated

resource "aws_security_group" "webserver_sg" {
  name = "webserver_sg-${var.environment}"
}

resource "aws_security_group_rule" "allow_http" {
  type              = "ingress"
  security_group_id = aws_security_group.webserver_sg.id
  from_port         = var.http_port
  to_port           = var.http_port
  protocol          = "tcp"
  cidr_blocks       = [var.my_system]
}

resource "aws_security_group_rule" "allow_ssh" {
  type              = "ingress"
  security_group_id = aws_security_group.webserver_sg.id
  from_port         = var.ssh_port
  to_port           = var.ssh_port
  protocol          = "tcp"
  cidr_blocks       = [var.my_system]
}

resource "aws_security_group_rule" "allow_outgoing" {
  type              = "egress"
  security_group_id = aws_security_group.webserver_sg.id
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
}