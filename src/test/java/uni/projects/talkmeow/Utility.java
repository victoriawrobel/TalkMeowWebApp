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
