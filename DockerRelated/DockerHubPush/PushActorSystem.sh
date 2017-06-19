cd ../../ActorSystem
sbt docker:stage
cd target/docker/stage
docker build -t htwg/actorsystem:$(VERSION) .
echo "push to docker"
docker push htwg/actorsystem:$(VERSION)