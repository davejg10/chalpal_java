package chalpal.co.uk.chalpal.authentication;

import chalpal.co.uk.chalpal.exceptions.ClientNullRequestException;
import chalpal.co.uk.chalpal.logging.LoggingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static chalpal.co.uk.chalpal.authentication.MvcResultUtil.contentContains;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MvcResultUtil  {

    public static ResultMatcher contentContains(String expectedContent) {
        return mvcResult -> {
            String content = mvcResult.getResponse().getContentAsString();
            AssertionErrors.assertTrue("Response content", content.contains(expectedContent));
        };
    }
}

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private LoggingService loggingService;

    private final String email = "an@email.com";

    record UserRequest(String email){};

    @Test
    void registerUser_shouldReturn201WrappedUserDTO_whenUserEmailDoesntExist() throws Exception {
        UserRequest newUser = new UserRequest(email);
        UserDTO returnedUser = new UserDTO(1, email);
        when(userService.createUser(any(UserDTO.class))).thenReturn(returnedUser);

        MvcResult mvcResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(returnedUser)))
                .andReturn();
    }

    @Test
    void registerUser_shouldReturnConflictRequest_whenUserEmailExists() throws Exception {
        UserRequest newUser = new UserRequest(email);
        when(userService.createUser(any(UserDTO.class))).thenThrow(new UserExistsException(email));

        MvcResult mvcResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isConflict())
                .andExpect(content().string("The user with email: " + email + " already exists."))
                .andReturn();
    }

    @Test
    void registerUser_shouldReturnBadRequest_whenUserEmailIsNull() throws Exception {
        UserRequest newUser = new UserRequest(null);
        when(userService.createUser(any(UserDTO.class))).thenThrow(new ClientNullRequestException("The email field cannot be empty!"));

        MvcResult mvcResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The email field cannot be empty!"))
                .andReturn();
    }

    @Test
    void registerUser_shouldReturnBadRequest_whenUserEmailIsEmpty() throws Exception {
        UserRequest newUser = new UserRequest("");
        when(userService.createUser(any(UserDTO.class))).thenThrow(new ClientNullRequestException("The email field cannot be empty!"));

        MvcResult mvcResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The email field cannot be empty!"))
                .andReturn();
    }

    @Test
    void loginUser_shouldReturn200WrappedUserDTO_whenUserEmailExists() throws Exception {
        UserDTO returnedUser = new UserDTO(1, email);
        when(userService.getUser(any(String.class))).thenReturn(returnedUser);

        MvcResult mvcResult = mockMvc.perform(get(String.format("/users/%s", email)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(returnedUser)))
                .andReturn();
    }

    @Test
    void loginUser_shouldReturn404_whenUserEmailIsEmpty() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/users/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void loginUser_shouldReturn400_whenUserEmailIsBlank() throws Exception {
        when(userService.getUser(any(String.class))).thenThrow(new ClientNullRequestException("The email field cannot be empty!"));

        MvcResult mvcResult = mockMvc.perform(get(String.format("/users/%s", " ")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The email field cannot be empty!"))
                .andReturn();
    }

    @Test
    void loginUser_shouldReturn404_whenUserEmailDoesntExist() throws Exception {
        String invalidUser = "invalid@email";
        when(userService.getUser(any(String.class))).thenThrow(new UserNotFoundException(invalidUser));

        MvcResult mvcResult = mockMvc.perform(get(String.format("/users/%s", invalidUser)))
                .andExpect(status().isNotFound())
                .andExpect(contentContains("The user with email: " + invalidUser + " does not exist."))
                .andReturn();
    }
}
