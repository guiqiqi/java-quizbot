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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.ResourceUtils;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import quizbot.controller.ClearScoreCommand;
import quizbot.controller.EchoCommand;
import quizbot.controller.RandomQuestionCommand;
import quizbot.controller.UpdateHandler;
import quizbot.dao.AnswerHistoryDaoImpl;
import quizbot.dao.OptionDaoImpl;
import quizbot.dao.QuestionDaoImpl;
import quizbot.dao.UserDaoImpl;
import quizbot.form.QuestionFormManager;

@Configuration
@Import({
        UserDaoImpl.class,
        QuestionDaoImpl.class,
        OptionDaoImpl.class,
        AnswerHistoryDaoImpl.class,
        QuestionFormManager.class,
        QuestionService.class,

        // TODO: add more commands support
        EchoCommand.class,
        RandomQuestionCommand.class,
        ClearScoreCommand.class,
        UpdateHandler.class
})
public class ApplicationConfig {
    @Autowired
    @Qualifier(value = "properties")
    private Properties properties;

    /**
     * Load properties from config.properties file
     * @return loaded properties
     * @throws IOException if config file missing
     */
    @Bean(value = "properties")
    public Properties loadProperties(@Autowired String propertiesFile) throws IOException {
        Properties properties = new Properties();
        File configFile = ResourceUtils.getFile(propertiesFile);
        properties.load(new FileReader(configFile));
        return properties;
    }

    /**
     * Create data source dynamically from config properties.
     * @return created data source
     * @throws IOException if `config.properties` cannot been found
     */
    @Bean
    public DataSource dataSource() throws IOException {
        Properties properties = this.properties;
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                properties.getProperty("database.url"),
                properties.getProperty("database.username"),
                properties.getProperty("database.password"));
        dataSource.setDriverClassName(properties.getProperty("database.driver"));
        return dataSource;
    }

    @Bean(value = "propertiesFile")
    @Profile("production")
    public String productionPropertiesFile() {
        return "classpath:config.properties";
    }

    @Bean(value = "propertiesFile")
    @Profile("development")
    public String developmentPropertiesFile() {
        return "classpath:development.properties";
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

    /**
     * Initialize new telegram http client for sending message.
     * @return telegram http client
     * @throws IOException if config.properties file missing
     */
    @Bean
    public TelegramClient telegramClient() throws IOException {
        Properties properties = this.properties;
        return new OkHttpTelegramClient(properties.getProperty("telegram.bot.token"));
    }
}
