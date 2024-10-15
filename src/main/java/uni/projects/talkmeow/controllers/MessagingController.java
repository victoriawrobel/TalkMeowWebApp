package uni.projects.talkmeow.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import uni.projects.talkmeow.components.message.Message;
import uni.projects.talkmeow.components.message.MessageStatus;
import uni.projects.talkmeow.components.user.User;

@Controller
public class MessagingController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/private")
    public void sendToSpecificUser(@Payload Message message) {
        simpMessagingTemplate.convertAndSendToUser(message.getReceiver().getUsername(), "/specific", message);

        simpMessagingTemplate.convertAndSendToUser(message.getSender().getUsername(), "/specific", message);
    }

    public void sendStatusUpdate(@Payload MessageStatus messageStatus, User receiver, User sender) {
        String name = receiver.getUsername() + "-" + sender.getUsername();
        simpMessagingTemplate.convertAndSendToUser(name, "/status", messageStatus);
    }
}


