package chalpal.co.uk.chalpal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ClientNullRequestAdvice {

    @ResponseBody
    @ExceptionHandler(ClientNullRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String clientNullRequestHandler(ClientNullRequestException ex) {
        return ex.getMessage();
    }
}
