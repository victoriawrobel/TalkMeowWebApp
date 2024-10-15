package uni.projects.talkmeow.controllers.auth;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import uni.projects.talkmeow.components.avatar.*;
import uni.projects.talkmeow.components.user.User;
import uni.projects.talkmeow.services.AvatarService;
import uni.projects.talkmeow.services.CustomUserDetailsService;

import java.util.List;

import static uni.projects.talkmeow.utility.Defaults.*;

@Controller
public class RegisterController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AvatarService avatarService;

    @GetMapping("/register/form")
    public String registerForm(Model model, HttpSession session) {
        model.addAttribute("user", (User) session.getAttribute("user"));
        model.addAttribute("newUser", new User());

        model.addAttribute("avatars", avatarService.getFilteredAvatars(null,null,null,null,null)); // Retrieve all avatars
        model.addAttribute("color", Color.values());
        model.addAttribute("eyeColors", Color.values());
        model.addAttribute("pattern", Pattern.values());
        model.addAttribute("breed", Breed.values());
        model.addAttribute("age", Age.values());

        return "auth/register";
    }

    @PostMapping("/register")
    @ResponseBody
    public RedirectView registerUser(@ModelAttribute User user, @RequestParam("selectedAvatarId") Long selectedAvatarId) {

        if (user.getUsername().length() < 6 || !user.getUsername().matches(usernameRegex)) {
            return new RedirectView("/register/form?error=username_format");
        }

        if (!user.getEmail().matches(emailRegex)) {
            return new RedirectView("/register/form?error=email_format");
        }

        if (!user.getPassword().matches(passwordRegex)) {
            return new RedirectView("/register/form?error=password_strength");
        }

        if (customUserDetailsService.existsByUsername(user.getUsername())) {
            return new RedirectView("/register/form?error=username_exists");
        }

        if (customUserDetailsService.existsByEmail(user.getEmail())) {
            return new RedirectView("/register/form?error=email_exists");
        }

        Avatar avatar = avatarService.getAvatarById(selectedAvatarId);

        if (avatar == null) {
            return new RedirectView("/register/form?error=avatar_not_found");
        }

        user.setAvatar(avatar);

        customUserDetailsService.registerUser(user, passwordEncoder);

        return new RedirectView("/login/form");
    }


}
