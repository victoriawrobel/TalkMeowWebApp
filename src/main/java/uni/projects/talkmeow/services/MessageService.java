package uni.projects.talkmeow.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uni.projects.talkmeow.components.message.Message;
import uni.projects.talkmeow.components.message.MessageStatus;
import uni.projects.talkmeow.components.user.User;
import uni.projects.talkmeow.repositories.MessageRepository;
import uni.projects.talkmeow.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    public Message sendMessage(User sender, User receiver, String messageContent) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setMessageContent(messageContent);
        message.setTimestamp(LocalDateTime.now());
        return messageRepository.save(message);
    }

    public List<Message> getMessagesBetweenUsers(User user1, User user2) {
        List<Message> messages = messageRepository.findAllBySenderAndReceiver(user1, user2);
        messages.addAll(messageRepository.findAllBySenderAndReceiver(user2, user1));
        messages.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

        User currentUser = customUserDetailsService.getCurrentUser();
        messages.forEach(message -> message.setSentByLoggedInUser(message.getSender().equals(currentUser)));

        return messages;
    }

    public List<User> getAllConversation(User user) {
        Set<User> users = new HashSet<>();
        users.addAll(messageRepository.findAllUsersSentMessagesBy(user));
        users.addAll(messageRepository.findAllUsersReceivedMessagesBy(user));
        return users.stream().toList();
    }

    public void changeStatus(Message message, MessageStatus status) {
        message.setStatus(status);
        messageRepository.save(message);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Message getLatestMessage(User user1, User user2) {
        Message message = messageRepository.findTop1BySenderAndReceiverOrderByTimestampDesc(user1, user2);
        Message message2 = messageRepository.findTop1BySenderAndReceiverOrderByTimestampDesc(user2, user1);
        if (message == null) {
            return message2;
        }
        if (message2 == null) {
            return message;
        }
        if (message.getTimestamp().isAfter(message2.getTimestamp())) {
            return message;
        }
        return message2;
    }
}

