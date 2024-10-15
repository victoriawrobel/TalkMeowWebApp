package uni.projects.talkmeow.services;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uni.projects.talkmeow.components.Banned;
import uni.projects.talkmeow.components.user.User;
import uni.projects.talkmeow.components.user.UserStatus;
import uni.projects.talkmeow.repositories.BannedRepository;
import uni.projects.talkmeow.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BannedService {

    @Autowired
    private BannedRepository bannedRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    public void banUser(Long userId, String reason, LocalDateTime endTime, HttpSession session) {
        Banned banned = new Banned();
        banned.setBannedUser(userRepository.findById(userId).orElse(null));
        banned.setReason(reason);
        banned.setBanTimeEnd(endTime);
        banned.setBanTime(LocalDateTime.now());
        banned.setBannedBy((User) session.getAttribute("user"));
        bannedRepository.save(banned);
        customUserDetailsService.banUser(userId);
    }

    public void unbanUser(Long userId) {
        customUserDetailsService.unbanUser(userId);
    }


    public List<User> getBannedUsers() {
        List<Banned> bans = bannedRepository.findAll();
        List<User> bannedUsers = bans.stream().map(Banned::getBannedUser).toList();
        return bannedUsers;
    }

    public List<Banned> getBansByUser(User user) {
        return bannedRepository.findAllByBannedUser(user);
    }

    public Banned getLastUserBan(User user) {
        if (getBansByUser(user).isEmpty()) {
            return null;
        }
        List<Banned> bans = getBansByUser(user).stream()
                .sorted((b1, b2) -> b2.getBanTime().compareTo(b1.getBanTime()))
                .toList();
        return bans.getFirst();
    }
}
