# BuyCrypto

A simple webpage to purchase cryptocurrencies (Bitcoin, Ethereum, Solana, and Monero) using a sleek, user-friendly interface.

## Table of Contents

- [BuyCrypto](#buycrypto)
  - [Table of Contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Features](#features)
  - [Project Structure](#project-structure)
  - [Prerequisites](#prerequisites)
    - [For Development](#for-development)
    - [For Local Usage](#for-local-usage)
  - [Running Locally](#running-locally)
    - [Running Everything](#running-everything)
  - [Deploying](#deploying)
    - [Building the Images](#building-the-images)
    - [Deploying to GCP](#deploying-to-gcp)
    - [Destroying](#destroying)

## Introduction

BuyCrypto is a minimalist web application that allows users to input an amount and "buy" four cryptocurrencies: Bitcoin, Ethereum, Solana, and Monero. The project is divided into a backend that handles potential server functionalities and a frontend for the user interface.

## Features

- Clean and modern UI
- Interactive cryptocurrency buttons with logos
- Input fields to specify the amount to buy
- Simple and efficient frontend and backend setup

## Project Structure

```bash
.
├── README.md
└── src
    ├── backend
    │   ├── bitcoinService
    │   │   ├── Dockerfile
    │   │   ├── package.json
    │   │   ├── package-lock.json
    │   │   └── server.js
    │   ├── ethereumService
    │   │   ├── Dockerfile
    │   │   ├── package.json
    │   │   ├── package-lock.json
    │   │   └── server.js
    │   ├── moneroService
    │   │   ├── Dockerfile
    │   │   ├── package.json
    │   │   ├── package-lock.json
    │   │   └── server.js
    │   └── solanaService
    │       ├── Dockerfile
    │       ├── package.json
    │       ├── package-lock.json
    │       └── server.js
    ├── docker-compose.yml
    └── frontend
        ├── Caddyfile
        ├── Dockerfile
        ├── index.html
        ├── script.js
        └── styles.css
└── terraform
    ├── cluster
    │   ├── 01-variables.tf
    │   ├── 02-providers.tf
    │   ├── 03-cluster.tf
    │   ├── outputs.tf
    │   └── plugins.tf
    ├── kubernetes
    │   ├── 01-variables.tf
    │   ├── 02-providers.tf
    │   ├── 03-namespaces.tf
    │   ├── 04-services.tf
    │   ├── 05-pods.tf
    │   ├── 06-monitoring.tf
    │   ├── 07-istio.tf
    │   ├── 08-database.tf
    │   ├── monitoring
    │   │   ├── grafana
    │   │   │   └── values.yaml
    │   │   └── prometheus
    │   │       └── values.yaml
    │   └── plugins.tf
    ├── main.tf
    ├── terraform.tfvars.example
    └── vpc
        ├── 01-variables.tf
        ├── 02-providers.tf
        ├── 03-vpc.tf
        └── outputs.tf
```

## Prerequisites

### For Development

- [Node.js](https://nodejs.org/en/download/)
- [npm](https://www.npmjs.com/get-npm)

### For Local Usage

 - [Docker](https://docs.docker.com/get-docker/)
 - [Docker Compose](https://docs.docker.com/compose/install/)


## Running Locally

To get the needed required dependencies, run the following commands:

```bash
nix develop flake.nix
```

To open zsh shell with the required dependencies, run the following command:

```bash
nix develop flake.nix -c zsh
```

If you want to open an instance of vscode with the required dependencies, run the following command:

```bash
nix develop flake.nix -c code .
```

### Running Everything

To run the entire project (microservices and frontend) locally, we use docker and docker-compose. To do so, run the following commands (you may need to use `sudo`):

```bash
cd src
[sudo] docker compose up -d
```

## Deploying
### Building the Images
To build images and push them to docker hub, you need to push a commit to the master branch which will trigger a CI pipeline that builds and pushes the images to Docker Hub.

### Deploying to GCP
Before deploying, you need to create a project on Google Cloud Platform, as well as activing the APIs we use, mainly Kubernetes Engine API.
To authenticate, run the following commands:

``` sh
gcloud auth application-default login
```

You should make a copy of the terraform.tfvars.example file, and give it the name of `terraform.tfvars` , inside the terraform folder, and fill it with the following information:

```shell
project_id = ""
region = ""
db_username = ""
db_password = ""
db_name = ""
```

After that, you can run the following commands to deploy the infrastructure:

``` sh
cd terraform
terraform init
terraform plan
terraform apply
```

### Destroying
You can clean up the deployment by running `terraform destroy`. 