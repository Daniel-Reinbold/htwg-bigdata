echo "creating docker machine nodemanager"
docker-machine create --driver virtualbox --virtualbox-memory "4096" --virtualbox-cpu-count "2" nodemanager 

echo "Opening/forwarding required ports for docker swarm" #https://docs.docker.com/engine/swarm/swarm-tutorial/#open-protocols-and-ports-between-the-hosts
#TCP port 2377 for cluster management communications
VBoxManage controlvm "nodemanager" natpf1 "TCP:2377-ClusterManagement,tcp,,2377,,2377"
#TCP and UDP port 7946 for communication among nodes
VBoxManage controlvm "nodemanager" natpf1 "TCP:7946-NodeCommunication,tcp,,7946,,7946"
VBoxManage controlvm "nodemanager" natpf1 "UDP:7946-NodeCommunication,udp,,7946,,7946"
#UDP port 4789 for overlay network traffic
VBoxManage controlvm "nodemanager" natpf1 "UDP:4789-OverlayNetworkTraffic,udp,,4789,,4789"

echo "creating swarm"
docker-machine ssh nodemanager docker swarm init --advertise-addr $(docker-machine ip nodemanager)

echo "switching docker machine to nodemanager"
eval $(docker-machine env nodemanager)
export NODEMANAGERIP=192.168.2.109
export NODEMANAGERVBIP=$(docker-machine ip nodemanager)

echo "######################################################################################"
echo "installing Kafka/ZooKeeper cluster"
echo "portforwarding 2181 (zookeeper)"
VBoxManage controlvm "nodemanager" natpf1 "TCP:2181-ZooKeeper,tcp,,2181,,2181"
echo "portforwarding 9092 (kafka)"
VBoxManage controlvm "nodemanager" natpf1 "TCP:9092-Kafka,tcp,,9092,,9092"
echo "portforwarding 1099 (kafka-jmx port)"
VBoxManage controlvm "nodemanager" natpf1 "TCP:1099-Kafka-JMX,tcp,,1099,,1099"
echo "portforwarding 9000 (kafka-manager)"
VBoxManage controlvm "nodemanager" natpf1 "TCP:9000-KafkaManager,tcp,,9000,,9000"
echo "deploying kafka/zookeeper cluster stack"
docker stack deploy --compose-file=kafka-compose.yml kafka 


echo "######################################################################################"
echo "installing Flink cluster"
echo "portforwarding 8081 (flink-UI)"
VBoxManage controlvm "nodemanager" natpf1 "TCP:8081-Flink-UI,tcp,,8081,,8081"
echo "portforwarding 6121 (flink-Data-TaskManager)"
VBoxManage controlvm "nodemanager" natpf1 "TCP:6121-Flink-Data-TaskManager,tcp,,6121,,6121"
echo "portforwarding 6122 (flink-RPC-TaskManager)"
VBoxManage controlvm "nodemanager" natpf1 "TCP:6122-Flink-RPC-TaskManager,tcp,,6122,,6122"
echo "portforwarding 6123 (flink-RPC-JobManager)"
VBoxManage controlvm "nodemanager" natpf1 "TCP:6123-Flink-RPC-JobManager,tcp,,6123,,6123"
echo "portforwarding 6124 (flink-Blob)"
VBoxManage controlvm "nodemanager" natpf1 "TCP:6124-Flink-Blob,tcp,,6124,,6124"
echo "portforwarding 6125 (flink-Query)"
VBoxManage controlvm "nodemanager" natpf1 "TCP:6125-Flink-Query,tcp,,6125,,6125"
echo "deploying flink cluster stack"
docker stack deploy --compose-file=flink-compose.yml flink


echo "######################################################################################"
echo "installing ElasticSearch/Kibana cluster"
echo "portforwarding 9200 (elasticsearch rest)"
VBoxManage controlvm "nodemanager" natpf1 "TCP:9200-ElasticSearch,tcp,,9200,,9200"
echo "portforwarding 9300 (elasticsearch internal com)"
VBoxManage controlvm "nodemanager" natpf1 "TCP:9300-ElasticSearch,tcp,,9300,,9300"
echo "raising max virtual memory (elasticsearch)"
docker-machine ssh nodemanager sudo sysctl -w vm.max_map_count=1966080 #262144
echo "portforwarding 5601 (kibana)"
VBoxManage controlvm "nodemanager" natpf1 "TCP:5601-Kibana,tcp,,5601,,5601"
echo "deploying elasticsearch cluster/kibana stack"
docker stack deploy --compose-file=esk-compose.yml esk

echo "######################################################################################"
echo "installing Ant MainServer + ActorServer + ActorSystem"
VBoxManage controlvm "nodemanager" natpf1 "TCP:27020-MainServer,tcp,,27020,,27020"
VBoxManage controlvm "nodemanager" natpf1 "TCP:27021-WorkerServer,tcp,,27021,,27021"
docker stack deploy --compose-file=ants-compose.yml ants
