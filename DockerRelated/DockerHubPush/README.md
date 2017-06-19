## sbt docker build + push on docker hub

```sh
# login with your docker id for pushing images to docker hub
$ docker login
...
# export version number e.g. 0.5
$ export VERSION={Version No}
...
# build and push actor system
$ sh PushActorSystem.sh
...
# build and push main server
$ sh PushMainServer.sh
...
# build and push worker server
$ sh PushWorkerServer.sh
...
```