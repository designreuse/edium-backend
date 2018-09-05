#!/usr/bin/env bash
deploystart=$(date +%s)
timeout=3000 # Seconds to wait before error. If it's taking awhile - your boxes probably are too small.
threshhold=$((deploystart + timeout))
while true; do
    # Check for timeout
    timenow=$(date +%s)
    if [[ "$timenow" > "$threshhold" ]]; then
        echo "Timeout - $timeout seconds elapsed"
        exit 1
    fi

    # See what's deployed
    echo $(aws elasticbeanstalk describe-environments --application-name $APP_NAME --environment-name $ENV_NAME)
    current_version=`aws elasticbeanstalk describe-environments --application-name $APP_NAME --environment-name $ENV_NAME --query "Environments[*].VersionLabel" --output text`

    status=`aws elasticbeanstalk describe-environments --application-name $APP_NAME --environment-name $ENV_NAME --query "Environments[*].Status" --output text`

    if [ "$current_version" != "$VERSION" ]; then
        echo "Tag not updated (currently $current_version). Waiting."
        sleep 10
        continue
    fi
    if [ "$status" != "Ready" ]; then
        echo "System not Ready -it's $status. Waiting."
        sleep 10
        continue
    fi
    break
done