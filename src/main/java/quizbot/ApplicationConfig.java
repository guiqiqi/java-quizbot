package quizbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.ResourceUtils;

@Configuration
public class ApplicationConfig {
    /**
     * Create data source dynamically from config properties.
     * @return created data source
     * @throws IOException if `config.properties` cannot been found
     */
    @Bean
    public DataSource dataSource() throws IOException {
        Properties properties = new Properties();
        File configFile = ResourceUtils.getFile("classpath:config.properties");
        properties.load(new FileReader(configFile));
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                properties.getProperty("database.url"),
                properties.getProperty("database.username"),
                properties.getProperty("database.password"));
        dataSource.setDriverClassName(properties.getProperty("database.driver"));
        return dataSource;
    }

    /**
     * Create JdbcTemplate as a singleton bean and executing database creation.
     * @param data source wired from dataSource
     * @return created JdbcTemplate
     * @throws IOException if `schema.sql` cannot been found in resources
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) throws IOException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        File sqlFile = ResourceUtils.getFile("classpath:schema.sql");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sqlFile)));
        String schema = reader.lines().collect(Collectors.joining("\n"));
        reader.close();
        for (String query : schema.split(";"))
            jdbcTemplate.execute(query);
        return jdbcTemplate;
    }
}
