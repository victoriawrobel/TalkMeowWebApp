package uni.projects.talkmeow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import uni.projects.talkmeow.services.BannedService;
import uni.projects.talkmeow.services.InappropriateMessageService;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static uni.projects.talkmeow.Utility.*;

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

    @Test
    @DisplayName("Admin panel is not accessible to non-admin users")
    void adminPanelIsNotAccessibleToNonAdminUsers() throws Exception {
        MockHttpSession session = login(username, password, mockMvc);
           this.mockMvc.perform(get("/admin/home").session(session))
                   .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin can access manager panel")
    public void testManagerPanelAccess() throws Exception {
        MockHttpSession session = login(adminUsername, adminPassword, mockMvc);
        this.mockMvc.perform(get("/manager/home").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Talk Meow - Home")));
    }

    @Test
    @DisplayName("Can retrieve list of suspicious messages and their senders")
    public void testUserInappropriateMessagesPage() throws Exception {
        MockHttpSession session = login(adminUsername, adminPassword, mockMvc);
        this.mockMvc.perform(get("/admin/user/1/messages").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user_inappropriate_messages"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("messages"));
    }

    @Test
    @DisplayName("Admin can approve suspicious messages to be verified")
    public void testApproveSuspiciousMessage() throws Exception {
        MockHttpSession session = login(adminUsername, adminPassword, mockMvc);
        this.mockMvc.perform(post("/admin/suspicious_messages/approve/1").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/suspicious_messages"));
    }

    @Test
    @DisplayName("Admin can mark messages as inappropriate")
    public void testMarkMessageInappropriate() throws Exception {
        MockHttpSession session = login(adminUsername, adminPassword, mockMvc);
        this.mockMvc.perform(post("/admin/suspicious_messages/inappropriate/1").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/suspicious_messages"));
    }

    @Test
    @DisplayName("Can retrieve list of all users from admin panel and their account details")
    public void testUserListPage() throws Exception {
        MockHttpSession session = login(adminUsername, adminPassword, mockMvc);
        this.mockMvc.perform(get("/admin/users").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user_list"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @DisplayName("Can retrieve list of banned users from admin panel")
    public void testBannedUsersPage() throws Exception {
        MockHttpSession session = login(adminUsername, adminPassword, mockMvc);
        this.mockMvc.perform(get("/admin/banned").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/banned"))
                .andExpect(model().attributeExists("bannedUsers"));
    }

    @Test
    @DisplayName("Admin can access user bans")
    public void testAccessUserBans() throws Exception {
        MockHttpSession session = login(adminUsername, adminPassword, mockMvc);
        this.mockMvc.perform(get("/admin/user/1/bans").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/userBans"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("bans"));
    }

    @Test
    @DisplayName("Admin panel can give user a ban strike")
    public void testBanUser() throws Exception {
        MockHttpSession session = login(adminUsername, adminPassword, mockMvc);
        this.mockMvc.perform(post("/admin/user/1/ban")
                        .param("reason", "Violation of rules")
                        .param("banEndTime", "2023-12-31T23:59:59")
                        .session(session))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Admin panel can remove user ban")
    public void testUnbanUser() throws Exception {
        MockHttpSession session = login(adminUsername, adminPassword, mockMvc);
        this.mockMvc.perform(post("/admin/user/1/unban").session(session))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Admin panel can check a user's messages")
    public void testCheckUserMessages() throws Exception {
        MockHttpSession session = login(adminUsername, adminPassword, mockMvc);
        this.mockMvc.perform(get("/admin/user/1/messages").session(session))
                .andExpect(status().isOk());
    }


}
