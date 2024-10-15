package uni.projects.talkmeow.config;

import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uni.projects.talkmeow.TalkMeowApplication;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@Getter
public class DatabaseHealthCheck {

    private final DataSourceConfig dataSourceConfig;
    private DataSource currentDataSource;

    public DatabaseHealthCheck(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
        this.currentDataSource = dataSourceConfig.dataSource();
    }

    @Scheduled(fixedDelay = 60 * 1000)
    public void checkDatabaseHealth() {
        System.out.println("Checking database health");
        if (DataSourceConfig.isDatabaseAvailable(currentDataSource)){

        } else {
            System.out.println("Restarting the application");
            TalkMeowApplication.restart();
        }
    }

}
