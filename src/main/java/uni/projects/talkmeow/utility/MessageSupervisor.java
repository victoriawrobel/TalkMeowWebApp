package uni.projects.talkmeow.utility;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 06.10.2024
 */
public class MessageSupervisor {

    private List<String> prohibitedWords;
    //Possibly get the list of prohibited words from a database or a configuration file

    // Constructor
    public MessageSupervisor() {
        // Example prohibited words (can be stored in DB or config file)
        this.prohibitedWords = List.of("dog", "lizard");
    }

    // Method to check if the message is appropriate
    public boolean isMessageAppropriate(String messageContent) {
        return !containsProhibitedWords(messageContent);
    }

    // Check if the message contains prohibited words
    private boolean containsProhibitedWords(String messageContent) {
        for (String word : prohibitedWords) {
            if (messageContent.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }


}

