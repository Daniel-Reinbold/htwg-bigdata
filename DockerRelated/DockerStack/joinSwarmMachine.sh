NodeManagerIP="192.168.2.109"
SwarmToken="SWMTKN-1-3zxgyk0tcojyjdfmi5s1wsj2dzxd2mpl4vd0rlo22qyi49vu9p-4eho6pv7w1r2ogdxqh8e8zo8g"
NodeManagerPort="2377"

echo "create docker-machine nodeworker"
docker-machine create --driver virtualbox --virtualbox-memory "4096" --virtualbox-cpu-count "2" nodeworker 

echo "Open/forward required ports for docker swarm" #https://docs.docker.com/engine/swarm/swarm-tutorial/#open-protocols-and-ports-between-the-hosts
#TCP port 2377 for cluster management communications
VBoxManage controlvm "nodeworker" natpf1 "TCP:2377-ClusterManagement,tcp,,2377,,2377"
#TCP and UDP port 7946 for communication among nodes
VBoxManage controlvm "nodeworker" natpf1 "TCP:7946-NodeCommunication,tcp,,7946,,7946"
VBoxManage controlvm "nodeworker" natpf1 "UDP:7946-NodeCommunication,udp,,7946,,7946"
#UDP port 4789 for overlay network traffic
VBoxManage controlvm "nodeworker" natpf1 "UDP:4789-OverlayNetworkTraffic,udp,,4789,,4789"

echo "join swarm"
docker-machine ssh nodeworker docker swarm join --token $SwarmToken $NodeManagerIP:$NodeManagerPort

#docker-machine ssh nodeworker docker swarm join --token SWMTKN-1-4x95wts3nkpnkgdeyzm5yd9o9bvr3moa4kyvz88o5n02nha1f1-5qgpau1doy63mpk829hue38f0 192.168.2.109:2377

echo "######################################################################################"
echo "portforwarding Kafka/ZooKeeper cluster"
echo "portforwarding 2181 (zookeeper)"
VBoxManage controlvm "nodeworker" natpf1 "TCP:2181-ZooKeeper,tcp,,2181,,2181"
echo "portforwarding 9092 (kafka)"
VBoxManage controlvm "nodeworker" natpf1 "TCP:9092-Kafka,tcp,,9092,,9092"


echo "######################################################################################"
echo "portforwarding Flink cluster"
echo "portforwarding 8081 (flink-UI)"
VBoxManage controlvm "nodeworker" natpf1 "TCP:8081-Flink-UI,tcp,,8081,,8081"
echo "portforwarding 6121 (flink-Data-TaskManager)"
VBoxManage controlvm "nodeworker" natpf1 "TCP:6121-Flink-Data-TaskManager,tcp,,6121,,6121"
echo "portforwarding 6122 (flink-RPC-TaskManager)"
VBoxManage controlvm "nodeworker" natpf1 "TCP:6122-Flink-RPC-TaskManager,tcp,,6122,,6122"
echo "portforwarding 6123 (flink-RPC-JobManager)"
VBoxManage controlvm "nodeworker" natpf1 "TCP:6123-Flink-RPC-JobManager,tcp,,6123,,6123"
echo "portforwarding 6124 (flink-Blob)"
VBoxManage controlvm "nodeworker" natpf1 "TCP:6124-Flink-Blob,tcp,,6124,,6124"
echo "portforwarding 6125 (flink-Query)"
VBoxManage controlvm "nodeworker" natpf1 "TCP:6125-Flink-Query,tcp,,6125,,6125"


echo "######################################################################################"
echo "portforwarding ElasticSearch/Kibana cluster"
echo "portforwarding 9200 (elasticsearch rest)"
VBoxManage controlvm "nodeworker" natpf1 "TCP:9200-ElasticSearch,tcp,,9200,,9200"
echo "portforwarding 9300 (elasticsearch internal com)"
VBoxManage controlvm "nodeworker" natpf1 "TCP:9300-ElasticSearch,tcp,,9300,,9300"
echo "raising max virtual memory (elasticsearch)"
docker-machine ssh nodeworker sudo sysctl -w vm.max_map_count=262144
echo "portforwarding 5601 (kibana)"
VBoxManage controlvm "nodeworker" natpf1 "TCP:5601-Kibana,tcp,,5601,,5601"

echo "######################################################################################"
echo "portforwarding Ant Mainserver/Actorsystem"
VBoxManage controlvm "nodeworker" natpf1 "TCP:27020-MainServer,tcp,,27020,,27020"
VBoxManage controlvm "nodeworker" natpf1 "TCP:27021-WorkerServer,tcp,,27021,,27021"