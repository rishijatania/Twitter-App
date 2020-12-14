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
We need [S3 Bucket](https://aws.amazon.com/s3/) for hosting images, [Slack WebHooks](https://api.slack.com/messaging/webhooks) and [RapidAPI](https://rapidapi.com) credentials


## Initial set up
Keep you Docker up and running
Clone the project, and step into the infrastructure and run the command
```sh
./deployment.sh
```
The scripts deploys the infrastructure using terraform scripts and ask for key credentials neededto deploy the application. Once secrets have been entered the script execution continues and deploys K8 cluster services. 

Once the cluster is created check the cluster health with 

```sh
kubectl get svc
```
To Open Grafana Dashboard using the following command and follow the url with the port mentioned.

```sh
kubectl get svc -n monitoring
```
## Delete Cluster:

To delete the cluster run the delete destroy.sh script

```sh
./destroy.sh
```

Docker Images:
- rishijatania/twitter-backend
- rishijatania/twitter-frontend
- rishijatania/twitter-middleware
- rishijatania/twitter-tweet