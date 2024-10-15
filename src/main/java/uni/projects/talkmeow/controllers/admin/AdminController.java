package uni.projects.talkmeow.controllers.admin;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uni.projects.talkmeow.components.Banned;
import uni.projects.talkmeow.components.InappropriateMessage;
import uni.projects.talkmeow.components.MessageApproval;
import uni.projects.talkmeow.components.user.User;
import uni.projects.talkmeow.services.BannedService;
import uni.projects.talkmeow.services.CustomUserDetailsService;
import uni.projects.talkmeow.services.InappropriateMessageService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BannedService bannedService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private InappropriateMessageService inappropriateMessageService;

    @GetMapping("/home")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/banned")
    public String banned(Model model) {
        model.addAttribute("bannedUsers", new HashSet<>(bannedService.getBannedUsers()).stream().toList());
        return "admin/banned";
    }

    @GetMapping("/user/{userId}/bans")
    public String userBans(@PathVariable Long userId, Model model) {
        User user = customUserDetailsService.getUsernameByID(userId);
        model.addAttribute("user", user);
        List<Banned> bans = bannedService.getBansByUser(user);

        List<Banned> sortedBans = bans.stream()
                .sorted(Comparator.comparing(Banned::getBanTime).reversed())
                .toList();
        model.addAttribute("bans", sortedBans);
        return "admin/userBans";
    }

    @GetMapping("/suspicious_messages")
    public String messageVerify(Model model) {
        List<InappropriateMessage> messagesToVerify = inappropriateMessageService.getAllToVerify();
        model.addAttribute("messages", messagesToVerify);
        return "admin/suspicious_messages";
    }

    @PostMapping("/suspicious_messages/approve/{id}")
    public String approveMessage(@PathVariable Long id) {
        inappropriateMessageService.updateMessageApproval(id, MessageApproval.APPROVED);
        return "redirect:/admin/suspicious_messages";
    }

    @PostMapping("/suspicious_messages/inappropriate/{id}")
    public String markMessageInappropriate(@PathVariable Long id) {
        inappropriateMessageService.updateMessageApproval(id, MessageApproval.INAPPROPRIATE);
        return "redirect:/admin/suspicious_messages";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = customUserDetailsService.getAllUsers().stream()
                .sorted(Comparator.comparing(user -> inappropriateMessageService.countMessagesBySender((User) user)).reversed())
                .toList();
        model.addAttribute("users", users);
        return "admin/user_list";
    }

    @GetMapping("/user/{userId}/messages")
    public String viewUserMessages(@PathVariable Long userId, Model model) {
        User user = customUserDetailsService.getUsernameByID(userId);
        Banned lastBan = bannedService.getLastUserBan(user);
        List<InappropriateMessage> messages;
        if (lastBan != null) {
             messages = inappropriateMessageService.getMessagesAfterLastBan(user, lastBan.getBanTime());
        } else {
            messages = inappropriateMessageService.getMessagesBySender(user);
        }

        model.addAttribute("user", user);
        model.addAttribute("messages", messages);
        return "admin/user_inappropriate_messages";
    }

    @PostMapping("/user/{userId}/ban")
    public String banUser(@PathVariable Long userId, @RequestParam String reason, @RequestParam LocalDateTime banEndTime, HttpSession session) {
        bannedService.banUser(userId, reason, banEndTime, session);
        return "redirect:/admin/users";
    }

    @PostMapping("/user/{userId}/unban")
    public String unbanUser(@PathVariable Long userId) {
        bannedService.unbanUser(userId);
        return "redirect:/admin/users";
    }

}
