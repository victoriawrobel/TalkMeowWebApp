package uni.projects.talkmeow.components;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 03.10.2024
 */

public class StrippedUser {
    public static User getStrippedUser(User user) {
        return new User(user.getId(), user.getUsername(), user.getEmail(), null, null, null, null);
    }
}
