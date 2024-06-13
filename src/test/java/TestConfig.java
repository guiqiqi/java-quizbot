import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
public class TestConfig {
    @Bean
    public UserDao userDao() {
        return new UserDaoImpl();
    }

    @Bean
    public QuestionDao questionDao() {
        return new QuestionDaoImpl();
    }

    @Bean
    public OptionDao optionDao() {
        return new OptionDaoImpl();
    }

    @Bean
    public AnswerHistoryDao answerHistoryDao() {
        return new AnswerHistoryDaoImpl();
    }

    @Bean
    public QuestionFormManager questionFormManager() {
        return new QuestionFormManager();
    }
}
