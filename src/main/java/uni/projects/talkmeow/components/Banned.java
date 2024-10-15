package uni.projects.talkmeow.components;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.talkmeow.components.user.User;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "banned")
public class Banned {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "banned_user", nullable = false)
    private User bannedUser;

    @ManyToOne
    @JoinColumn(name = "banned_by", nullable = false)
    private User bannedBy;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "ban_time", nullable = false)
    private LocalDateTime banTime = LocalDateTime.now();

    @Column(name = "ban_time_end", nullable = false)
    private LocalDateTime banTimeEnd;

}
