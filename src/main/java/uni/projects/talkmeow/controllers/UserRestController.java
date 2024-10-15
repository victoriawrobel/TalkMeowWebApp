package uni.projects.talkmeow.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uni.projects.talkmeow.components.user.User;

@RestController
public class UserRestController {

    @GetMapping("/user/currentUser")
    public String getCurrentUserUsername(HttpSession session) {
        User user = (User)session.getAttribute("user");
        return user.getUsername();
    }

    @GetMapping("/user/currentUser/id")
    public Long getCurrentUserId(HttpSession session) {
        User user = (User)session.getAttribute("user");
        return user.getId();
    }

    @GetMapping("/user/currentUser/avatar")
    public Long getCurrentUserAvatar(HttpSession session) {
        User user = (User)session.getAttribute("user");
        return user.getAvatar().getId();
    }



}
