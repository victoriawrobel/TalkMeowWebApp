package uni.projects.talkmeow.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uni.projects.talkmeow.components.Message;
import uni.projects.talkmeow.components.MessageStatus;
import uni.projects.talkmeow.components.StrippedUser;
import uni.projects.talkmeow.components.User;
import uni.projects.talkmeow.services.CustomUserDetailsService;
import uni.projects.talkmeow.services.MessageService;

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

    @GetMapping("/send")
    public String send(Model model, @RequestParam(required = false) String error) {
        if (error != null) {
            switch (error) {
                case "no_user":
                    model.addAttribute("error", "No user provided");
                    break;
                case "user_not_found":
                    model.addAttribute("error", "User not found");
                    break;
                case "empty_message":
                    model.addAttribute("error", "Message cannot be empty");
                    break;
            }
        }
        return "conversations/sendMessage";
    }

    // Send message to another user by ID or username
    @PostMapping("/send")
    public String sendMessage(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String username,
            @RequestParam String messageContent) {

        // Check if either ID or username is provided
        if (id == null && (username == null || username.isEmpty())) {
            return "redirect:/api/messages/send?error=no_user";
        }

        Optional<User> receiverOptional;

        // Find the receiver by ID or username
        if (id != null) {
            receiverOptional = messageService.findUserById(id);
        } else {
            receiverOptional = messageService.findUserByUsername(username);
        }

        if (!receiverOptional.isPresent()) {
            return "redirect:/api/messages/send?error=user_not_found";
        }
        if (messageContent.isEmpty()) {
            return "redirect:/api/messages/send?error=empty_message";
        }

        // Get the currently authenticated user as the sender (this assumes you have security context)
        User sender = customUserDetailsService.getCurrentUser();
        User receiver = receiverOptional.get();

        // Send the message
        messageService.sendMessage(sender, receiver, messageContent);

        return "redirect:/api/messages/conversation?id=" + receiver.getId();
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

        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getStatus().equals(MessageStatus.SENT)
                    && messages.get(i).getReceiver().getId().equals(currentUser.getId())) {
                messageService.changeStatus(messages.get(i), MessageStatus.SEEN);
            }
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