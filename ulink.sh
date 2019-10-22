#!/bin/bash
# must be executed with root privilege
gradle assemble
docker build -t ulink-spring .
docker run -p 8080:8080 ulink-spring