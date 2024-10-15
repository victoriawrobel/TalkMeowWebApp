package uni.projects.talkmeow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.projects.talkmeow.components.user.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    User findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    List<User> findAllByUsernameStartingWithIgnoreCase(String username);

}
