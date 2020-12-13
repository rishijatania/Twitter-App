#!/bin/bash
echo "Destroy Script"

#Kube Destroy deployment and services
cd ./kubernetes/deployment_services
. ./kube-destroy.sh

# Terraform
cd ../../terraform
echo "Terrafrom destroy"
FILE=./vars.tfvars
if [ -f "$FILE" ]; then
	terraform destroy -var-file="vars.tfvars" --auto-approve
else 
	terraform destroy --auto-approve
fi

echo "Thank you for using Twitter App! Good Bye......."