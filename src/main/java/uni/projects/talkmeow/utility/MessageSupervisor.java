package uni.projects.talkmeow.utility;

import java.util.List;
import java.util.regex.Pattern;

import static uni.projects.talkmeow.utility.Defaults.prohibitedWords;

public class MessageSupervisor {

    public boolean isMessageAppropriate(String messageContent) {
        return !containsProhibitedWords(messageContent);
    }

    private boolean containsProhibitedWords(String messageContent) {
        for (String word : prohibitedWords) {
            if (messageContent.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }


}

