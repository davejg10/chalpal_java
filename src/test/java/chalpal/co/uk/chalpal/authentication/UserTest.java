package chalpal.co.uk.chalpal.authentication;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserTest {

    @Test
    void newUserShouldHaveNullId() {
        User user = new User("an@email.com");

        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo("an@email.com");
    }

    @Test
    void toStringMethod() {
        User user = new User("an@email.com");
        user.setId(1);

        assertThat(user.toString()).isEqualTo("User{id=1, email=an@email.com}");
    }

    @Test
    void settersShouldMutateState() {
        User user = new User("an@email.com");

        user.setId(2);
        user.setEmail("an@emailv2.com");

        assertThat(user.getId()).isEqualTo(2);
        assertThat(user.getEmail()).isEqualTo("an@emailv2.com");
    }

}
