# These examples are setup minimally. In order to reduce repetition, these do not setup
# non-root users and such as they are not intended to run in production anyway.
ARG jre_image

# We copy files from the context into a scratch container first to avoid a problem where docker and
# docker-compose don't share layer hashes https://github.com/docker/compose/issues/883 normally.
# COPY --from= works around the issue.
FROM scratch as scratch

COPY . /code/

## Use JDK 11 to build projects, as that can still compile Java 6
FROM openzipkin/java:11.0.9-11.43.21 as install

ARG project

WORKDIR /code
COPY --from=scratch /code .

# This results in:
# * /install - the project home
# * /install/bin - scripts that should be copied into the search path
RUN /code/docker/bin/install-example $project

# Make the final layer that the example will run in
FROM $jre_image

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

# We use start period of 30s to avoid marking the container unhealthy on slow or contended CI hosts
HEALTHCHECK --interval=1s --start-period=30s --timeout=5s CMD ["docker-healthcheck"]

# Expose the default ports for the frontend and backend
EXPOSE 8081 9000

ENTRYPOINT ["start-brave-example"]
