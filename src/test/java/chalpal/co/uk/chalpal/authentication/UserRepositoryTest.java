package chalpal.co.uk.chalpal.authentication;

import chalpal.co.uk.chalpal.PostgresDataSourceInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // Ensures our tests use the PostgreSQL container rather than an inmemory H2 one.
@ContextConfiguration(initializers = PostgresDataSourceInitializer.class) // References our Postgres TestContainer
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private final String userEmail = "an@email.com";

    @BeforeEach
    void setup() {
        userRepository.save(new User(userEmail));
    }


    @Test
    void save_shouldReturnUser_whenUserEmailDoesntExist () {
        String userEmail = "an@emailv2.com";
        User user = userRepository.save(new User(userEmail));

        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(userEmail);
        assertThat(user.getId()).isGreaterThan(0);
    }

    @Test
    void save_ShouldThrowDataIntegrityViolationExcpetion_whenUserWithEmailAlreadyExists() {
        Throwable exception = assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(new User(userEmail)));
    }

    @Test
    void findByEmail_ShouldReturnUser_whenUserWithEmailExistsInDatabase() {
        String userEmail = "an@email.com";

        User user = userRepository.findByEmail(userEmail);
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(userEmail);
        assertThat(user.getId()).isGreaterThan(0);
    }
}
