package uni.projects.talkmeow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class DataSourceConfig {

    // Primary Database
    @Value("${spring.datasource.primary.url}")
    private String primaryDbUrl;

    @Value("${spring.datasource.primary.username}")
    private String primaryDbUsername;

    @Value("${spring.datasource.primary.password}")
    private String primaryDbPassword;

    @Value("${spring.datasource.primary.driver-class-name}")
    private String primaryDbDriverClassName;

    // Fallback Database
    @Value("${spring.datasource.fallback.url}")
    private String fallbackDbUrl;

    @Value("${spring.datasource.fallback.username}")
    private String fallbackDbUsername;

    @Value("${spring.datasource.fallback.password}")
    private String fallbackDbPassword;

    @Value("${spring.datasource.fallback.driver-class-name}")
    private String fallbackDbDriverClassName;

    @Bean
    public DataSource dataSource() {
        DataSource primaryDataSource = createPrimaryDataSource();
        if (isDatabaseAvailable(primaryDataSource)) {
            return primaryDataSource;
        } else {
            System.out.println("Primary database is not available. Switching to fallback database.");
            return createFallbackDataSource();
        }
    }

    public DataSource createPrimaryDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(primaryDbDriverClassName);
        dataSourceBuilder.url(primaryDbUrl);
        dataSourceBuilder.username(primaryDbUsername);
        dataSourceBuilder.password(primaryDbPassword);
        return dataSourceBuilder.build();
    }

    public DataSource createFallbackDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(fallbackDbDriverClassName);
        dataSourceBuilder.url(fallbackDbUrl);
        dataSourceBuilder.username(fallbackDbUsername);
        dataSourceBuilder.password(fallbackDbPassword);
        return dataSourceBuilder.build();
    }

    public static boolean isDatabaseAvailable(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}