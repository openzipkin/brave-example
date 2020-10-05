ARG java_version=15.0.0-15.27.17

# We copy files from the context into a scratch container first to avoid a problem where docker and
# docker-compose don't share layer hashes https://github.com/docker/compose/issues/883 normally.
# COPY --from= works around the issue.

FROM scratch as scratch

COPY . /code/

FROM openzipkin/java:${java_version} as built

# Copy and build the source
WORKDIR /code
COPY --from=scratch /code .
RUN for TARGET in webmvc4-boot; do cd ${TARGET} && \
      mvn -q --batch-mode -DskipTests package && \
      JAR=/code/${TARGET}/target/*-exec.jar && \
      (mkdir -p /built/${TARGET} && cd /built/${TARGET} && jar xf ${JAR}) && \
    cd - ; done

# Copy scripts used in the Dockerfile
RUN cp -r /code/docker/bin/ /built/

# Almost everything is common between variants
FROM openzipkin/java:${java_version}-jre as brave-example

# Use to set heap, trust store or other system properties.
ENV JAVA_OPTS -Xms32m -Xmx64m -XX:+ExitOnOutOfMemoryError

# All content including binaries and logs write under WORKDIR
ARG USER=brave-example
WORKDIR /${USER}

# Ensure the process doesn't run as root
RUN adduser -g '' -h ${PWD} -D ${USER}
USER ${USER}

COPY --from=built /built/bin/ /usr/local/bin/

EXPOSE 8081 9000

ENTRYPOINT ["start-brave-example"]

FROM brave-example as webmvc4-boot

COPY --from=built --chown=${USER} /built/webmvc4-boot/ .
