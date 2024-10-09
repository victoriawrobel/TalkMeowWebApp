package uni.projects.talkmeow.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import uni.projects.talkmeow.components.message.Message;
import uni.projects.talkmeow.components.message.MessageStatus;
import uni.projects.talkmeow.components.user.StrippedUser;
import uni.projects.talkmeow.components.user.User;
import uni.projects.talkmeow.repositories.MessageRepository;
import uni.projects.talkmeow.services.CustomUserDetailsService;
import uni.projects.talkmeow.services.InappropriateMessageService;
import uni.projects.talkmeow.services.MessageService;
import uni.projects.talkmeow.utility.MessageSupervisor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private TemplateEngine templateEngine;

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

        if (receiverOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        if (messageContent.isEmpty()) {
            return ResponseEntity.badRequest().body("Message cannot be empty");
        }

        // Get the currently authenticated user as the sender
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
        simpMessagingTemplate.convertAndSendToUser(receiver.getUsername(), "/new-message", sender.getUsername());

        // Return the rendered fragment with the message
        return ResponseEntity.ok(renderMessageFragment(strippedMessage, sender));
    }

    // Method to render the message fragment
    private String renderMessageFragment(Message message, User currentUser) {
        Context context = new Context();
        context.setVariable("messageContent", message.getMessageContent());
        context.setVariable("timestamp", message.getTimestamp());
        context.setVariable("sentByLoggedInUser", message.getSender().getUsername().equals(currentUser.getUsername()));
        context.setVariable("last", true); // Update this logic if needed
        context.setVariable("status", message.getStatus());
        context.setVariable("otherUser", currentUser);

        return templateEngine.process("fragments/messageFragment", context); // Use the proper template engine
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
        List<UserMessage> userMessages = strippedUsers.stream().map(user -> {
            Message message = messageService.getLatestMessage(currentUser, user);
            return new UserMessage(user, message);
        }).collect(Collectors.toList());
        userMessages.sort((o1, o2) -> {
            if (o1.getMessage() == null || o2.getMessage() == null) {
                return 0;
            }
            return o2.getMessage().getTimestamp().compareTo(o1.getMessage().getTimestamp());
        });
        model.addAttribute("userMessages", userMessages);
        return "conversations/allConversations";
    }

    public class UserMessage {
        private User user;
        private Message message;

        public UserMessage(User user, Message message) {
            this.user = user;
            this.message = message;
        }

        public User getUser() {
            return user;
        }

        public Message getMessage() {
            return message;
        }
    }
}