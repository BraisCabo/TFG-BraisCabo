#!/bin/bash

while ! nc -z "http://localhost" 8443; do
  printf "\n  => Waiting for application to start - IP: http://localhost and PORT: '8443'"
  sleep 1s
done