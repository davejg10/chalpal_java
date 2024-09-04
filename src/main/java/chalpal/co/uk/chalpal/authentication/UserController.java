package chalpal.co.uk.chalpal.authentication;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
public class UserController {
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping(path="/users")
    public ResponseEntity<UserDTO> registerUser (@RequestBody UserDTO user) {
        UserDTO newUser = userService.createUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newUser);
    }

    @GetMapping(path={"/users/{email}"})
    UserDTO loginUser(@PathVariable(required = false) String email) {
        return userService.getUser(email);
    }
}
