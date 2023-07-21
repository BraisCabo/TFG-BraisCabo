#!/bin/bash

MAX_WAIT_SECONDS=600

elapsed_seconds=0

while ! nc -z localhost 8443; do
  printf "\n  => Waiting for application to start - IP: http://localhost and PORT: '8443'"
  sleep 1s
  ((elapsed_seconds++))

  if [ "$elapsed_seconds" -ge "$MAX_WAIT_SECONDS" ]; then
    printf "\n  => Application took too long to start. Exiting...\n"
    exit 1
  fi
done