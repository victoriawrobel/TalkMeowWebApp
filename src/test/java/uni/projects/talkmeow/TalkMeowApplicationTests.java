package uni.projects.talkmeow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TalkMeowApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Application context loads successfully")
    void contextLoads() {
        // No action needed, just checks if the context loads.
    }

    @Test
    @DisplayName("Home is accessible, no user logged in")
    void homeControllerIsCreated() throws Exception {
        this.mockMvc.perform(get("/"))
                // Check that the status is OK (200)
                .andExpect(status().isOk())

                // Verify that the title is "TalkMeow - Home"
                .andExpect(xpath("//title").string("TalkMeow - Home"))

                // Check that there are links to Register and Login
                .andExpect(xpath("//a[@href='/register/form']").exists())
                .andExpect(xpath("//a[@href='/login/form']").exists())

                // Verify that the logo is present in the navbar
                .andExpect(xpath("//img[@alt='App Logo']").exists())

                // Verify that the main container contains the logo and text
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
        MockHttpSession session = login("wiki", "wiki");

        this.mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome, ")))
                .andExpect(content().string(containsString("wiki")))
                .andExpect(xpath("//*[@id='current-user-username']").exists())
                .andExpect(xpath("//a[@href='/profile' and contains(text(),'User Profile')]").exists())
                .andExpect(xpath("//a[@href='/logout' and contains(text(),'Logout')]").exists())
                .andExpect(xpath("//*[@id='search-bar']").exists());
    }

    @Test
    @DisplayName("Login as 'wiki wiki', log out and verify that the user is logged out")
    void loginAndLogout() throws Exception {
        MockHttpSession session = login("wiki", "wiki");

        this.mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome, ")))
                .andExpect(content().string(containsString("wiki")))
                .andExpect(xpath("//*[@id='current-user-username']").exists())
                .andExpect(xpath("//a[@href='/logout' and contains(text(),'Logout')]").exists());

        this.mockMvc.perform(post("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login/form?logout"));

        // Access the home page after logout
        this.mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Talk Meow")))
                .andExpect(xpath("//a[@href='/login/form']").exists())
                .andExpect(content().string(containsString("Register")));
    }

    @Test
    @DisplayName("Search for a user after logging in")
    void searchUserAfterLogin() throws Exception {
        // Step 1: Log in as 'wiki'
        MockHttpSession session = login("wiki", "wiki");

        // Step 2: Perform a search for 'admin'
        this.mockMvc.perform(get("/user/search") // Assuming your search endpoint is "/search"
                        .param("username", "admin") // Adjust according to your search implementation
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("admin"))) // Check if the username appears in the results
                .andExpect(xpath("//div[contains(@class, 'user-search-results')]").exists()) // Verify search results container exists
                .andExpect(xpath("//a[@href='/api/messages/conversation?id=6']//span[contains(@class, 'username') and text()='admin']").exists()); // Check if 'admin' is in the results
    }

    @Test
    @DisplayName("Clicking on the logo redirects to the home page")
    void clickingLogoRedirectsToHome() throws Exception {
        // Access the home page
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk()) // Ensure the home page is accessible
                .andExpect(xpath("//title").string("TalkMeow - Home")) // Optional: Verify the title

                // Check if the logo is present and clickable
                .andExpect(xpath("//a[@href='/']") // Check if the logo link points to the correct URL
                        .exists())
                .andExpect(xpath("//a[@href='/']//img[@alt='App Logo']") // Verify that the logo image exists
                        .exists());

        // Simulate clicking on the logo
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    // Helper method for login
    private MockHttpSession login(String username, String password) throws Exception {
        return (MockHttpSession) this.mockMvc.perform(post("/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andReturn()
                .getRequest()
                .getSession();
    }
}
