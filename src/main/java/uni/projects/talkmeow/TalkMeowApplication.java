package uni.projects.talkmeow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TalkMeowApplication {

    public static void main(String[] args) {
        SpringApplication.run(TalkMeowApplication.class, args);
    }

}
