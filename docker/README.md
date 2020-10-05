## Running images

The only argument to the image is the name of the service, which is `frontend` or `backend`.

Here's an example, using the [webmvc4-boot](../webmvc4-boot) configuration:
```yaml
services:
  # Generate traffic by hitting http://localhost:8081
  frontend:
    container_name: frontend
    image: openzipkin/example-brave:webmvc4-boot
    command: frontend
    ports:
      - 8081:8081
    depends_on:
      backend:
        condition: service_started
  backend:
    container_name: backend
    image: openzipkin/example-brave:webmvc4-boot
    command: backend
```

## Building images

To build `openzipkin/example-brave`, from the top level of the repository, run:

To build the Spring Boot 1.5 with WebMVC 4 example:
```bash
$ docker build -t openzipkin/example-brave:webmvc4-boot -f docker/Dockerfile . --target webmvc4-boot
```
