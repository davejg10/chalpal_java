package chalpal.co.uk.chalpal.authentication;

import chalpal.co.uk.chalpal.PostgresDataSourceInitializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // Ensures our tests use the PostgreSQL container rather than an inmemory H2 one.
@ContextConfiguration(initializers = PostgresDataSourceInitializer.class) // References our Postgres TestContainer
public class UserIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private int totalUsers;
    private static String existingEmail = "user@exists.com";
    private static User existingUser = new User(existingEmail);


    @BeforeEach
    public void setupEach() {
        log.info("===============================BEFORE EACH===============================");
        userRepository.save(existingUser);
        totalUsers = userRepository.findAll().size();
        log.info(String.format("Total users in DB = %d, Users are: %s", totalUsers, userRepository.findAll()));
    }

    record UserRequest(String email) {};

    @Test
    public void registerUser_shouldReturnJsonUser_whenUserEmailDoesntExist() throws Exception {
        String url = "http://localhost:" + port + "/users";

        String newEmail = "an@email.com";
        UserRequest newUser = new UserRequest(newEmail);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        HttpEntity<String> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(newUser), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains(objectMapper.writeValueAsString(new UserDTO(totalUsers + 1, newEmail)));
        assertThat(userRepository.findAll().size()).isEqualTo(totalUsers + 1);
    }

    @Test
    public void registerUser_shouldReturnConflictError_whenUserEmailExists() throws Exception {
        String url = "http://localhost:" + port + "/users";
        UserRequest newUser = new UserRequest(existingEmail);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        HttpEntity<String> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(newUser), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("The user with email: " + existingEmail + " already exists.");
        assertThat(userRepository.findAll().size()).isEqualTo(totalUsers);
    }

    @Test
    public void registerUser_shouldReturnBadRequest_whenUserEmailIsNull() throws Exception {
        String url = "http://localhost:" + port + "/users";
        UserRequest newUser = new UserRequest(null);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        HttpEntity<String> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(newUser), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("The email field cannot be empty!");
        assertThat(userRepository.findAll().size()).isEqualTo(totalUsers);
    }

    @Test
    public void registerUser_shouldReturnBadRequest_whenUserEmailIsEmpty() throws Exception {
        String url = "http://localhost:" + port + "/users";
        UserRequest newUser = new UserRequest("");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        HttpEntity<String> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(newUser), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("The email field cannot be empty!");
        assertThat(userRepository.findAll().size()).isEqualTo(totalUsers);
    }

    @Test
    public void loginUser_shouldReturnUser_whenUserEmailExists() throws Exception {
        Integer existingUserId = userRepository.findByEmail(existingEmail).getId();
        String url = String.format("http://localhost:%s/users/%s", port, existingEmail);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(objectMapper.writeValueAsString(new UserDTO(existingUserId, existingEmail)));
    }

    @Test
    public void loginUser_shouldThrowClientNullException_whenUserEmailIsBlank() {
        String url = String.format("http://localhost:%s/users/%s", port, " ");
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("The email field cannot be empty!");
    }

    @Test
    public void loginUser_shouldThrowUserNotFoundError_whenUserEmailDoesntExist() {
        String invalidEmail = "random@email.com";
        String url = String.format("http://localhost:%s/users/%s", port, invalidEmail);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("The user with email: " + invalidEmail + " does not exist.");
    }
}
