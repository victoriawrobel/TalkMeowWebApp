package uni.projects.talkmeow.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uni.projects.talkmeow.components.Message;
import uni.projects.talkmeow.components.MessageStatus;
import uni.projects.talkmeow.components.User;
import uni.projects.talkmeow.repositories.MessageRepository;
import uni.projects.talkmeow.services.CustomUserDetailsService;
import uni.projects.talkmeow.services.MessageService;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 03.10.2024
 */

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

}
