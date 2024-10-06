package uni.projects.talkmeow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uni.projects.talkmeow.components.InappropriateMessage;
import uni.projects.talkmeow.components.MessageApproval;
import uni.projects.talkmeow.components.user.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 06.10.2024
 */
public interface InappropriateMessageRepository extends JpaRepository<InappropriateMessage, Long> {


    List<InappropriateMessage> findAllByApproval(MessageApproval approval);

    List<InappropriateMessage> findAllBySenderAndApprovalAndMessageTimeAfter(User sender, MessageApproval approval, LocalDateTime messageTime);

    List<InappropriateMessage> findAllBySender(User sender);

    int countAllBySender(User sender);
}
