#!/bin/bash

if [ $# -ne 2 ]; then
    echo "Usage: $0 <image_name> <image_tag>"
    exit 1
fi

# Create the .env file
echo "IMAGE_TAG=$2" > .env
echo "IMAGE_NAME=$1" >> .env

# Run docker-compose up
docker-compose up -d

# Remove the .env file
rm .env