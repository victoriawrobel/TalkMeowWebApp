package uni.projects.talkmeow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uni.projects.talkmeow.components.avatar.Avatar;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 05.10.2024
 */

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long>, JpaSpecificationExecutor<Avatar> {
}
