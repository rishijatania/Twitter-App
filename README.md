# Twitter-App
# Team 1
| Name | Email Address |
| --- | --- |
| Rishi Jatania | jatania.r@northeastern.edu |
| Karun Kesavadas | kesavadas.k@northeastern.edu |

## Introduction:
Twitter-App is an the modern microblogging and social networking service on which users post and interact with messages known as "tweets". Registered users can post, like and comment on tweets, follow other users for daily updates. The app also blocks offensive tweets and comments by users using [RapidAPI](https://rapidapi.com) module called [Purgomalum](https://rapidapi.com/community/api/PurgoMalum/details). Other features include Abusive tweet and Bug reporting in [Slack Incoming Webhooks](https://api.slack.com/messaging/webhooks).

Deployment scripts recreate this scalable microservices application and deploy it on AWS CLoud Infrastructure. 

## Scope:
The deployment scripts would create 2 worker nodes within AWS EKS cluster automated using Terraform scripts. All routing is managed internally by Service Names, so no IP's needs to be edited.

## Infrastructure:
The project is deployed on Kubernetes cluster and has the following components:
### Components:
	- Frontend:
		1. Frontend deployment
		2. Frontend Load balancer Service
	- Middleware:
		1. Middleware deployment
		2. Middleware Load balancer Service
	- Redis-Queue:
		1. Queue deployment
		2. Queue Load balancer Service
	- Backend:
		1. Backend Auth/Profile Deployment
		2. Backend Auth/Profile Load balancer Service
		3. Backend Tweet Deployment 
		4. Backend Tweet Load balancer Service
	- MongoDB: - (This is a hosted cluster on MongoDB Atlas):
		1. MongDB Atlas cluster
	- Prometheus:
		1. Prometheus deployment
		2. Prometheus Load balancer Service
	- Grafana:
		1. Grafana dashboard for visualization
	- AutoScaler:
		1. Horizontal-pod Autoscaler for Backend Service
	- Metrics-Server
		1. Source of Container resource metrics for AutoScaling

## PreReq tools that you need

1. `aws-cli`
2. `kubectl`
3. `git`
4. `terraform`
5. `aws-iam-authenticator`
## Dependancies

We need [Terraform](https://www.terraform.io/downloads.html), [Docker](https://docs.docker.com/get-docker/) for running the deployment.
We need [S3 Bucket Name](https://aws.amazon.com/s3/) for hosting images, [Slack WebHooks](https://api.slack.com/messaging/webhooks), [RapidAPI](https://rapidapi.com) credentials, RAPID API KEY for [Purgomalum](https://rapidapi.com/community/api/PurgoMalum/details), DockerHub Username and Password and AWS Account Access Key and Secret of the Account that will run the EKS stack and also has the S3 Bucket.
We also need the [MongoDB Atlas Cluster URL](https://www.mongodb.com/cloud).

## continuous Integration Setup
In-order to setup continuous integration in github using github actions please configure your Docker Username(DOCKER_USER) and Password(DOCKER_PASSWORD) by following these [steps](https://secrethub.io/docs/guides/github-actions/#pass-credenitals-to-github-action)


## Initial set up
Keep you Docker up and running
Clone the project, and open the Terminal(Linux/Mac) or Git bash(Windows) into the infrastructure directory of the project and run the command
```sh
./deployment.sh
```
The scripts deploys the infrastructure using terraform scripts and ask for the details required to populate the secrets in the following order:
1. Name of the 4 secret files you would like to use. (Press enter for the first 4 prompts)
2. MongoDB Cluster URI
3. MongoDB Name
4. Slack Webhook URI
5. JWT Secret (Can be any random string)
6. Rapid API Key
7. AWS Access Key
8. AWS Secret Access Key
9. S3 Bucket Name

Once secrets have been generated the script execution continues and deploys K8 cluster services. 

Once the cluster is created check the services with:

```sh
kubectl get svc
```
To Access the Front-end use the link for the LoadBalancer of twitter-front end.


To Open Grafana Dashboard use the following command and follow the url with the port mentioned.
```sh
kubectl get svc -n monitoring
```
## PWA Installation steps:
You will find a '+' symbol to right of the URL on Chrome Browser. Cick on it and install the app on to your device. 
If you don’t find a '+' to the right of the URL bar after going in to the deployed website then follow these steps.

1. Open Chrome browser and type the following in the url (chrome://flags/#unsafely-treat-insecure-origin-as-secure)
2. Enable the "Insecure origins treated as secure" by adding the deployed application's URL in the input field.
3. Now reload browser and you will see '+' symbol to right of the URL. Cick on it and install the app on to your device.

## Delete Cluster:

To delete the cluster run the destroy.sh script found in the infrastructure directory.

```sh
./destroy.sh
```

Docker Images:
- rishijatania/twitter-backend
- rishijatania/twitter-frontend
- rishijatania/twitter-middleware
- rishijatania/twitter-tweet
