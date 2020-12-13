#!/bin/bash
# Prometheus Deployment
kubectl create namespace monitoring &&
kubectl create -f clusterRole.yaml -n monitoring &&
kubectl create -f config-map.yaml -n monitoring &&
kubectl create -f prometheus-deployment.yaml -n monitoring &&
kubectl create -f prometheus-service.yaml -n monitoring &&

# Grafana Deployment
kubectl create -f grafana-datasource-config.yaml -n monitoring &&
kubectl create -f grafana-datasource-deploy.yaml -n monitoring &&
kubectl create -f grafana-datasource-service.yaml -n monitoring &&

kubectl apply -f kube-state-metrics/ 

# kubectl get deployments -n monitoring
# kubectl get svc -n monitoring
# kubectl get svc -n monitoring