package uni.projects.talkmeow.controllers.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.RequestMapping;
import uni.projects.talkmeow.components.User;
import uni.projects.talkmeow.repositories.UserRepository;
import uni.projects.talkmeow.services.CustomUserDetailsService;
import uni.projects.talkmeow.services.GlobalAttributeService;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 02.10.2024
 */

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

    private final AuthenticationManager authenticationManager;

    public LoginController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login/form")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response, @ModelAttribute LoginForm loginForm) {
        if (customUserDetailsService.authenticateUser(loginForm, passwordEncoder)) {
            User user = userRepository.findByUsername(loginForm.getUsername());
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

            return "redirect:/";
        }
        return "redirect:/login/form?error=true";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        globalAttributeService.addAttribute("isLoggedIn", false);
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        return "redirect:/home";
    }

    @Data
    public static class LoginForm {
        private String username;
        private String password;

    }

}
