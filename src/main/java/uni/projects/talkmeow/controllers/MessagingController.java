package uni.projects.talkmeow.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import uni.projects.talkmeow.components.Message;
import uni.projects.talkmeow.components.MessageStatus;
import uni.projects.talkmeow.components.User;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 03.10.2024
 */

@Controller
public class MessagingController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/private")
    public void sendToSpecificUser(@Payload Message message) {
        // Send message to the receiver's personal topic
        simpMessagingTemplate.convertAndSendToUser(message.getReceiver().getUsername(), "/specific", message);

        // Also send the message back to the sender's personal topic to update their own chat view
        simpMessagingTemplate.convertAndSendToUser(message.getSender().getUsername(), "/specific", message);
    }

    public void sendStatusUpdate(@Payload MessageStatus messageStatus, User receiver, User sender) {
        String name = receiver.getUsername() + "-" + sender.getUsername();
        simpMessagingTemplate.convertAndSendToUser(name, "/status", messageStatus);
    }
}


