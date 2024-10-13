package uni.projects.talkmeow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uni.projects.talkmeow.Utility.*;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 12.10.2024
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AdminTests {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("Admin panel is accessible")
    void adminPanelIsAccessible() throws Exception {
        MockHttpSession session = login(adminUsername, adminPassword, mockMvc);
           this.mockMvc.perform(get("/admin/home").session(session))
                   .andExpect(status().isOk());
    }

}
