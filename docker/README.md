## Running images

The only argument to the image is the name of the service, which is `frontend` or `backend`.

Here's an example, using the [armeria](../armeria) configuration:
```yaml
version: '2.4'
services:
  # Generate traffic by hitting http://localhost:8081
  frontend:
    container_name: frontend
    image: ghcr.io/openzipkin/brave-example:armeria
    entrypoint: start-frontend
    ports:
      - 8081:8081
    depends_on:
      backend:
        condition: service_healthy
      zipkin:
        condition: service_healthy
  backend:
    container_name: backend
    image: ghcr.io/openzipkin/brave-example:armeria
    entrypoint: start-backend
    depends_on:
      zipkin:
        condition: service_healthy
  zipkin:
    image: ghcr.io/openzipkin/zipkin-slim
    container_name: zipkin
    ports:
      - 9411:9411
```

When using Docker directly, ensure you pass an appropriate `entrypoint` like above. For example:
```bash
docker run --rm --entrypoint start-frontend ghcr.io/openzipkin/brave-example:armeria
```

## Building images

The build-arg `version` controls which project is built.

Ex. To build the Armeria example as the image 'openzipkin/brave-example:test':
```bash
$ docker build -f docker/Dockerfile --build-arg version=armeria -t openzipkin/brave-example:test .
```
