#!/bin/bash

if [ $# -lt 2 ] || [ $# -gt 3 ]; then
    echo "Usage: $0 <docker_username> <image_name>"
    echo "Optionally: $0 <docker_username> <image_name> <image_tag>"
    exit 1
fi

if [ $# -eq 2 ]; then
    echo "No tag specified, using 'latest'"
    docker build -f Dockerfile -t $1/$2:latest ../
    docker push $1/$2:latest
    exit 0
fi
if [ $# -eq 3 ]; then
    echo "Tag specified, using $3"
    docker build -f Dockerfile -t $1/$2:$3 ../
    docker push $1/$2:$3
    exit 0
fi
```