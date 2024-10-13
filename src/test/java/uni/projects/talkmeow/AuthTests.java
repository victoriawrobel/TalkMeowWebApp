package uni.projects.talkmeow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uni.projects.talkmeow.Utility.login;
import static uni.projects.talkmeow.Utility.randomString;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 12.10.2024
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthTests {

    @Autowired
    private MockMvc mockMvc;

    private final String username = "admin";
    private final String password = "admin";

    private final String invalidUsername = randomString(16);
    private final String invalidPassword = randomString(16);

    //Login
    @Test
    @DisplayName("Login form is accessible")
    void loginFormIsAccessible() throws Exception {
           this.mockMvc.perform(get("/login/form"))
                   .andExpect(status().isOk())
                   .andExpect(content().string(containsString("Login")));
    }

    @Test
    @DisplayName("Login with valid credentials")
    void loginWithValidCredentials() throws Exception {
        MockHttpSession session = login(username, password, mockMvc);
        this.mockMvc.perform(get("/admin/home").session(session))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Login with invalid credentials")
    void loginWithInvalidCredentials() throws Exception {
        this.mockMvc.perform(post("/login")
                        .param("username", invalidUsername)
                        .param("password", invalidPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login/form?error=true"));
    }

    @Test
    @DisplayName("Login with missing username")
    void loginWithMissingUsername() throws Exception {
        this.mockMvc.perform(post("/login")
                        .param("password", password))  // Missing username
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login/form?error=missing_username"));
    }

    @Test
    @DisplayName("Login with missing password")
    void loginWithMissingPassword() throws Exception {
        this.mockMvc.perform(post("/login")
                        .param("username", username))  // Missing password
                .andExpect(status().is3xxRedirection())  // Expecting 400 Bad Request
                .andExpect(redirectedUrl("/login/form?error=missing_password"));  // Expecting proper redirect with error message
    }


    @Test
    @DisplayName("Logout successfully")
    void logoutSuccessfully() throws Exception {
        MockHttpSession session = login(username, password, mockMvc);
        this.mockMvc.perform(post("/logout").session(session))
                .andExpect(status().is3xxRedirection())  // Typically, a redirect after logout
                .andExpect(redirectedUrl("/login/form"));
    }

    //Register

    @Test
    @DisplayName("Register form is accessible")
    void registerFormIsAccessible() throws Exception {
           this.mockMvc.perform(get("/register/form"))
                   .andExpect(status().isOk())
                   .andExpect(content().string(containsString("Register")));
    }
}
