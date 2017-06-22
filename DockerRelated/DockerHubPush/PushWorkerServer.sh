cd ../../WorkerServer
sbt docker:stage
cd target/docker/stage
docker build -t htwg/workerserver:$(VERSION) .
echo "push to docker"
docker push htwg/workerserver:$(VERSION)