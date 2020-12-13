#!/bin/bash
# Prometheus Deployment
kubectl delete -f clusterRole.yaml -n monitoring &&
kubectl delete -f config-map.yaml -n monitoring &&
kubectl delete -f prometheus-deployment.yaml -n monitoring &&
kubectl delete -f prometheus-service.yaml -n monitoring &&

# Grafana Deployment
kubectl delete -f grafana-datasource-config.yaml -n monitoring &&
kubectl delete -f grafana-datasource-deploy.yaml -n monitoring &&
kubectl delete -f grafana-datasource-service.yaml -n monitoring &&

kubectl delete -f kube-state-metrics/ 

kubectl delete namespace monitoring