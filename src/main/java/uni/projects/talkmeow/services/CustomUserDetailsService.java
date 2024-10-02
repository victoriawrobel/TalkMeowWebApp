package uni.projects.talkmeow.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uni.projects.talkmeow.components.Role;
import uni.projects.talkmeow.components.User;
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
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new UserPrincipal(user);
    }

    public boolean authenticateUser(LoginController.LoginForm loginForm, PasswordEncoder passwordEncoder) {
        User user = userRepository.findByUsername(loginForm.getUsername());

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

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
