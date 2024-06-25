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
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.ResourceUtils;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import quizbot.controller.UpdateHandler;
import quizbot.dao.AnswerHistoryDao;
import quizbot.dao.AnswerHistoryDaoImpl;
import quizbot.dao.OptionDao;
import quizbot.dao.OptionDaoImpl;
import quizbot.dao.QuestionDao;
import quizbot.dao.QuestionDaoImpl;
import quizbot.dao.UserDao;
import quizbot.dao.UserDaoImpl;
import quizbot.form.QuestionFormManager;

@Configuration
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
     * Create user DAO.
     * @return created question DAO
     */
    @Bean
    public UserDao userDao() {
        return new UserDaoImpl();
    }

    /**
     * Create question DAO.
     * @return created question DAO
     */
    @Bean
    public QuestionDao questionDao() {
        return new QuestionDaoImpl();
    }

    /**
     * Create option DAO.
     * @return created option DAO
     */
    @Bean
    public OptionDao optionDao() {
        return new OptionDaoImpl();
    }

    /**
     * Create answer history DAO.
     * @return created answer history DAO
     */
    @Bean
    public AnswerHistoryDao answerHistoryDao() {
        return new AnswerHistoryDaoImpl();
    }

    /**
     * Create question form manager object.
     * @return created question form manager
     */
    @Bean
    public QuestionFormManager questionFormManager() {
        return new QuestionFormManager();
    }

    /**
     * Create question service for controller.
     * @return created question service
     */
    @Bean
    public QuestionService questionService() {
        return new QuestionService();
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

    /**
     * Initialize single thread long polling update consumer for bot
     * @return update handler
     */
    @Bean
    public LongPollingUpdateConsumer updateHandler() {
        return new UpdateHandler();
    }

    /**
     * Initalize telegram bot application with update message handler.
     * @return initialized telegram bot application
     * @param consumer autowired from updateHandler
     * @throws TelegramApiException if error occured in registering of application
     */
    @Bean(value = "bot")
    public TelegramBotsLongPollingApplication botApplication(
            @Autowired LongPollingUpdateConsumer consumer)
            throws TelegramApiException {
        TelegramBotsLongPollingApplication application = new TelegramBotsLongPollingApplication();
        application.registerBot(this.properties.getProperty("telegram.bot.token"), consumer);
        return application;
    }
}
