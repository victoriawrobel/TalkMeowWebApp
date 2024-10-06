package uni.projects.talkmeow.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uni.projects.talkmeow.components.message.Message;
import uni.projects.talkmeow.components.message.MessageStatus;
import uni.projects.talkmeow.components.user.StrippedUser;
import uni.projects.talkmeow.components.user.User;
import uni.projects.talkmeow.repositories.InappropriateMessageRepository;
import uni.projects.talkmeow.repositories.MessageRepository;
import uni.projects.talkmeow.services.CustomUserDetailsService;
import uni.projects.talkmeow.services.InappropriateMessageService;
import uni.projects.talkmeow.services.MessageService;
import uni.projects.talkmeow.utility.MessageSupervisor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 03.10.2024
 */
@Controller
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageRepository messageRepository;

    private final MessageSupervisor messageSupervisor = new MessageSupervisor();

    @Autowired
    private InappropriateMessageService inappropriateMessageService;

    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<String> sendMessage(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String username,
            @RequestParam String messageContent) {

        // Check if either ID or username is provided
        if (id == null && (username == null || username.isEmpty())) {
            return ResponseEntity.badRequest().body("No user provided");
        }

        Optional<User> receiverOptional;

        // Find the receiver by ID or username
        if (id != null) {
            receiverOptional = messageService.findUserById(id);
        } else {
            receiverOptional = messageService.findUserByUsername(username);
        }

        if (!receiverOptional.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        if (messageContent.isEmpty()) {
            return ResponseEntity.badRequest().body("Message cannot be empty");
        }

        // Get the currently authenticated user as the sender (this assumes you have security context)
        User sender = customUserDetailsService.getCurrentUser();
        User receiver = receiverOptional.get();



        // Send the message
        Message message = messageService.sendMessage(sender, receiver, messageContent);

        if (!messageSupervisor.isMessageAppropriate(messageContent)) {
            inappropriateMessageService.saveInappropriateMessage(message, sender);
        }

        Message strippedMessage = new Message(message.getId(), StrippedUser.getStrippedUser(sender), StrippedUser.getStrippedUser(receiver), messageContent, message.getTimestamp(), message.getStatus(), true);

        // Send push notification
        String name = receiver.getUsername() + "-" + sender.getUsername();
        simpMessagingTemplate.convertAndSendToUser(name, "/specific", strippedMessage);

        return ResponseEntity.ok("Message sent successfully");
    }

    @GetMapping("/conversation")
    public String getMessagesBetweenUsers(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String username,
            Model model) {

        // Check if either ID or username is provided
        if (id == null && (username == null || username.isEmpty())) {
            model.addAttribute("error", "No user provided");
            return "conversations/conversation";
        }

        Optional<User> otherUserOptional;

        // Find the other user by ID or username
        if (id != null) {
            otherUserOptional = messageService.findUserById(id);
        } else {
            otherUserOptional = messageService.findUserByUsername(username);
        }

        if (!otherUserOptional.isPresent()) {
            model.addAttribute("error", "User not found");
            return "conversations/conversation";
        }

        // Get the currently authenticated user
        User currentUser = customUserDetailsService.getCurrentUser();
        User otherUser = otherUserOptional.get();

        // Fetch messages between the two users
        List<Message> messages = messageService.getMessagesBetweenUsers(currentUser, otherUser);

        // Convert messages to use StrippedUser and set sentByLoggedInUser flag
        List<Message> strippedMessages = messages.stream().map(message -> {
            Message strippedMessage = new Message();
            strippedMessage.setId(message.getId());
            strippedMessage.setSender(StrippedUser.getStrippedUser(message.getSender()));
            strippedMessage.setReceiver(StrippedUser.getStrippedUser(message.getReceiver()));
            strippedMessage.setMessageContent(message.getMessageContent());
            strippedMessage.setTimestamp(message.getTimestamp());
            strippedMessage.setSentByLoggedInUser(message.getSender().equals(currentUser));
            strippedMessage.setStatus(message.getStatus());
            return strippedMessage;
        }).collect(Collectors.toList());

        int lastSENTIndex = -1;
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getStatus().equals(MessageStatus.SENT)
                    && messages.get(i).getReceiver().getId().equals(currentUser.getId())) {
                messageService.changeStatus(messages.get(i), MessageStatus.SEEN);
                lastSENTIndex = i;
            }
        }
        if (lastSENTIndex != -1) {
            String name = messages.get(lastSENTIndex).getSender().getUsername() + "-" + currentUser.getUsername();
            simpMessagingTemplate.convertAndSendToUser(name, "/status", MessageStatus.SEEN);
        }

        model.addAttribute("messages", strippedMessages);
        model.addAttribute("otherUser", StrippedUser.getStrippedUser(otherUser));

        return "conversations/conversation";
    }

    @GetMapping("/conversation/all")
    public String getAllConversations(Model model) {
        User currentUser = customUserDetailsService.getCurrentUser();
        List<User> users = messageService.getAllConversation(currentUser);
        List<User> strippedUsers = users.stream().map(StrippedUser::getStrippedUser).collect(Collectors.toList());
        model.addAttribute("users", strippedUsers);
        return "conversations/allConversations";
    }
}