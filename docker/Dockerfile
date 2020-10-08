# These examples are setup minimally. In order to reduce repetition, these do not setup
# non-root users and such as they are not intended to run in production anyway.

# We copy files from the context into a scratch container first to avoid a problem where docker and
# docker-compose don't share layer hashes https://github.com/docker/compose/issues/883 normally.
# COPY --from= works around the issue.
FROM scratch as scratch

COPY . /code/

## Use JDK 11 to build projects, as that can still compile Java 6
FROM openzipkin/java:11.0.8-11.41.23 as install

# The final stage to build. Mandatory: ex webmvc4-jetty
ARG target
RUN test -z "$target" && \
    echo "'--build-arg target' must be set to same value as '--target'" && exit 1 || true

WORKDIR /code
COPY --from=scratch /code .

# We copy the result of the build into a target-specific install directory.
# Each end phase looks for this convention. This strategy enforces a match
# between --target and the --build-arg target.
WORKDIR /install/${target}

# invoke the platfom-specific install script and copy the results
RUN cd /code/$target && mvn -q --batch-mode -DskipTests package && \
    platform=$(echo $target|cut -d- -f2) && \
    /code/docker/bin/install-${platform}-example && \
    cp -rp install/* /install/${target}/ && \
# Add start and health check scripts
    mkdir /install/${target}/docker-bin && cd /install/${target}/docker-bin && \
    cp -p /code/docker/bin/docker-healthcheck . && \
    cp -p /code/docker/bin/start-${platform}-example start-brave-example

## Spring WebMVC 2.5/Servlet 2.5/Jetty 7/Java 6 example
FROM azul/zulu-openjdk-alpine:6u119-6.22.0.3 as webmvc25-jetty

WORKDIR /brave-example
COPY --from=install /install/webmvc25-jetty/ .
# Add HEALTHCHECK and ENTRYPOINT scripts into the default search path
RUN mv docker-bin/* /usr/local/bin/ && rmdir docker-bin

EXPOSE 8081 9000

# When using JRE 1.6, we cannot use -XX:+ExitOnOutOfMemoryError
ENV JAVA_OPTS="-Xms16m -Xmx16m"
ENTRYPOINT ["start-brave-example"]

# We use start period of 30s to avoid marking the container unhealthy on slow or contended CI hosts
HEALTHCHECK --interval=1s --start-period=30s --timeout=5s CMD ["docker-healthcheck"]

## Spring WebMVC 3/Servlet 3.0/Jetty 8/Java 7 example
FROM openzipkin/java:1.7.0_261-b02-jre as webmvc3-jetty

WORKDIR /brave-example
COPY --from=install /install/webmvc3-jetty/ .
# Add HEALTHCHECK and ENTRYPOINT scripts into the default search path
RUN mv docker-bin/* /usr/local/bin/ && rmdir docker-bin

EXPOSE 8081 9000

ENV JAVA_OPTS="-Xms16m -Xmx16m -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["start-brave-example"]

# We use start period of 30s to avoid marking the container unhealthy on slow or contended CI hosts
HEALTHCHECK --interval=1s --start-period=30s --timeout=5s CMD ["docker-healthcheck"]

## Spring WebMVC 4/Servlet 3.1/Jetty 9/Java 8 example
FROM openzipkin/java:1.8.0_252-b09-jre as webmvc4-jetty

WORKDIR /brave-example
COPY --from=install /install/webmvc4-jetty/ .
# Add HEALTHCHECK and ENTRYPOINT scripts into the default search path
RUN mv docker-bin/* /usr/local/bin/ && rmdir docker-bin

EXPOSE 8081 9000

ENV JAVA_OPTS="-Xms16m -Xmx16m -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["start-brave-example"]

# We use start period of 30s to avoid marking the container unhealthy on slow or contended CI hosts
HEALTHCHECK --interval=1s --start-period=30s --timeout=5s CMD ["docker-healthcheck"]

## Spring WebMVC 4/Spring Boot 1.5/Java 8 example
FROM openzipkin/java:1.8.0_252-b09-jre as webmvc4-boot

WORKDIR /brave-example
COPY --from=install /install/webmvc4-boot/ .
# Add HEALTHCHECK and ENTRYPOINT scripts into the default search path
RUN mv docker-bin/* /usr/local/bin/ && rmdir docker-bin

EXPOSE 8081 9000

ENV JAVA_OPTS="-Xms32m -Xmx64m -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["start-brave-example"]

# We use start period of 30s to avoid marking the container unhealthy on slow or contended CI hosts
HEALTHCHECK --interval=1s --start-period=30s --timeout=5s CMD ["docker-healthcheck"]