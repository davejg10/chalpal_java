package chalpal.co.uk.chalpal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // Ensures our tests use the PostgreSQL container rather than an inmemory H2 one.
@ContextConfiguration(initializers = PostgresDataSourceInitializer.class)
class ChalpalApplicationTests {

	@Test
	void contextLoads() {
	}

}
