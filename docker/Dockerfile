# These examples are setup minimally. In order to reduce repetition, these do not setup
# non-root users and such as they are not intended to run in production anyway.

## Use JDK 11 to build projects, as that can still compile Java 6
FROM openzipkin/java:11.0.8-11.41.23 as built

# Copy and build the source
WORKDIR /code
COPY . .

ARG jetty_examples="webmvc25 webmvc3 webmvc4"
ARG boot_examples=webmvc4-boot
ARG all_examples="webmvc25 webmvc3 webmvc4 webmvc4-boot"

# All projects build similarly. The difference is in what is packaged
RUN for project in $all_examples; do cd ${project} && \
      mvn -q --batch-mode -DskipTests package && \
      mkdir -p /built/${project}/docker-bin && \
      cp -p /code/docker/bin/docker-healthcheck /built/${project}/docker-bin/ && \
    cd - ; done

# Jetty based projects use jetty-runner. The built project is the root web context.
RUN for project in $jetty_examples; do cd /built/${project} && \
      # Get the same version of jetty-runner as the Maven project uses
      mvn_property() { mvn -f /code/${project} help:evaluate -Dexpression=$1 -q -DforceStdout;} && \
      jettyGroupId=$(mvn_property jetty.groupId) && \
      jettyVersion=$(mvn_property jetty.version) && \
      # Use maven to get the jetty-runner so it is cached if multiple projects use it
      mvn -q --batch-mode org.apache.maven.plugins:maven-dependency-plugin:get \
          -Dtransitive=false -Dartifact=${jettyGroupId}:jetty-runner:${jettyVersion}:jar && \
      # Unzip the jetty-runner so that it is faster to start
      find ~/.m2/repository -name jetty-runner-${jettyVersion}.jar -exec jar xf {} \; && \
      # Add the start script and the root war
      cp -p /code/docker/bin/start-jetty-example docker-bin/start-brave-example && \
      sed -i "s/org.eclipse.jetty/${jettyGroupId}/~g" docker-bin/start-brave-example && \
      cp -r /code/${project}/target/*-SNAPSHOT root && \
    cd - ; done

# Spring Boot based projects unzip the "exec" jar as it is faster at runtime.
RUN for project in $boot_examples; do cd /built/${project} && \
      cp -p /code/docker/bin/start-boot-example docker-bin/start-brave-example && \
      jar xf /code/${project}/target/*-exec.jar && \
    cd - ; done

## Spring WebMVC 2.5/Servlet 2.5/Jetty 7/Java 6 example
FROM azul/zulu-openjdk-alpine:6u119-6.22.0.3 as webmvc25

WORKDIR /brave-example
COPY --from=built /built/webmvc25/ .
# Add HEALTHCHECK and ENTRYPOINT scripts into the default search path
RUN mv docker-bin/* /usr/local/bin/ && rmdir docker-bin

EXPOSE 8081 9000

# When using JRE 1.6, we cannot use -XX:+ExitOnOutOfMemoryError
ENV JAVA_OPTS="-Xms16m -Xmx16m"
ENTRYPOINT ["start-brave-example"]

# We use start period of 30s to avoid marking the container unhealthy on slow or contended CI hosts
HEALTHCHECK --interval=1s --start-period=30s --timeout=5s CMD ["docker-healthcheck"]

## Spring WebMVC 3/Servlet 3.0/Jetty 8/Java 7 example
FROM openzipkin/java:1.7.0_261-b02-jre as webmvc3

WORKDIR /brave-example
COPY --from=built /built/webmvc3/ .
# Add HEALTHCHECK and ENTRYPOINT scripts into the default search path
RUN mv docker-bin/* /usr/local/bin/ && rmdir docker-bin

EXPOSE 8081 9000

ENV JAVA_OPTS="-Xms16m -Xmx16m -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["start-brave-example"]

# We use start period of 30s to avoid marking the container unhealthy on slow or contended CI hosts
HEALTHCHECK --interval=1s --start-period=30s --timeout=5s CMD ["docker-healthcheck"]

## Spring WebMVC 4/Servlet 3.1/Jetty 9/Java 7 example
FROM openzipkin/java:1.7.0_261-b02-jre as webmvc4

WORKDIR /brave-example
COPY --from=built /built/webmvc4/ .
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
COPY --from=built /built/webmvc4-boot/ .
# Add HEALTHCHECK and ENTRYPOINT scripts into the default search path
RUN mv docker-bin/* /usr/local/bin/ && rmdir docker-bin

EXPOSE 8081 9000

ENV JAVA_OPTS="-Xms32m -Xmx64m -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["start-brave-example"]

# We use start period of 30s to avoid marking the container unhealthy on slow or contended CI hosts
HEALTHCHECK --interval=1s --start-period=30s --timeout=5s CMD ["docker-healthcheck"]