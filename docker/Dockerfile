# These examples are setup minimally. In order to reduce repetition, these do not setup
# non-root users and such as they are not intended to run in production anyway.

# The image binaries this example builds are installed over
ARG docker_parent_image=ghcr.io/openzipkin/java:21.0.10_p7
## Use JDK 11 to build projects, as that can still compile Java 6
ARG docker_build_image=ghcr.io/openzipkin/java:11.0.30_p7

FROM $docker_build_image AS install

WORKDIR /code
# version of the project, matching /code/$version/pom.xml. ex armeria or netty4-grpc
ARG version=armeria
COPY ${version}/pom.xml ${version}/
COPY ${version}/src/main/ ${version}/src/main/
COPY parent-pom.xml m2repository/ ./
COPY build-bin/maven/maven_build build-bin/maven/maven_opts build-bin/maven/maven_unjar build-bin/maven/
COPY build-bin/docker/docker-healthcheck build-bin/docker/
COPY docker/bin/* docker/bin/

# Build the project. This results in:
# * /install - the version home
# * /install/bin - scripts that should be copied into the search path
RUN \
# If we've seeded m2repository, copy it into the repository of our install image.
# This prevents uncacheable downloads that occur during Docker build.
test -d /code/m2repository/de && export MAVEN_OPTS="-Dmaven.repo.local=/code/m2repository"; \
/code/docker/bin/install-example $version

# Make the final layer that the example will run in
FROM $docker_parent_image

# All content including binaries and logs write under WORKDIR
ARG USER=brave-example
WORKDIR /${USER}

# Ensure the process doesn't run as root
RUN adduser -g '' -h ${PWD} -D ${USER}

# Copy binaries and config we installed earlier
COPY --from=install --chown=${USER} /install .

# Add HEALTHCHECK and ENTRYPOINT scripts into the default search path
RUN mv bin/* /usr/local/bin/ && rm -rf bin

# Switch to the runtime user
USER ${USER}

# We use a 30s start period to avoid marking the container unhealthy on slow or contended CI hosts
HEALTHCHECK --interval=1s --start-period=30s --timeout=5s CMD ["docker-healthcheck"]

# Expose the default ports for the frontend and backend
EXPOSE 8081 9000

ENTRYPOINT ["start-frontend"]
