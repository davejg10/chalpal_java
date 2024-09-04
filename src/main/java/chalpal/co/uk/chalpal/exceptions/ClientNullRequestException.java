package chalpal.co.uk.chalpal.exceptions;

public class ClientNullRequestException extends RuntimeException {

    ClientNullRequestException() { super("Something was wrong with that request, please try again."); }
    public ClientNullRequestException(String details) { super(details);}


}
