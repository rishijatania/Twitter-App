#!/bin/bash
read -p "Enter backend secrets filename [twitter-backend-secrets.yml]" backend_sec_f
backend_sec_f=${backend_sec_f:-twitter-backend-secrets.yml}

read -p "Enter middleware secrets filename [twitter-middleware-secrets.yml]" middleware_sec_f
middleware_sec_f=${middleware_sec_f:-twitter-middleware-secrets.yml}

read -p "Enter backend configmap filename [twitter-backend-config.yml]" backend_con_f
backend_con_f=${backend_con_f:-twitter-backend-config.yml}

read -p "Enter tweet service configmap filename [twitter-tweet-config.yml]" tweet_con_f
tweet_con_f=${tweet_con_f:-twitter-tweet-config.yml}

echo "Enter MongoDB Atlas Cluster Details"
read -p 'Connection URI: ' mongoUri
mongoUri=$(printf "%s" "$mongoUri" | base64)

if [[ $backend_sec_f != "" && $mongoUri != "" ]]; then
	# Mac command specifics
	if [[ "$OSTYPE" == "darwin"* ]]; then
		sed -i '.bak' "s/{{ MongoDB.Cluster.URI }}/$mongoUri/g" $backend_sec_f
	else # Linux 
		sed -i "s/{{ MongoDB.Cluster.URI }}/$mongoUri/g" $backend_Sec_F
	fi
else
	exit 1
fi

read -p 'MongoDB DB Name: ' mongoDBName

if [[ $backend_con_f != "" && $mongoDBName != "" ]]; then
	# Mac command specifics
	if [[ "$OSTYPE" == "darwin"* ]]; then
		sed -i '.bak' "s/{{ MongoDB.DB.Name }}/$mongoDBName/g" $backend_con_f
	else # Linux 
		sed -i "s/{{ MongoDB.DB.Name }}/$mongoDBName/g" $backend_con_f
	fi
else
	exit 1
fi

if [[ $tweet_con_f != "" && $mongoDBName != "" ]]; then
	# Mac command specifics
	if [[ "$OSTYPE" == "darwin"* ]]; then
		sed -i '.bak' "s/{{ MongoDB.DB.Name }}/$mongoDBName/g" $tweet_con_f
	else # Linux 
		sed -i "s/{{ MongoDB.DB.Name }}/$mongoDBName/g" $tweet_con_f
	fi
else
	exit 1
fi

echo "Enter Slack Webhook Details"
read -p 'Webhook URI: ' slackUri
slackUri=$(printf "%s" "$slackUri" | base64)

if [[ $backend_sec_f != "" && $slackUri != "" && $middleware_sec_f != "" ]]; then
	# Mac command specifics
	if [[ "$OSTYPE" == "darwin"* ]]; then
		sed -i '.bak' "s/{{ Slack.Webhook.URI }}/$slackUri/g" $backend_sec_f
		sed -i '.bak' "s/{{ Slack.Webhook.URI }}/$slackUri/g" $middleware_sec_f
	else # Linux 
		sed -i "s/{{ Slack.Webhook.URI }}/$slackUri/g" $backend_sec_f
		sed -i "s/{{ Slack.Webhook.URI }}/$slackUri/g" $middleware_sec_f
	fi
else
	exit 1
fi

echo "Enter JWT Secret"
read -p 'Jwt Secret: ' jwt_secret
jwt_secret=$(printf "%s" "$jwt_secret" | base64)

if [[ $backend_sec_f != "" && $jwt_secret != "" ]]; then
	# Mac command specifics
	if [[ "$OSTYPE" == "darwin"* ]]; then
		sed -i '.bak' "s/{{ App.Secrets.JWT }}/$jwt_secret/g" $backend_sec_f
	else # Linux 
		sed -i "s/{{ App.Secrets.JWT }}/$jwt_secret/g" $backend_sec_f
	fi
else
	exit 1
fi

echo "Enter Rapid Api Key"
read -p 'Rapid Api Key: ' rapid_api_key
rapid_api_key=$(printf "%s" "$rapid_api_key" | base64)

if [[ $middleware_sec_f != "" && $rapid_api_key != "" ]]; then
	# Mac command specifics
	if [[ "$OSTYPE" == "darwin"* ]]; then
		sed -i '.bak' "s/{{ Rapid.Api.Key }}/$rapid_api_key/g" $middleware_sec_f
	else # Linux 
		sed -i "s/{{ Rapid.Api.Key }}/$rapid_api_key/g" $middleware_sec_f
	fi
else
	exit 1
fi

echo "Enter AWS Credentials"
read -p 'AWS AWS_ACCESS_KEY_ID: ' aws_access_key
aws_access_key=$(printf "%s" "$aws_access_key" | base64)

if [[ $backend_sec_f != "" && $aws_access_key != "" ]]; then
	# Mac command specifics
	if [[ "$OSTYPE" == "darwin"* ]]; then
		sed -i '.bak' "s/{{ AWS.Access.Key.Id }}/$aws_access_key/g" $backend_sec_f
	else # Linux 
		sed -i "s/{{ AWS.Access.Key.Id }}/$aws_access_key/g" $backend_sec_f
	fi
else
	exit 1
fi

read -p 'AWS AWS_SECRET_ACCESS_KEY: ' aws_secret_key
aws_secret_key=$(printf "%s" "$aws_secret_key" | base64)

if [[ $backend_sec_f != "" && $aws_secret_key != "" ]]; then
	# Mac command specifics
	if [[ "$OSTYPE" == "darwin"* ]]; then
		sed -i '.bak' "s/{{ AWS.Secret.Access.Key }}/$aws_secret_key/g" $backend_sec_f
	else # Linux 
		sed -i "s/{{ AWS.Secret.Access.Key }}/$aws_secret_key/g" $backend_sec_f
	fi
else
	exit 1
fi

read -p 'AWS BUCKET_NAME: ' aws_bucket_name

if [[ $backend_con_f != "" && $aws_bucket_name != "" ]]; then
	# Mac command specifics
	if [[ "$OSTYPE" == "darwin"* ]]; then
		sed -i '.bak' "s/{{ AWS.Bucket.Name }}/$aws_bucket_name/g" $backend_con_f
	else # Linux 
		sed -i "s/{{ AWS.Bucket.Name }}/$aws_bucket_name/g" $backend_con_f
	fi
else
	exit 1
fi

if [[ $tweet_con_f != "" && $aws_bucket_name != "" ]]; then
	# Mac command specifics
	if [[ "$OSTYPE" == "darwin"* ]]; then
		sed -i '.bak' "s/{{ AWS.Bucket.Name }}/$aws_bucket_name/g" $tweet_con_f
	else # Linux 
		sed -i "s/{{ AWS.Bucket.Name }}/$aws_bucket_name/g" $tweet_con_f
	fi
else
	exit 1
fi