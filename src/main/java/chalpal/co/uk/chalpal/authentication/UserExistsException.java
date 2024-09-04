package chalpal.co.uk.chalpal.authentication;

public class UserExistsException extends RuntimeException {

    UserExistsException(String email) { super("The user with email: " + email + " already exists."); }
}
