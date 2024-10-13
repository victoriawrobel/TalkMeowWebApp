package uni.projects.talkmeow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uni.projects.talkmeow.Utility.*;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 12.10.2024
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Default user login test, has to be changed")
    void adminPanelIsAccessible() throws Exception {
        MockHttpSession session = login(username, password, mockMvc);
        this.mockMvc.perform(get("/home").session(session))
                .andExpect(status().is3xxRedirection());
    }

}
