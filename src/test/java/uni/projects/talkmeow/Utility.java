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

    public static String invalidUsername = randomString(16);
    public static String invalidPassword = randomString(16);

    public static String adminUsername = "admin";
    public static String adminPassword = "admin";

    public static String managerUsername = "manager";
    public static String managerPassword = "manager";

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

}
