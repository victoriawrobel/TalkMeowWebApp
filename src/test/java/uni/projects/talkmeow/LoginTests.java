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
import static uni.projects.talkmeow.Utility.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class LoginTests {

    @Autowired
    private MockMvc mockMvc;

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
        MockHttpSession session = login(adminUsername, adminPassword, mockMvc);
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
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login/form?error=missing_username"));
    }

    @Test
    @DisplayName("Login with missing password")
    void loginWithMissingPassword() throws Exception {
        this.mockMvc.perform(post("/login")
                        .param("username", username))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login/form?error=missing_password"));
    }


    @Test
    @DisplayName("Logout successfully")
    void logoutSuccessfully() throws Exception {
        MockHttpSession session = login(username, password, mockMvc);
        this.mockMvc.perform(post("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login/form"));
    }

}
