cd ../../MainServer
sbt docker:stage
cd target/docker/stage
docker build -t htwg/mainserver:$(VERSION) .
echo "push to docker"
docker push htwg/mainserver:$(VERSION)