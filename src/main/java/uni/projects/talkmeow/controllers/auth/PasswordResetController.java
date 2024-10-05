package uni.projects.talkmeow.controllers.auth;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uni.projects.talkmeow.components.user.User;
import uni.projects.talkmeow.services.CustomUserDetailsService;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 03.10.2024
 */

@Controller
public class PasswordResetController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/password-reset/form")
    public String passwordResetForm(Model model) {
        return "auth/password-reset/password-reset-email";
    }

    @PostMapping("/password-reset-email")
    public ModelAndView passwordReset(@RequestParam String email, HttpSession session) {
        User user = customUserDetailsService.loadUserPasswordReset(email);
        ModelAndView modelAndView = new ModelAndView();
        if (user == null) {
            modelAndView.setViewName("auth/password-reset/password-reset-email");
            modelAndView.addObject("error", "Email not found");
            return modelAndView;
        }
        session.setAttribute("password-reset-user", user);
        modelAndView.setViewName("auth/password-reset/password-reset-question");
        modelAndView.addObject("question", user.getSecurityQuestion());
        return modelAndView;
    }

    @PostMapping("/password-reset-question")
    public ModelAndView passwordResetQuestion(@RequestParam String answer, HttpSession session) {
        User user = (User) session.getAttribute("password-reset-user");
        ModelAndView modelAndView = new ModelAndView();
        if (user != null && customUserDetailsService.checkPasswordResetAnswer(user, answer)) {
            modelAndView.setViewName("auth/password-reset/password-reset-change");
        } else {
            modelAndView.setViewName("auth/password-reset/password-reset-question");
            modelAndView.addObject("question", user != null ? user.getSecurityQuestion() : null);
            modelAndView.addObject("error", "Incorrect answer");
        }
        return modelAndView;
    }

    @PostMapping("/password-reset-change")
    public String passwordResetChange(@RequestParam String newPassword, HttpSession session) {
        User user = (User) session.getAttribute("password-reset-user");
        if (user != null) {
            customUserDetailsService.changePassword(user, newPassword, passwordEncoder);
        }
        return "redirect:/login/form";
    }
}