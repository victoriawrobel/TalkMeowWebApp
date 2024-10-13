package uni.projects.talkmeow;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 12.10.2024
 */
public class Utility {

    public static String username = "wiki";
    public static String password = "wiki";
    public static String email = "wiki@wiki.wiki";

    public static String invalidUsername = randomStringUsername(16);
    public static String invalidPassword = randomString(16);

    public static String adminUsername = "admin";
    public static String adminPassword = "admin";

    public static String managerUsername = "manager";
    public static String managerPassword = "manager";

    public static String newUsername = randomStringUsername(16);
    public static String newPassword = "ThisIsAValidPassword123@!Please";
    public static String newEmail = randomStringEmail(16);
    public static String newSecurityQuestion = "What is your pet's name?";
    public static String newSecurityAnswer = "Fluffy";
    public static String newAvatarId = "4";

    public static String invalidUsernameReg = randomStringUsername(4);
    public static String invalidEmailFormatReg = "invalidEmail";
    public static String shortPassword = "Pa1@";
    public static String noUppercasePassword = "password@123";
    public static String noSpecialCharPassword = "Password123";
    public static String noNumberPassword = "Password@word";
    public static String noLowercasePassword = "PASSWORD@123";
    public static String invalidAvatarId = "9999999";

    // Helper method for login
    public static MockHttpSession login(String username, String password, MockMvc mockMvc) throws Exception {
        return (MockHttpSession) mockMvc.perform(post("/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andReturn()
                .getRequest()
                .getSession();
    }

    public static String randomString(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String randomStringUsername(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i >= 48 && i <= 57) || // digits 0-9
                        (i >= 65 && i <= 90) || // uppercase A-Z
                        (i >= 97 && i <= 122) || // lowercase a-z
                        i == 95) // underscore '_'
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String randomStringEmail(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i >= 48 && i <= 57) || // digits 0-9
                        (i >= 65 && i <= 90) || // uppercase A-Z
                        (i >= 97 && i <= 122)) // underscore '_'
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString() + "@example.com";
    }

}
