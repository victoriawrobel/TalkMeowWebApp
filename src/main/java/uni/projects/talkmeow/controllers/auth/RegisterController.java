package uni.projects.talkmeow.controllers.auth;

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

    @Autowired
    private AvatarService avatarService;

    @GetMapping("/register/form")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());

        model.addAttribute("avatars", avatarService.getFilteredAvatars(null,null,null,null,null)); // Retrieve all avatars
        model.addAttribute("color", Color.values());
        model.addAttribute("eyeColors", Color.values());  // Add available eye colors
        model.addAttribute("pattern", Pattern.values());
        model.addAttribute("breed", Breed.values());
        model.addAttribute("age", Age.values());

        return "auth/register";
    }

    @PostMapping("/register")
    @ResponseBody
    public RedirectView registerUser(@ModelAttribute User user, @RequestParam("selectedAvatarId") Long selectedAvatarId) {

        // Check if required fields are provided
        if (user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
            return new RedirectView("/register/form?error=form");
        }

        // Check if the username already exists
        if (customUserDetailsService.existsByUsername(user.getUsername())) {
            return new RedirectView("/register/form?error=username");
        }

        // Fetch the selected avatar
        Avatar avatar = avatarService.getAvatarById(selectedAvatarId);

        // If avatar not found, return an error
        if (avatar == null) {
            return new RedirectView("/register/form?error=avatar");
        }

        // Assign the avatar to the user
        user.setAvatar(avatar);

        // Encode and save the user
        customUserDetailsService.registerUser(user, passwordEncoder);

        return new RedirectView("/login/form");
    }

    @GetMapping("/register/avatars")
    public String filterAvatars(@RequestParam(value = "furColor", required = false) Color furColor,
                                @RequestParam(value = "eyeColor", required = false) Color eyeColor,
                                @RequestParam(value = "pattern", required = false) Pattern pattern,
                                @RequestParam(value = "breed", required = false) Breed breed,
                                @RequestParam(value = "age", required = false) Age age,
                                Model model) {
        // Fetch filtered avatars based on the provided filters
        List<Avatar> filteredAvatars = avatarService.getFilteredAvatars(age, breed, furColor, eyeColor, pattern);
        model.addAttribute("avatars", filteredAvatars);
        return "fragments/avatarList :: avatarList";  // Return a fragment with the updated avatar list
    }

}
