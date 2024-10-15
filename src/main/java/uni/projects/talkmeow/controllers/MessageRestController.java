package uni.projects.talkmeow.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uni.projects.talkmeow.components.message.Message;
import uni.projects.talkmeow.components.message.MessageStatus;
import uni.projects.talkmeow.components.user.StrippedUser;
import uni.projects.talkmeow.components.user.User;
import uni.projects.talkmeow.repositories.MessageRepository;
import uni.projects.talkmeow.services.CustomUserDetailsService;
import uni.projects.talkmeow.services.MessageService;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageRestController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private MessagingController messagingController;

    @PostMapping("/status")
    public String changeStatus(@RequestParam Long id) {
        Message message = messageRepository.findById(id).get();
        User currentUser = customUserDetailsService.getCurrentUser();
        if (message != null && message.getReceiver().equals(currentUser)) {
            messageService.changeStatus(message, MessageStatus.SEEN);
        }

        messagingController.sendStatusUpdate(MessageStatus.SEEN, message.getReceiver(), message.getSender());

        return "redirect:/api/messages/conversation/all";
    }

    @GetMapping("/get/message/{id}")
    public Message getMessage(@PathVariable Long id, HttpSession session) {
        User currentUser = customUserDetailsService.getCurrentUser();
        Message message = messageRepository.findById(id).get();
        User sender = message.getSender();
        User receiver = message.getReceiver();
        Message strippedMessage = new Message(message.getId(), StrippedUser.getStrippedUser(sender), StrippedUser.getStrippedUser(receiver), message.getMessageContent(), message.getTimestamp(), message.getStatus(), true);
        if (currentUser.getId().equals(sender.getId()) || currentUser.getId().equals(receiver.getId())) {
            return strippedMessage;
        }
        return null;
    }

}
