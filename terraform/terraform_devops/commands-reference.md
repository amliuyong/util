# Commands Cheat Sheet <!-- NOTOC -->


A list of all the commands used in the course.

[[_TOC_]]

## Git

Add new files to Git:

```bash
git add .
```

Commit changes:

```bash
git commit -am "<Message>"
```

Add and commit on one line:

```bash
git add . && git commit -am "<Message>"
```


## aws-vault

Add a new user to aws-vault:

```bash
aws-vault add <user>
```

Initialise aws-vault in current terminal on macOS/Linux:

```bash
aws-vault exec <USER> --duration=12h
```

Initialise aws-vault in current command prompt window on Windows:

```bash
aws-vault exec <USER> --duration=12h -- cmd.exe
```


## Docker

Build docker image in current directory:

```bash
docker build -t <tag> .
```

### Docker Compose

Starting services:

```bash
docker-compose up
```

Starting services with specific config file:

```bash
docker-compose -f <file-path> up
```

## Project commands

Run unit tests:

```bash
docker-compose run --rm app sh -c "python manage.py wait_for_db && python manage.py test"
```

Run linting:

```bash
docker-compose run --rm app sh -c "flake8"
```

Run tests and linting together:

```bash
docker-compose run --rm app sh -c "python manage.py wait_for_db && python manage.py test && flake8"
```

## Bastion Commands

### Authenticate with ECR

In order to pull the application image, authentication with ECR is required.

To authenticate with ECR:

```sh
$(aws ecr get-login --no-include-email --region us-east-1)
```

### Create a superuser 

Replace the following variables:
 * `<DB_HOST>`: The hostname for the database (retrieve from Terraform apply output)
 * `<DB_PASS>`: The password for the database instance (retrieve from GitLab CI/CD variables)

```bash
docker run -it \
    -e DB_HOST=<DB_HOST> \
    -e DB_NAME=recipe \
    -e DB_USER=recipeapp \
    -e DB_PASS=<DB_PASS> \
    <ECR_REPO>:latest \
    sh -c "python manage.py wait_for_db && python manage.py createsuperuser"
```

## Terraform (via Docker Compose)

### Initialise

Initialise the Terraform state file locally, and download and Terraform providers.

```sh
docker-compose -f deploy/docker-compose.yml terraform init
```

### Format (fmt)

Run Terraform auto format command on code.

```sh
docker-compose -f deploy/docker-compose.yml run --rm terraform fmt
```

### Validate

Run Terraform validation on code.

```sh
docker-compose -f deploy/docker-compose.yml run --rm terraform validate
```

### Manage workspaces

Terraform allows you to create, remove or select a workspace using the CLI.

List all workspaces:

```sh
docker-compose -f deploy/docker-compose.yml terraform workspace list
```

 > In the below commands, replace `<name>` with the name of the workspace.

Create:

```sh
docker-compose -f deploy/docker-compose.yml terraform workspace new <name>
```

Select:

```sh
docker-compose -f deploy/docker-compose.yml terraform workspace select <name>
```

Delete:

```sh
docker-compose -f deploy/docker-compose.yml terraform workspace delete <name>
```


### Plan

Output a plan for changes Terraform will make to AWS resources.

```sh
docker-compose -f deploy/docker-compose.yml terraform plan
```

### Apply

Run Terraform apply to make changes described in plan to actual resources (this can create, remove or change resources)

```sh
docker-compose -f deploy/docker-compose.yml terraform apply
```

### Destroy

Remove any resources managed by Terraform (tear down all infrastructure) for selected workspace

```sh
docker-compose -f deploy/docker-compose.yml terraform destroy
```





