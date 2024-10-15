package uni.projects.talkmeow.components.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.talkmeow.components.Banned;
import uni.projects.talkmeow.components.avatar.Avatar;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    @Column(name = "security_question", nullable = false)
    private String securityQuestion;

    @Column(name = "security_answer", nullable = false)
    private String securityAnswer;

    @ManyToOne
    @JoinColumn(name = "avatar", nullable = false)
    private Avatar avatar;

    @Column(name = "ban_strike")
    private int banStrike = 0;

    @Column(name = "user_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.ACTIVE;

    public User(User user) {
        this.id = user.id;
        this.username = user.username;
        this.email = user.email;
        this.password = user.password;
        this.role = user.role;
        this.securityQuestion = user.securityQuestion;
        this.securityAnswer = user.securityAnswer;
        this.avatar = user.avatar;
        this.banStrike = user.banStrike;
        this.userStatus = user.userStatus;
    }

}
