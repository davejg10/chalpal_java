package chalpal.co.uk.chalpal.authentication;

import chalpal.co.uk.chalpal.exceptions.ClientNullRequestException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public UserDTO createUser(UserDTO newUser) throws UserExistsException, ClientNullRequestException {
        if (newUser.email() == null || newUser.email().isEmpty()) throw new ClientNullRequestException("The email field cannot be empty!");
        if (isEmailInvalid(newUser.id(), newUser.email())) throw new UserExistsException(newUser.email());

        User user = userRepository.save(new User(newUser.email()));
        return new UserDTO(user.getId(), user.getEmail());
    }

    public UserDTO getUser(String email) throws UserNotFoundException, ClientNullRequestException {
        if (email == null || email.isEmpty()) throw new ClientNullRequestException("The email field cannot be empty!");
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException(email);

        return new UserDTO(user.getId(), user.getEmail());
    }

    public boolean isEmailInvalid(Integer id, String email) {
        boolean isEmailInvalid = false;

        User userByEmail = userRepository.findByEmail(email);
        boolean isCreatingNew = (id == null || id == 0);

        if (isCreatingNew) {
            if (userByEmail != null) isEmailInvalid = true;
        } else {
            if (userByEmail.getId() != id) {
                isEmailInvalid = true;
            }
        }

        return isEmailInvalid;
    }
}
