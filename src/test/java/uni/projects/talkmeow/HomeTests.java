package uni.projects.talkmeow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uni.projects.talkmeow.Utility.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class HomeTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Home is accessible, no user logged in")
    void homeControllerIsCreated() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())

                .andExpect(xpath("//title").string("TalkMeow - Home"))

                .andExpect(xpath("//a[@href='/register/form']").exists())
                .andExpect(xpath("//a[@href='/login/form']").exists())

                .andExpect(xpath("//img[@alt='App Logo']").exists())

                .andExpect(xpath("//img[@alt='Talk Meow Logo']").exists())
                .andExpect(xpath("//h1").string("Talk Meow"));
    }

    @Test
    @DisplayName("Home page is accessible and login button redirects to /login/form")
    void homePageLoginRedirection() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Talk Meow")))
                .andExpect(xpath("//a[@href='/login/form']").exists())
                .andDo(result -> {
                    this.mockMvc.perform(get("/login/form"))
                            .andExpect(status().isOk());
                });
    }

    @Test
    @DisplayName("Home page is accessible and register button redirects to /register/form")
    void homePageRegisterRedirection() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Talk Meow")))
                .andExpect(xpath("//a[@href='/register/form']").exists())
                .andDo(result -> {
                    this.mockMvc.perform(get("/register/form"))
                            .andExpect(status().isOk());
                });
    }

    @Test
    @DisplayName("Login as 'wiki wiki' and verify the home page content for logged-in user")
    void loginAndVerifyHomePage() throws Exception {
        MockHttpSession session = login(username, password, mockMvc);

        this.mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome, ")))
                .andExpect(content().string(containsString(username)))
                .andExpect(xpath("//*[@id='current-user-username']").exists())
                .andExpect(xpath("//a[@href='/profile' and contains(text(),'User Profile')]").exists())
                .andExpect(xpath("//a[@href='/logout' and contains(text(),'Logout')]").exists())
                .andExpect(xpath("//*[@id='search-bar']").exists());
    }

    @Test
    @DisplayName("Login as 'wiki wiki', log out and verify that the user is logged out")
    void loginAndLogout() throws Exception {
        MockHttpSession session = login(username, password, mockMvc);

        this.mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome, ")))
                .andExpect(content().string(containsString(username)))
                .andExpect(xpath("//*[@id='current-user-username']").exists())
                .andExpect(xpath("//a[@href='/logout' and contains(text(),'Logout')]").exists());

        this.mockMvc.perform(post("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login/form"));

        this.mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Talk Meow")))
                .andExpect(xpath("//a[@href='/login/form']").exists())
                .andExpect(content().string(containsString("Register")));
    }

    @Test
    @DisplayName("Search for a user after logging in")
    void searchUserAfterLogin() throws Exception {
        MockHttpSession session = login(username, password, mockMvc);

        this.mockMvc.perform(get("/user/search")
                        .param("username", searchUsername)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(searchUsername)))
                .andExpect(xpath("//div[contains(@class, 'user-search-results')]").exists())
                .andExpect(xpath("//span[contains(@class, 'username') and text()='admin']").exists());
    }

    @Test
    @DisplayName("Clicking on the logo redirects to the home page")
    void clickingLogoRedirectsToHome() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(xpath("//title").string("TalkMeow - Home"))
                .andExpect(xpath("//a[@href='/']")
                        .exists())
                .andExpect(xpath("//a[@href='/']//img[@alt='App Logo']")
                        .exists());

        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

}
