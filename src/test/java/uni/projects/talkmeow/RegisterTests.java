package uni.projects.talkmeow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uni.projects.talkmeow.Utility.*;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 13.10.2024
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RegisterTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Register form is accessible")
    void registerFormIsAccessible() throws Exception {
        this.mockMvc.perform(get("/register/form"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Register")));
    }

    @Test
    @DisplayName("Register form contains necessary fields")
    void registerFormContainsFields() throws Exception {
        this.mockMvc.perform(get("/register/form"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Username")))
                .andExpect(content().string(containsString("Email")))
                .andExpect(content().string(containsString("Password")))
                .andExpect(content().string(containsString("Security Question")))
                .andExpect(content().string(containsString("Security Answer")));
    }



    @Test
    @DisplayName("Register with invalid username format")
    void registerWithInvalidUsernameFormat() throws Exception {
        this.mockMvc.perform(post("/register")
                        .param("username", invalidUsernameReg) // Invalid username (contains special characters)
                        .param("email", newEmail)
                        .param("password", newPassword)
                        .param("securityQuestion", newSecurityQuestion)
                        .param("securityAnswer", newSecurityAnswer)
                        .param("selectedAvatarId", newAvatarId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register/form?error=username_format"));
    }

    @Test
    @DisplayName("Register with already existing username")
    void registerWithExistingUsername() throws Exception {

        this.mockMvc.perform(post("/register")
                        .param("username", managerUsername) // Username already exists
                        .param("email", newEmail)
                        .param("password", newPassword)
                        .param("securityQuestion", newSecurityQuestion)
                        .param("securityAnswer", newSecurityAnswer)
                        .param("selectedAvatarId", newAvatarId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register/form?error=username_exists"));
    }

    @Test
    @DisplayName("Register with invalid email format")
    void registerWithInvalidEmailFormat() throws Exception {
        this.mockMvc.perform(post("/register")
                        .param("username", newUsername)
                        .param("email", invalidEmailFormatReg) // Invalid email format
                        .param("password", newPassword)
                        .param("securityQuestion", newSecurityQuestion)
                        .param("securityAnswer", newSecurityAnswer)
                        .param("selectedAvatarId", newAvatarId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register/form?error=email_format"));
        newUsername = randomStringUsername(16);
        newEmail = randomStringEmail(16);
    }

    @Test
    @DisplayName("Register with already existing email")
    void registerWithExistingEmail() throws Exception {

        this.mockMvc.perform(post("/register")
                        .param("username", newUsername)
                        .param("email", email) // Email already exists
                        .param("password", newPassword)
                        .param("securityQuestion", newSecurityQuestion)
                        .param("securityAnswer", newSecurityAnswer)
                        .param("selectedAvatarId", newAvatarId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register/form?error=email_exists"));
        newUsername = randomStringUsername(16);
        newEmail = randomStringEmail(16);
    }

    @Test
    @DisplayName("Register with password too short")
    void registerWithPasswordTooShort() throws Exception {
        this.mockMvc.perform(post("/register")
                        .param("username", newUsername)
                        .param("email", newEmail)
                        .param("password", shortPassword) // Short password
                        .param("securityQuestion", newSecurityQuestion)
                        .param("securityAnswer", newSecurityAnswer)
                        .param("selectedAvatarId", newAvatarId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register/form?error=password_strength"));
        newUsername = randomStringUsername(16);
        newEmail = randomStringEmail(16);
    }

    @Test
    @DisplayName("Register with no uppercase letters in password")
    void registerWithNoUppercasePassword() throws Exception {
        this.mockMvc.perform(post("/register")
                        .param("username", newUsername)
                        .param("email", newEmail)
                        .param("password", noUppercasePassword) // Missing uppercase letters
                        .param("securityQuestion", newSecurityQuestion)
                        .param("securityAnswer", newSecurityAnswer)
                        .param("selectedAvatarId", newAvatarId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register/form?error=password_strength"));
        newUsername = randomStringUsername(16);
        newEmail = randomStringEmail(16);
    }

    @Test
    @DisplayName("Register with no special characters in password")
    void registerWithNoSpecialCharPassword() throws Exception {
        this.mockMvc.perform(post("/register")
                        .param("username", newUsername)
                        .param("email", newEmail)
                        .param("password", noSpecialCharPassword) // Missing special characters
                        .param("securityQuestion", newSecurityQuestion)
                        .param("securityAnswer", newSecurityAnswer)
                        .param("selectedAvatarId", newAvatarId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register/form?error=password_strength"));
        newUsername = randomStringUsername(16);
        newEmail = randomStringEmail(16);
    }

    @Test
    @DisplayName("Register with no numbers in password")
    void registerWithNoNumberPassword() throws Exception {
        this.mockMvc.perform(post("/register")
                        .param("username", newUsername)
                        .param("email", newEmail)
                        .param("password", noNumberPassword) // Missing numbers
                        .param("securityQuestion", newSecurityQuestion)
                        .param("securityAnswer", newSecurityAnswer)
                        .param("selectedAvatarId", newAvatarId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register/form?error=password_strength"));
        newUsername = randomStringUsername(16);
        newEmail = randomStringEmail(16);
    }

    @Test
    @DisplayName("Register with no lowercase letters in password")
    void registerWithNoLowercasePassword() throws Exception {
        this.mockMvc.perform(post("/register")
                        .param("username", newUsername)
                        .param("email", newEmail)
                        .param("password", noLowercasePassword) // Missing lowercase letters
                        .param("securityQuestion", newSecurityQuestion)
                        .param("securityAnswer", newSecurityAnswer)
                        .param("selectedAvatarId", newAvatarId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register/form?error=password_strength"));
        newUsername = randomStringUsername(16);
        newEmail = randomStringEmail(16);
    }

    @Test
    @DisplayName("Register with invalid avatar selection")
    void registerWithInvalidAvatar() throws Exception {

        this.mockMvc.perform(post("/register")
                        .param("username", newUsername)
                        .param("email", newEmail)
                        .param("password", newPassword)
                        .param("securityQuestion", newSecurityQuestion)
                        .param("securityAnswer", newSecurityAnswer)
                        .param("selectedAvatarId", invalidAvatarId)) // Invalid avatar ID
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register/form?error=avatar_not_found"));
        newUsername = randomStringUsername(16);
        newEmail = randomStringEmail(16);
    }


    @Test
    @DisplayName("Register form submission with valid data and avatar selection and login")
    void registerFormSubmissionWithValidDataAndAvatar() throws Exception {
        this.mockMvc.perform(post("/register")
                        .param("username", newUsername)
                        .param("email", newEmail)
                        .param("password", newPassword)
                        .param("securityQuestion", newSecurityQuestion)
                        .param("securityAnswer", newSecurityAnswer)
                        .param("selectedAvatarId", newAvatarId)) // Simulate selecting avatar with ID 4
                .andExpect(status().is3xxRedirection()) // Assuming successful form submission redirects
                .andExpect(redirectedUrl("/login/form"));

        MockHttpSession session = login(newUsername, newPassword, mockMvc);

        this.mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome, ")))
                .andExpect(content().string(containsString(newUsername)))
                .andExpect(xpath("//*[@id='current-user-username']").exists())
                .andExpect(xpath("//a[@href='/profile' and contains(text(),'User Profile')]").exists())
                .andExpect(xpath("//a[@href='/logout' and contains(text(),'Logout')]").exists())
                .andExpect(xpath("//*[@id='search-bar']").exists());
        newUsername = randomStringUsername(16);
        newEmail = randomStringEmail(16);
    }
}
