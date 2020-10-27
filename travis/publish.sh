#!/bin/sh -x

set -ue

ALL_PROJECTS=$(ls */pom.xml|sed 's~/pom.xml~~g')
for PROJECT in ${ALL_PROJECTS}; do
  docker/build_image "${PROJECT}"
  IMAGE="openzipkin/brave-example:${PROJECT}"
  docker tag $IMAGE ghcr.io/$IMAGE
  docker push ghcr.io/$IMAGE
done
