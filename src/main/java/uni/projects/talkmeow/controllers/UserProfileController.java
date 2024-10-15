package uni.projects.talkmeow.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uni.projects.talkmeow.components.avatar.*;
import uni.projects.talkmeow.components.user.User;
import uni.projects.talkmeow.repositories.AvatarRepository;
import uni.projects.talkmeow.repositories.UserRepository;
import uni.projects.talkmeow.services.CustomUserDetailsService;

import static uni.projects.talkmeow.utility.Defaults.passwordRegex;
import static uni.projects.talkmeow.utility.Defaults.usernameRegex;

@Controller
public class UserProfileController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AvatarRepository avatarRepository;

    @GetMapping("/profile")
    public String getProfileAttributes(Model model, HttpSession session) {
        User user = (User)session.getAttribute("user");
        model.addAttribute("currentUser", new User(user));
        model.addAttribute("age", Age.values());
        model.addAttribute("breed", Breed.values());
        model.addAttribute("color", Color.values());
        model.addAttribute("pattern", Pattern.values());
        return "profile";
    }

    @PostMapping("/profile/avatar-change")
    public String changeAvatar(@RequestParam(value = "selectedAvatarId", required = false, defaultValue = "-1") Long avatarId, HttpSession session) {
        User currentUser = new User((User)session.getAttribute("user"));
        if (avatarId == -1)
            return "redirect:/profile?error=invalid_avatar";

        Avatar avatar = avatarRepository.getById(avatarId);
        currentUser.setAvatar(avatar);
        userRepository.save(currentUser);
        session.setAttribute("user", currentUser);
        return "redirect:/profile";
    }

    @PostMapping("/profile/change")
    public String changeUserAttributes(@RequestParam(required = false) String newUsername,
                                       @RequestParam(required = false) String newPassword,
                                       @RequestParam (required = false) String oldPassword,
                                       HttpSession session) {
        User currentUser = new User((User)session.getAttribute("user"));
        if((oldPassword.isEmpty() && !newPassword.isEmpty()) || (!oldPassword.isEmpty() && newPassword.isEmpty()))
            return "redirect:/profile?error=invalid_form";
        if(newUsername != null)
            if(customUserDetailsService.existsByUsername(newUsername) &&
                    !(currentUser.getUsername().equals(newUsername)))
                return "redirect:/profile?error=username_taken";
            else if(newUsername.matches(usernameRegex) && newUsername.length() > 6)
                currentUser.setUsername(newUsername);
            else
                return "redirect:/profile?error=wrong_username_format";

        if (!newPassword.isEmpty() && !oldPassword.isEmpty())
            if (!customUserDetailsService.authenticateOldPassword(oldPassword, currentUser, passwordEncoder))
                return "redirect:/profile?error=wrong_password";
            else if(newPassword.matches(passwordRegex))
                customUserDetailsService.changePassword(currentUser, newPassword, passwordEncoder);
            else
                return "redirect:/profile?error=wrong_password_format";

        userRepository.save(currentUser);
        session.setAttribute("user", currentUser);
        return "redirect:/profile";
    }

    @GetMapping("/user/search")
    public String searchUsersStartingWith(@RequestParam String username, Model model) {
        model.addAttribute("users", userRepository.findAllByUsernameStartingWithIgnoreCase(username));
        return "fragments/user-search-results :: user-search-results";
    }

}