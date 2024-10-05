package uni.projects.talkmeow.controllers.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import uni.projects.talkmeow.components.user.User;
import uni.projects.talkmeow.services.CustomUserDetailsService;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 02.10.2024
 */

@Controller
public class RegisterController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register/form")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());

        return "auth/register";
    }

    @PostMapping("/register")
    @ResponseBody
    public RedirectView registerUser(@ModelAttribute User user) {

        if (user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
            return new RedirectView("/register/form?error=form");
        } else if (customUserDetailsService.existsByUsername(user.getUsername())) {
            return new RedirectView("/register/form?error=username");
        }
        customUserDetailsService.registerUser(user, passwordEncoder);

        return new RedirectView("/login/form");
    }

}
