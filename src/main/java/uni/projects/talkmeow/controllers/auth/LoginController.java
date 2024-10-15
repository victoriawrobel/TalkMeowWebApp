package uni.projects.talkmeow.controllers.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uni.projects.talkmeow.components.user.User;
import uni.projects.talkmeow.components.user.UserStatus;
import uni.projects.talkmeow.repositories.UserRepository;
import uni.projects.talkmeow.services.BannedService;
import uni.projects.talkmeow.services.CustomUserDetailsService;
import uni.projects.talkmeow.services.GlobalAttributeService;

import java.time.LocalDateTime;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
public class LoginController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private GlobalAttributeService globalAttributeService;

    @Autowired
    private BannedService bannedService;

    private final AuthenticationManager authenticationManager;

    public LoginController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login/form")
    public String loginForm(Model model) {
        User user = (User) httpSession.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("loginForm", new LoginForm());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request, @ModelAttribute LoginForm loginForm) {
        if (loginForm.getPassword() == null || loginForm.getPassword().isEmpty()) {
            return "redirect:/login/form?error=missing_password";
        }
        if (loginForm.getUsername() == null || loginForm.getUsername().isEmpty()) {
            return "redirect:/login/form?error=missing_username";
        }
        if (customUserDetailsService.authenticateUser(loginForm, passwordEncoder)) {
            User user = userRepository.findByUsername(loginForm.getUsername()).orElse(null);
            if (user == null) {
                user = userRepository.findByEmail(loginForm.getUsername());
            }
            if (user.getUserStatus() != UserStatus.ACTIVE){
                if (user.getUserStatus() == UserStatus.TEMPORARILY_BANNED) {
                    if (bannedService.getLastUserBan(user).getBanTimeEnd().isBefore(LocalDateTime.now())) {
                        bannedService.unbanUser(user.getId());
                    } else
                        return "redirect:/login/form?error=temporarily_banned";
                } else
                    return "redirect:/login/form?error=permanently_banned";

            }

            httpSession.setAttribute("user", user);

            UsernamePasswordAuthenticationToken authReq
                    = new UsernamePasswordAuthenticationToken(user.getUsername(), loginForm.getPassword());
            Authentication auth = authenticationManager.authenticate(authReq);

            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(auth);
            HttpSession session = request.getSession(true);
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
            session.setAttribute("isLoggedIn", true);
            globalAttributeService.addAttribute("isLoggedIn", true);
            globalAttributeService.addAttribute("user", user);

            return "redirect:/";
        }
        return "redirect:/login/form?error=true";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();

        globalAttributeService.addAttribute("isLoggedIn", false);
        globalAttributeService.addAttribute("current-user-username", null);
        return "redirect:/home";
    }

    @Data
    public static class LoginForm {
        private String username;
        private String password;

    }

}
