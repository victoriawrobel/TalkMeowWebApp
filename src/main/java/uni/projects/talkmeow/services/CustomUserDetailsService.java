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
import uni.projects.talkmeow.components.user.UserStatus;
import uni.projects.talkmeow.controllers.auth.LoginController;
import uni.projects.talkmeow.principals.UserPrincipal;
import uni.projects.talkmeow.repositories.UserRepository;

import java.util.List;

import static uni.projects.talkmeow.utility.Defaults.MAX_BAN_STRIKES;

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

    public void banUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setUserStatus(UserStatus.TEMPORARILY_BANNED);
            user.setBanStrike(user.getBanStrike() + 1);
            if (user.getBanStrike() >= MAX_BAN_STRIKES) {
                user.setUserStatus(UserStatus.PERMANENTLY_BANNED);
            }
            userRepository.save(user);
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserStatus getUserStatus(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return user.getUserStatus();
        }
        return null;
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
        if (user.getId() != null && userRepository.existsById(user.getId())) {
            user.setId(null);
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            return null;
        }
        if (userRepository.existsByEmail(user.getEmail())) {
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

    public boolean authenticateOldPassword(String oldPassword, User user, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    public User getUsernameByID(Long ID) {
        return userRepository.findById(ID).orElse(null);
    }

    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setUserStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        }
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }
}
