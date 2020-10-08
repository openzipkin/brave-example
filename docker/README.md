## Running images

The only argument to the image is the name of the service, which is `frontend` or `backend`.

Here's an example, using the [webmvc4-boot](../webmvc4-boot) configuration:
```yaml
services:
  # Generate traffic by hitting http://localhost:8081
  frontend:
    container_name: frontend
    image: openzipkin/example-brave:webmvc4
    command: frontend
    ports:
      - 8081:8081
    depends_on:
      backend:
        condition: service_started
  backend:
    container_name: backend
    image: openzipkin/example-brave:webmvc4
    command: backend
```

## Building images

To build the Spring WebMVC 2.5/Servlet 2.5/Jetty 7/Java 6 example:
```bash
$ docker build -t openzipkin/example-brave:webmvc25 -f docker/Dockerfile . --target webmvc25
```

To build the Spring WebMVC 3/Servlet 3.0/Jetty 8/Java 7 example:
```bash
$ docker build -t openzipkin/example-brave:webmvc3 -f docker/Dockerfile . --target webmvc3
```

To build the Spring WebMVC 4/Servlet 3.1/Jetty 9/Java 7 example:
```bash
$ docker build -t openzipkin/example-brave:webmvc4 -f docker/Dockerfile . --target webmvc4
```

To build the Spring WebMVC 4/Spring Boot 1.5/Java 8 example:
```bash
$ docker build -t openzipkin/example-brave:webmvc4-boot -f docker/Dockerfile . --target webmvc4-boot
```