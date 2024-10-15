package uni.projects.talkmeow.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uni.projects.talkmeow.components.InappropriateMessage;
import uni.projects.talkmeow.components.MessageApproval;
import uni.projects.talkmeow.components.message.Message;
import uni.projects.talkmeow.components.user.User;
import uni.projects.talkmeow.repositories.InappropriateMessageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InappropriateMessageService {

    @Autowired
    private InappropriateMessageRepository inappropriateMessageRepository;

    public void saveInappropriateMessage(Message message, User sender) {
        InappropriateMessage inappropriateMessage = new InappropriateMessage();
        inappropriateMessage.setMessageContent(message.getMessageContent());
        inappropriateMessage.setSender(sender);
        inappropriateMessage.setApproval(MessageApproval.NEEDS_VERIFICATION);
        inappropriateMessage.setMessage(message);
        inappropriateMessage.setMessageTime(message.getTimestamp());
        inappropriateMessageRepository.save(inappropriateMessage);
    }

    public List<InappropriateMessage> getAllToVerify() {
        return inappropriateMessageRepository.findAllByApproval(MessageApproval.NEEDS_VERIFICATION);
    }

    public void updateMessageApproval(Long messageId, MessageApproval approval) {
        Optional<InappropriateMessage> messageOptional = inappropriateMessageRepository.findById(messageId);
        if (messageOptional.isPresent()) {
            InappropriateMessage inappropriateMessage = messageOptional.get();
            inappropriateMessage.setApproval(approval);
            inappropriateMessageRepository.save(inappropriateMessage);
        }
    }

    public List<InappropriateMessage> getMessagesAfterLastBan(User user, LocalDateTime lastBanTime) {
        return inappropriateMessageRepository.findAllBySenderAndApprovalAndMessageTimeAfter(user, MessageApproval.INAPPROPRIATE, lastBanTime);
    }

    public List<InappropriateMessage> getMessagesBySender(User user) {
        return inappropriateMessageRepository.findAllBySender(user);
    }

    public int countMessagesBySender(User user) {
        return inappropriateMessageRepository.countAllBySender(user);
    }

}
