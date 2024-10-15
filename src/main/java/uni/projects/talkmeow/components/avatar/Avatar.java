package uni.projects.talkmeow.components.avatar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "avatars")
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source", nullable = false)
    private String source;

    @JsonIgnore
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "image", nullable = false, columnDefinition = "BYTEA")
    private byte[] image;

    @Column(name = "fur_color", nullable = false)
    @Enumerated(EnumType.STRING)
    private Color furColor;

    @Column(name = "eye_color", nullable = false)
    @Enumerated(EnumType.STRING)
    private Color eyeColor;

    @Column(name = "pattern", nullable = false)
    @Enumerated(EnumType.STRING)
    private Pattern pattern;

    @Column(name = "breed", nullable = false)
    @Enumerated(EnumType.STRING)
    private Breed breed;

    @Column(name = "age", nullable = false)
    @Enumerated(EnumType.STRING)
    private Age age;


}
