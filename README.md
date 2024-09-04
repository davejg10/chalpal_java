
# Pre-req
In order to run the application you need the following tools installed

1) Java 17
2) Docker
3) Maven

You might also see some log errors if you do not have the lombok intellij plugin intalled.

# Running the application

You can run the application in a number of ways;

### Using Docker-Compose

A docker-compose.yml file has been created in the root directory that first creates the necessary Postgres container and then runs the actual application as an image. 
For this to work you will need to have built the image which you can do using the Dockerfile located in the root directory:

```
docker build -t chalpal -f Dockerfile .
```

Now you are ready to use docker-compose:

```
docker-compose up
```

The application should now be running.

### Running the application with mvn or in intellij

You will need a Postgres container running locally (Unless we are now running a dev instance in cloud...).


```
docker run --name postgres-container -e POSTGRES_USER=myuser -e POSTGRES_PASSWORD=mypassword -e POSTGRES_DB=mydatabase -p 5432:5432 -d postgres
```

In development or prod environments the database credentials and url will be fetched from a Key Vault and passed as environment variables. Locally, however we dont really care and therefore we can set defaults for these values in application.properties to connect to our local postgres container ensuring we set these values the same as the values passed to the docker command above. 


# Testing the application

To run the tests you must have Docker installed as PostgresDataSourceInitializer.java is a class used to initialize a Postgres Docker container than we can use for testing. It is initialized at exactly the correct moment in order for our tests to be run on a simulated cattle database rather than the dev/prod database declared in application.properties.

### Troubleshooting

You may need to create a docker network:
1) Create a Docker network
```
docker network create mynetwork
```
