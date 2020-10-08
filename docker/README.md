## Running images

The only argument to the image is the name of the service, which is `frontend` or `backend`.

Here's an example, using the [webmvc4-jetty](../webmvc4-jetty) configuration:
```yaml
version: '2.4'
services:
  # Generate traffic by hitting http://localhost:8081
  frontend:
    container_name: frontend
    image: openzipkin/example-brave:armeria
    command: frontend
    ports:
      - 8081:8081
    depends_on:
      backend:
        condition: service_healthy
  backend:
    container_name: backend
    image: openzipkin/example-brave:armeria
    command: backend
```

## Building images

To build the Armeria/Java 15 example:
```bash
$ DOCKER_BUILDKIT=1 docker build -t openzipkin/example-brave:armeria -f docker/Dockerfile --target armeria --build-arg target=armeria .
```

To build the Spring WebMVC 2.5/Servlet 2.5/Jetty 7/Java 6 example:
```bash
$ DOCKER_BUILDKIT=1 docker build -t openzipkin/example-brave:webmvc25-jetty -f docker/Dockerfile --target webmvc25-jetty --build-arg target=webmvc25-jetty .
```

To build the Spring WebMVC 3/Servlet 3.0/Jetty 8/Java 7 example:
```bash
$ DOCKER_BUILDKIT=1 docker build -t openzipkin/example-brave:webmvc3-jetty -f docker/Dockerfile --target webmvc3-jetty --build-arg target=webmvc3-jetty .
```

To build the Spring WebMVC 4/Spring Boot 1.5/Java 8 example:
```bash
$ DOCKER_BUILDKIT=1 docker build -t openzipkin/example-brave:webmvc4-boot -f docker/Dockerfile --target webmvc4-boot --build-arg target=webmvc4-boot .
```

To build the Spring WebMVC 4/Servlet 3.1/Jetty 9/Java 8 example:
```bash
$ DOCKER_BUILDKIT=1 docker build -t openzipkin/example-brave:webmvc4-jetty -f docker/Dockerfile --target webmvc4-jetty --build-arg target=webmvc4-jetty .
```
