#!/bin/bash
docker run --name DataBase --rm -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=keywhale -p 3306:3306 -d mysql