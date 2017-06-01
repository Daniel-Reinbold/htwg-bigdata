# Kafka

##Start a cluster:

docker-compose up -d

##Add more brokers:

docker-compose scale kafka=3

##Stop a cluster:

docker-compose stop

## Get Docker Host IP

docker-machine ip default

## Start Kafka Shell

$ start-kafka-shell.sh <DOCKER_HOST_IP> <ZK_HOST:ZK_PORT>

## Kafka Manager UI

Docker: https://hub.docker.com/r/sheepkiller/kafka-manager/
Git: https://github.com/yahoo/kafka-manager


