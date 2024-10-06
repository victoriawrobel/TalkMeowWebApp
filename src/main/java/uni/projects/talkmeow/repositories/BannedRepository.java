package uni.projects.talkmeow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.projects.talkmeow.components.Banned;
import uni.projects.talkmeow.components.user.User;

import java.util.List;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 06.10.2024
 */

@Repository
public interface BannedRepository extends JpaRepository<Banned, Long> {

    List<Banned> findAllByBannedUser(User user);

}
