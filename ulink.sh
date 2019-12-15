#!/bin/bash
# must be executed with root privilege
gradle assemble
docker build -t ulink-spring .
docker-compose -f ./docker/docker-compose/docker-compose.yml up pgmaster pgslave1 pgslave2 pgslave3 pgslave4 pgpool backup web adminer prometheus alertmanager nodeexporter cadvisor grafana pushgateway caddy
docker-compose -f ./docker/docker-compose/docker-compose.yml down
