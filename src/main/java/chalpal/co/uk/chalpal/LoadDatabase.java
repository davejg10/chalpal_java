package chalpal.co.uk.chalpal;

import chalpal.co.uk.chalpal.authentication.User;
import chalpal.co.uk.chalpal.authentication.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
class LoadDatabase {


    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {

        return args -> {
            userRepository.save(new User("davidgoddard@email.com"));
            userRepository.save(new User("tpazz@gmail.com"));

            userRepository.findAll().forEach(user -> log.info("Preloaded " + user));

        };
    }
}