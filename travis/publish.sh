#!/bin/sh -x

set -ue

ALL_PROJECTS=$(ls */pom.xml|sed 's~/pom.xml~~g')
for PROJECT in ${ALL_PROJECTS}; do
  docker/build_image "${PROJECT}" "ghcr.io/openzipkin/brave-example:${PROJECT}" push
done
