package chalpal.co.uk.chalpal.authentication;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {

    User findByEmail(String email);

    List<User> findAll();
}