package uni.projects.talkmeow.controllers.auth;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import uni.projects.talkmeow.components.user.User;
import uni.projects.talkmeow.services.CustomUserDetailsService;

import static uni.projects.talkmeow.utility.Defaults.passwordRegex;

@Controller
public class PasswordResetController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/password-reset/form")
    public String passwordResetForm(Model model) {
        model.addAttribute("user", null);
        return "auth/password-reset/password-reset-email";
    }

    @PostMapping("/password-reset-email")
    public ModelAndView passwordReset(Model model, @RequestParam String email, HttpSession session) {
        model.addAttribute("user", null);
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
    public ModelAndView passwordResetQuestion(Model model, @RequestParam String answer, HttpSession session) {
        model.addAttribute("user", null);
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
    public ModelAndView passwordResetChange(Model model, @RequestParam String newPassword, HttpSession session) {
        model.addAttribute("user", null);
        User user = (User) session.getAttribute("password-reset-user");
        ModelAndView modelAndView = new ModelAndView();
        if (!newPassword.matches(passwordRegex)) {
            modelAndView.setViewName("auth/password-reset/password-reset-change");
            modelAndView.addObject("error", "Password doesn't meet the requirements");
            return modelAndView;
        }
        if (user != null) {
            customUserDetailsService.changePassword(user, newPassword, passwordEncoder);
        }
        modelAndView.setView(new RedirectView("/login/form"));
        return modelAndView;
    }
}