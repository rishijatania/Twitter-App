#!/bin/bash
kubectl apply -f twitter-backend-secrets.yml &&
kubectl apply -f twitter-backend-config.yml &&
kubectl apply -f twitter-backend-deployment.yml &&
kubectl apply -f service-twitter-backend-lb.yml &&
kubectl apply -f twitter-tweet-config.yml &&
kubectl apply -f twitter-tweet-deployment.yml && 
kubectl apply -f service-twitter-tweet-lb.yml &&
kubectl apply -f twitter-queue-deployment.yml &&
kubectl apply -f service-twitter-queue-lb.yml &&
kubectl apply -f twitter-middleware-secrets.yml &&
kubectl apply -f twitter-middleware-config.yml &&
kubectl apply -f twitter-middleware-deployment.yml &&
kubectl apply -f service-twitter-middleware-lb.yml &&
kubectl apply -f twitter-frontend-deployment.yml &&
kubectl apply -f service-twitter-frontend-lb.yml 
# kubectl apply -f metrics-server.yml &&
# kubectl autoscale deployment twitter-backend --cpu-percent=40 --min=1 --max=5 &&

sleep 4
kubectl get pods &&
kubectl get svc