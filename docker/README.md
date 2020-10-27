## Running images

The only argument to the image is the name of the service, which is `frontend` or `backend`.

Here's an example, using the [armeria](../armeria) configuration:
```yaml
version: '2.4'
services:
  # Generate traffic by hitting http://localhost:8081
  frontend:
    container_name: frontend
    image: openzipkin/brave-example:armeria
    command: frontend
    ports:
      - 8081:8081
    depends_on:
      backend:
        condition: service_healthy
  backend:
    container_name: backend
    image: openzipkin/brave-example:armeria
    command: backend
```

## Building images

To build an example, from the root directory, invoke `docker/build_image YOUR_PROJECT`:

Ex. To build the Armeria example as the image 'openzipkin/brave-example:test'
```bash
$ docker/build_image armeria test
```
