package uni.projects.talkmeow.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uni.projects.talkmeow.components.user.User;
import uni.projects.talkmeow.repositories.UserRepository;
import uni.projects.talkmeow.services.CustomUserDetailsService;

/**
 * @author Wiktoria Wróbel
 * @version 1.0
 * @since 10/5/2024
 */

@Controller
public class UserProfileController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public String getProfileAttributes(Model model, HttpSession session) {
        User user = (User)session.getAttribute("user");
        model.addAttribute("currentUser", user);
        return "profile";
    }

    @PostMapping("/profile/change")
    public String changeUserAttributes(@ModelAttribute User user,
                                       @RequestParam(required = false) String newPassword,
                                       @RequestParam (required = false) String oldPassword,
                                       HttpSession session) {
        User currentUser = new User((User)session.getAttribute("user"));
        if((oldPassword.isEmpty() && !newPassword.isEmpty()) || (!oldPassword.isEmpty() && newPassword.isEmpty()))
            return "redirect:/profile?error=invalid_form";
        //Change username
        if(user.getUsername() != null)
            if(customUserDetailsService.existsByUsername(user.getUsername()) &&
                    !(currentUser.getUsername().equals(user.getUsername())))
                return "redirect:/profile?error=username_taken";
            else
                currentUser.setUsername(user.getUsername());

        //Change password
        if (!newPassword.isEmpty() && !oldPassword.isEmpty())
            if (!customUserDetailsService.authenticateOldPassword(oldPassword, currentUser, passwordEncoder))
                return "redirect:/profile?error=wrong_password";
            else
                customUserDetailsService.changePassword(currentUser, newPassword, passwordEncoder);


        userRepository.save(currentUser);
        session.setAttribute("user", currentUser);
        return "redirect:/profile";
    }

}