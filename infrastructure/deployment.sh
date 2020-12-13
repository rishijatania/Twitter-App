#!/bin/bash
echo "Welcome to Twitter app"

# Terraform
cd ./terraform
echo "Terrafrom init,plan and apply"
FILE=./vars.tfvars
if [ -f "$FILE" ]; then
    terraform init -var-file="vars.tfvars" &&
	terraform plan -var-file="vars.tfvars" &&
	terraform apply -var-file="vars.tfvars" --auto-approve
else 
    terraform init &&
	terraform plan &&
	terraform apply --auto-approve
fi

echo "Kubernetes Setup"
# Kubectl Setup
terraform output config_map_aws_auth > ../kubernetes/config_map_aws_auth.yaml &&
terraform output kubeconfig > ~/.kube/config-terraform-eks-twitter-app &&
cp ~/.kube/config-terraform-eks-twitter-app ~/.kube/config &&
export KUBECONFIG=~/.kube/config-terraform-eks-twitter-app:~/.kube/config &&
echo "export KUBECONFIG=${KUBECONFIG}" >>${HOME}/.bash_profile &&
sleep 7 &&
kubectl apply -f ../kubernetes/config_map_aws_auth.yaml
cd ..

echo "Please enter following details for a successful deployment"
#Load credentials
echo "Loading credentials and Applying deployment/services"
cd ./kubernetes/deployment_services &&
. ./credentials.sh &&

#Kube Apply deployment and services
. ./kube-deploy.sh