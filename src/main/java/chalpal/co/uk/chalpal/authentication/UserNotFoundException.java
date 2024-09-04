package chalpal.co.uk.chalpal.authentication;

public class UserNotFoundException extends RuntimeException {

    UserNotFoundException(String email) { super("The user with email: " + email + " does not exist."); }
}
