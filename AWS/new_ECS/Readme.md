# AWS ECS deep-dive course on Udemy

## Quickstart

- create IAM user
  log into AWS-mgm console and create new IAM user for this course, name it e.g. _eks-course_ and provide him policy permissions **AmazonECS-FullAccess**  and **AdministratorAccess**

- launch first container
  - using wizard in AWS-mgm console, starting webserver nginx container on Fargate

## Course Hands-On lectures

- [Docker basics](./01_docker-basics/Readme.md)
- [ECS setup](./02_ecs-setup/Readme.md)
- [Loadbalancing & autoscaling](./03_Loadbalancing-and-Autoscaling/Readme.md)
- [Environment variables and SSM, Secrets](./06_Env-variables-ssm-secrets/Readme.md)
- [ECR](./05_ECR/Readme.md)
- CI/CD
  - [CodeCommit](./04_CICD/1_CodeCommit/Readme.md)
  - [CodeBuild](./04_CICD/2_CodeBuild/Readme.md)
  - [CodePipeline](./04_CICD/3_CodePipeline/Readme.md)
  - [Blue/Green deployment w/ CodeDeploy](./04_CICD/4_Blue-Green-with-CodeDeploy/Readme.md)
- [EFS - persistent storage](./07_EFS-Persistent-storage/Readme.md)
- [Microservices - XRay/AppMesh/CloudMap](./08_Microservices-XRay-AppMesh/Readme.md)