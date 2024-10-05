package uni.projects.talkmeow.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uni.projects.talkmeow.components.user.User;
import uni.projects.talkmeow.controllers.auth.LoginController;
import uni.projects.talkmeow.principals.UserPrincipal;
import uni.projects.talkmeow.repositories.UserRepository;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 02.10.2024
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            user = userRepository.findByEmail(username);
        }
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new UserPrincipal(user);
    }

    public boolean authenticateUser(LoginController.LoginForm loginForm, PasswordEncoder passwordEncoder) {
        User user = userRepository.findByUsername(loginForm.getUsername()).orElse(null);
        if (user == null) {
            user = userRepository.findByEmail(loginForm.getUsername());
        }

        if (user != null) {
            return passwordEncoder.matches(loginForm.getPassword(), user.getPassword());
        }
        return false;
    }

    public User registerUser(User user, PasswordEncoder passwordEncoder) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return null;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User loadUserPasswordReset(String login) {
        User user = userRepository.findByUsername(login).orElse(null);
        if (user == null) {
            user = userRepository.findByEmail(login);
        }
        return user;
    }

    public boolean checkPasswordResetAnswer(User user, String answer) {
        return user.getSecurityAnswer().equals(answer);
    }

    public void changePassword(User user, String password, PasswordEncoder passwordEncoder) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}
