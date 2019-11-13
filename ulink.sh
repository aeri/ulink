#!/bin/bash
# must be executed with root privilege
gradle assemble
docker build -t ulink-spring .
docker-compose up
docker-compose down