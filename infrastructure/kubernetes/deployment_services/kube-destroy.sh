#!/bin/bash
kubectl delete -f twitter-backend-secrets.yml &&
kubectl delete -f twitter-backend-config.yml &&
kubectl delete -f twitter-backend-deployment.yml &&
kubectl delete -f service-twitter-backend-lb.yml &&
kubectl delete -f twitter-tweet-config.yml &&
kubectl delete -f twitter-tweet-deployment.yml && 
kubectl delete -f service-twitter-tweet-lb.yml &&
kubectl delete -f twitter-queue-deployment.yml &&
kubectl delete -f service-twitter-queue-lb.yml &&
kubectl delete -f twitter-middleware-secrets.yml &&
kubectl delete -f twitter-middleware-config.yml &&
kubectl delete -f twitter-middleware-deployment.yml &&
kubectl delete -f service-twitter-middleware-lb.yml