package uni.projects.talkmeow;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public static String newAvatarId = "1";

    public static String invalidUsernameReg = randomStringUsername(4);
    public static String invalidEmailFormatReg = "invalidEmail";
    public static String shortPassword = "Pa1@";
    public static String noUppercasePassword = "password@123";
    public static String noSpecialCharPassword = "Password123";
    public static String noNumberPassword = "Password@word";
    public static String noLowercasePassword = "PASSWORD@123";
    public static String invalidAvatarId = "9999999";

    public static final String placeholderImagePath = "src/main/resources/static/images/test_placeholder.png";

    public static final String searchUsername = "admin";



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
        int leftLimit = 48;
        int rightLimit = 122;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String randomStringUsername(int length) {
        int leftLimit = 48;
        int rightLimit = 122;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i >= 48 && i <= 57) ||
                        (i >= 65 && i <= 90) ||
                        (i >= 97 && i <= 122) ||
                        i == 95)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String randomStringEmail(int length) {
        int leftLimit = 48;
        int rightLimit = 122;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i >= 48 && i <= 57) ||
                        (i >= 65 && i <= 90) ||
                        (i >= 97 && i <= 122))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString() + "@example.com";
    }

}
