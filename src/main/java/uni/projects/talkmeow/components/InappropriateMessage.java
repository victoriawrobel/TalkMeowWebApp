package uni.projects.talkmeow.components;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.talkmeow.components.message.Message;
import uni.projects.talkmeow.components.user.User;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "inappropriate_messages")
public class InappropriateMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "message_content", nullable = false)
    private String messageContent;

    @Column(name = "message_time", nullable = false)
    private LocalDateTime messageTime;

    @ManyToOne
    @JoinColumn(name = "sender", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "message", nullable = false)
    private Message message;

    @Column(name = "approval", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageApproval approval = MessageApproval.INAPPROPRIATE;


}
