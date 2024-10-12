package uni.projects.talkmeow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    @DisplayName("Login form is accessible")
    void loginFormIsAccessible() throws Exception {
           this.mockMvc.perform(get("/login/form"))
                   .andExpect(status().isOk())
                   .andExpect(content().string(containsString("Login")));
    }

    @Test
    @DisplayName("Register form is accessible")
    void registerFormIsAccessible() throws Exception {
           this.mockMvc.perform(get("/register/form"))
                   .andExpect(status().isOk())
                   .andExpect(content().string(containsString("Register")));
    }
}
