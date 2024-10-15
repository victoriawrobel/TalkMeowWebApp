package uni.projects.talkmeow.components.user;

public class StrippedUser {
    public static User getStrippedUser(User user) {
        return new User(user.getId(), user.getUsername(), user.getEmail(), null, null, null, null, user.getAvatar(), -1, null);
    }
}
