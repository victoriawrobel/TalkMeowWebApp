package uni.projects.talkmeow.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 07.10.2024
 */

@Component
public class DatabaseHealthCheck {

    private final DataSourceConfig dataSourceConfig;
    private DataSource currentDataSource;

    public DatabaseHealthCheck(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
        this.currentDataSource = dataSourceConfig.dataSource();
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000)  // Check every 5 minutes
    public void checkDatabaseHealth() {
        try (Connection connection = currentDataSource.getConnection()) {
            // Successfully connected to the current database, no action needed
        } catch (SQLException e) {
            // If connection fails, switch the data source
            System.out.println("Switching to fallback database");
            currentDataSource = dataSourceConfig.createFallbackDataSource();
        }
    }

    public DataSource getCurrentDataSource() {
        return currentDataSource;
    }
}
