package uni.projects.talkmeow.repositories;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import uni.projects.talkmeow.components.User;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 02.10.2024
 */

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findByEmail(String email);

    boolean existsByUsername(String username);

}
