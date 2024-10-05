package uni.projects.talkmeow.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uni.projects.talkmeow.components.user.User;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 02.10.2024
 */

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession httpSession, Model model) {
        User user = (User) httpSession.getAttribute("user");
        model.addAttribute("user", user);
        return "home";
    }

    @GetMapping("/home")
    public String homeRed() {
        return "redirect:/";
    }

}
