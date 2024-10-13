package uni.projects.talkmeow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Suite
@SelectClasses({
        LoginTests.class,
        RegisterTests.class,
        AdminTests.class,
        HomeTests.class,
        ManagerTests.class,
        UserTests.class
})
@IncludeClassNamePatterns({"^.*Tests$"})
class TalkMeowApplicationTests {

    @Test
    @DisplayName("Application context loads successfully")
    void contextLoads() {

    }

}
