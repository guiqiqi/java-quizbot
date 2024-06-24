import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import quizbot.ApplicationConfig;
import quizbot.dao.AnswerHistoryDao;
import quizbot.dao.OptionDao;
import quizbot.dao.QuestionDao;
import quizbot.dao.UserDao;
import quizbot.model.AnswerHistory;
import quizbot.model.Option;
import quizbot.model.Question;
import quizbot.model.User;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
@ActiveProfiles("development")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDao {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private OptionDao optionDao;

    @Autowired
    private AnswerHistoryDao answerHistoryDao;

    public static final String userTelegramId = "telegram_id_012345";
    public static final String userTelegramNickname = "Doge";
    public static final String questionTag = "test";
    public static final List<String> questionContents = List.of(
            "what is the capital of thailand",
            "this is another test",
            "this is holy another test",
            "we dont want no more test",
            "final test");
    public static final Map<String, Boolean> optionsForFirstQuestion = Map.of(
            "Bangkok", true, "Bangdik", false, "Bangpusi", false, "Bangtits", false);

    @Test
    @Order(1)
    public void testCreateUser() {
        userDao.create(userTelegramId, userTelegramNickname);
        User user = userDao.findByTelegram(userTelegramId).get();
        assertEquals(user.getTelegram(), userTelegramId);
    }

    @Test
    @Order(2)
    public void testCreateQuestion() {
        User user = userDao.findByTelegram(userTelegramId).get();
        for (String content : questionContents) {
            Integer id = questionDao.create(questionTag, content, user).getId();
            Question question = questionDao.findById(id).get();
            assertEquals(id, question.getId());
        }
    }

    @Test
    @Order(3)
    public void testListQuestions() {
        User user = userDao.findByTelegram(userTelegramId).get();
        Integer listedQuesitionCount = questionDao.listAll(user).size();
        assertEquals(listedQuesitionCount, questionContents.size());
    }

    @Test
    @Order(4)
    public void testListQuestionsByTag() {
        User user = userDao.findByTelegram(userTelegramId).get();
        Integer listedQuesitionCount = questionDao.listByTag(user, questionTag).size();
        assertEquals(listedQuesitionCount, questionContents.size());
    }

    @Test
    @Order(5)
    public void testCreateOptions() {
        Question question = questionDao.findById(1).get();
        for (String optionText : optionsForFirstQuestion.keySet()) {
            Boolean correctness = optionsForFirstQuestion.get(optionText);
            Integer id = optionDao.create(optionText, 1, correctness, question).getId();
            Option option = optionDao.findById(id).get();
            assertEquals(id, option.getId());
        }
    }

    @Test
    @Order(6)
    public void testListOptions() {
        Question question = questionDao.findById(1).get();
        List<Option> options = optionDao.listByQuestion(question);
        assertEquals(options.size(), optionsForFirstQuestion.size());
    }

    @Test
    @Order(7)
    public void testCreateAnswerHistory() {
        User user = userDao.findByTelegram(userTelegramId).get();
        Question question = questionDao.findById(1).get();
        List<Option> options = optionDao.listByQuestion(question);
        for (Integer index = 0; index < options.size(); index++)
            answerHistoryDao.create(user, question, options.get(index));
    }

    @Test
    @Order(8)
    public void testListAnswerHistory() {
        User user = userDao.findByTelegram(userTelegramId).get();
        List<AnswerHistory> histories = answerHistoryDao.listByUser(user);
        assertEquals(optionsForFirstQuestion.size(), histories.size());
    }

    /**
     * Drop all tables after testing so we could create next time.
     */
    @AfterAll
    public static void cleanup(@Autowired DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            List<String> tables = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery("SHOW TABLES");
            while (resultSet.next())
                tables.add(resultSet.getString(1));
            for (String table : tables) {
                statement.execute("DROP TABLE " + table);
            }
        }
    }
}
