package chalpal.co.uk.chalpal;


import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

// This class is used to create a PostgreSQL docker container that we can use to run our JPA & Integration tests against.
// This heavily utilizes the test-container dependency
// This class will be invoked at exactly the correct moment (before Spring Data JPA autoconfiguration is applied). This means that
// Spring will use our container below as a Datasource rather than the postgres container specified in resources/application.properties
public class PostgresDataSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Container
    static final PostgreSQLContainer<?> database = //
            new PostgreSQLContainer<>("postgres:9.6.12") //
                    .withUsername("postgres");

    // Start the database before we try to pull metadata about it in the initialize method below
    static {
        database.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext
                                   applicationContext) {

        // Add metadata about our Postgres container to the application context
        TestPropertySourceUtils.
                addInlinedPropertiesToEnvironment(applicationContext,
                        "spring.datasource.url=" + database.getJdbcUrl(),
                        "spring.datasource.username="+database.getUsername(),
                        "spring.datasource.password="+database.getPassword(),
                        "spring.jpa.hibernate.ddl-auto=create-drop");
    }
}