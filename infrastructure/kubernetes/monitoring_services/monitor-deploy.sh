#!/bin/bash
kubectl create namespace monitoring &&

# #Alert Manager
# kubectl create -f AlertManagerConfigmap.yaml &&
# kubectl create -f AlertManagerDeployment.yaml &&
# kubectl create -f AlertManagerService.yaml &&

# Prometheus Deployment
kubectl create -f clusterRole.yaml -n monitoring &&
kubectl create -f config-map.yaml -n monitoring &&
kubectl create -f prometheus-deployment.yaml -n monitoring &&
kubectl create -f prometheus-service.yaml -n monitoring &&

# Grafana Deployment
kubectl create -f grafana-datasource-config.yaml -n monitoring &&
kubectl create -f grafana-datasource-deploy.yaml -n monitoring &&
kubectl create -f grafana-datasource-service.yaml -n monitoring &&

kubectl apply -f kube-state-metrics/ 

sleep 4
kubectl get pods -n monitoring
kubectl get svc -n monitoring