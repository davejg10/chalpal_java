package chalpal.co.uk.chalpal.authentication;

import chalpal.co.uk.chalpal.exceptions.ClientNullRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void isEmailInvalid_shouldReturnFalse_whenUserEmailIsUnique() {
        String email = "an@email.com";

        Mockito.when(userRepository.findByEmail(email)).thenReturn(null);

        boolean result = userService.isEmailInvalid(null, email);
        assertThat(result).isFalse();
        verify(userRepository, Mockito.times(1)).findByEmail(email);
    }

    @Test
    void isEmailInvalid_shouldReturnTrue_whenUserEmailIsNotUnique() {
        Integer id = 1;
        String email = "an@email.com";
        User existingUser = new User(email);
        existingUser.setId(1);
        when(userRepository.findByEmail(email)).thenReturn(existingUser);

        boolean result = userService.isEmailInvalid(null, email);
        assertThat(result).isTrue();
        verify(userRepository, Mockito.times(1)).findByEmail(email);
    }

    @Test
    void isEmailInvalid_shouldReturnFalse_whenUserEmailIsNotUniqueButIsUsersEmail() {
        Integer id = 1;
        String email = "an@email.com";
        User existingUser = new User(email);
        existingUser.setId(1);
        when(userRepository.findByEmail(email)).thenReturn(existingUser);

        boolean result = userService.isEmailInvalid(id, email);
        assertThat(result).isFalse();
        verify(userRepository, Mockito.times(1)).findByEmail(email);
    }

    @Test
    void createUser_shouldReturnNewUserWithId_whenUserEmailDoesntExist() {
        Integer id = 1;
        String uniqueEmail = "an@email.com";

        User newUserEntity = new User(uniqueEmail);
        User returnedUserEntity = new User(uniqueEmail);
        returnedUserEntity.setId(id);

        when(userRepository.save(newUserEntity)).thenReturn(returnedUserEntity);

        UserDTO savedUser = userService.createUser(new UserDTO(null, uniqueEmail));

        verify(userRepository, Mockito.times(1)).save(newUserEntity);
        assertThat(savedUser).isEqualTo(new UserDTO(id, uniqueEmail));
    }

    @Test
    void createUser_shouldThrowUserExistsException_whenUserEmailExists() {
        String invalidEmail = "an@email.com";
        UserDTO newUser = new UserDTO(null, invalidEmail);
        User returnedUserEntity = new User(invalidEmail);
        returnedUserEntity.setId(1);

        when(userRepository.findByEmail(invalidEmail)).thenReturn(returnedUserEntity);

        Throwable exception = assertThrows(UserExistsException.class, () -> userService.createUser(newUser));
        assertThat(exception.getMessage()).isEqualTo("The user with email: " + invalidEmail + " already exists.");
    }

    @Test
    void createUser_ShouldThrowNullPointerException_whenUserEmailIsNull() {
        UserDTO nullUser = new UserDTO(null, "");

        Throwable exception = assertThrows(ClientNullRequestException.class, () -> userService.createUser(nullUser));
        verify(userRepository, Mockito.times(0)).findByEmail(null);
        assertThat(exception.getMessage()).isEqualTo("The email field cannot be empty!");
    }

    @Test
    void getUser_shouldReturnUser_whenUserEmailExists() {
        String validEmail = "an@email.com";
        Integer id = 1;
        UserDTO newUser = new UserDTO(null, validEmail);
        User returnedUserEntity = new User(validEmail);
        returnedUserEntity.setId(id);

        when(userRepository.findByEmail(validEmail)).thenReturn(returnedUserEntity);

        UserDTO returnedUser = userService.getUser(validEmail);

        verify(userRepository, Mockito.times(1)).findByEmail(validEmail);
        assertThat(returnedUser).isEqualTo(new UserDTO(id, validEmail));
    }

    @Test
    void getUser_shouldThrowUserNotFoundException_whenUserEmailNotExists() {
        String invalidEmail = "an@email.com";
        UserDTO newUser = new UserDTO(null, invalidEmail);

        when(userRepository.findByEmail(invalidEmail)).thenReturn(null);

        Throwable exception = assertThrows(UserNotFoundException.class, () -> userService.getUser(invalidEmail));
        verify(userRepository, Mockito.times(1)).findByEmail(invalidEmail);
        assertThat(exception.getMessage()).isEqualTo("The user with email: " + invalidEmail + " does not exist.");
    }

    @Test
    void getUser_ShouldThrowNullPointerException_whenUserEmailIsNull() {
        Throwable exception = assertThrows(ClientNullRequestException.class, () -> userService.getUser(null));
        verify(userRepository, Mockito.times(0)).findByEmail(null);
        assertThat(exception.getMessage()).isEqualTo("The email field cannot be empty!");
    }

    @Test
    void getUser_ShouldThrowNullPointerException_whenUserEmailIsEmpty() {
        Throwable exception = assertThrows(ClientNullRequestException.class, () -> userService.getUser(""));
        verify(userRepository, Mockito.times(0)).findByEmail("");
        assertThat(exception.getMessage()).isEqualTo("The email field cannot be empty!");
    }
}
