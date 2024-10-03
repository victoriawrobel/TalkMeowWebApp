package uni.projects.talkmeow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uni.projects.talkmeow.components.Message;
import uni.projects.talkmeow.components.User;

import java.util.List;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 03.10.2024
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllBySenderOrReceiver(User sender, User receiver);
}
