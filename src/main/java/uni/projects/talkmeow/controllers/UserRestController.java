package uni.projects.talkmeow.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uni.projects.talkmeow.components.user.User;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 07.10.2024
 */

@RestController
public class UserRestController {

    @GetMapping("/user/currentUser")
    public String getCurrentUser(HttpSession session) {
        User user = (User)session.getAttribute("user");
        return user.getUsername();
    }

}
