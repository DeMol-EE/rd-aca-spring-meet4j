# Meet4j (Spring)

## Database (Postgres)

### Docker

```shell
docker run --detach --name pg-sb-tst --rm -p 5434:5432 \
    -e POSTGRES_PASSWORD=acaddemicts -e POSTGRES_USER=acaddemicts \
    -e POSTGRES_DB=meet4j postgres:alpine
```

For testing, simply run a second instance by repeating the same command with a different port binding.

## Properties

You need to pass the following properties:

* SPRING_DATASOURCE_URL
* SPRING_DATASOURCE_USERNAME
* SPRING_DATASOURCE_PASSWORD
* MEET4J_EMAIL_URL
* MEET4J_EMAIL_USERNAME
* MEET4J_EMAIL_PASSWORD