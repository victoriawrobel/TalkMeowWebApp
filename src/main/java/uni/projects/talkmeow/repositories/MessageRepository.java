package uni.projects.talkmeow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uni.projects.talkmeow.components.message.Message;
import uni.projects.talkmeow.components.user.User;

import java.util.List;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 03.10.2024
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllBySenderAndReceiver(User sender, User receiver);


    @Query("SELECT DISTINCT m.receiver FROM Message m WHERE m.sender = :user")
    List<User> findAllUsersSentMessagesBy(User user);

    @Query("SELECT DISTINCT m.sender FROM Message m WHERE m.receiver = :user")
    List<User> findAllUsersReceivedMessagesBy(User user);

    Message findTop1BySenderAndReceiverOrderByTimestampDesc(User sender, User receiver);

    Message findTop1ByReceiverAndSenderOrderByTimestampDesc(User receiver, User sender);

}
