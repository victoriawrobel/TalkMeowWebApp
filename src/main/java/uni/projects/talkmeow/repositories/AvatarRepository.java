package uni.projects.talkmeow.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uni.projects.talkmeow.components.avatar.Avatar;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long>, JpaSpecificationExecutor<Avatar> {

    Avatar findBySource(String source);

    @Transactional
    void deleteBySource(String source);

}
