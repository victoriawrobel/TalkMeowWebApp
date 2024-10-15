package uni.projects.talkmeow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.projects.talkmeow.components.Banned;
import uni.projects.talkmeow.components.user.User;

import java.util.List;

@Repository
public interface BannedRepository extends JpaRepository<Banned, Long> {

    List<Banned> findAllByBannedUser(User user);

}
