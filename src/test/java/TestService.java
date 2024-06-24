import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import quizbot.QuestionService;
import quizbot.form.QuestionFormStatus;
import quizbot.model.AnswerHistory;
import quizbot.model.Option;
import quizbot.model.Question;
import quizbot.model.QuestionWithOptions;
import quizbot.model.User;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
@ActiveProfiles("development")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestService {

    @Autowired
    private QuestionService service;

    public static final String userTelegramId = "telegram_id_012345";
    public static final String userTelegramNickname = "Doge";
    public static final String questionTag = "test";
    public static final String questionText = "what is the capital of thailand";
    public static final String correctOption = "Bangkok";
    public static final List<String> wrongOptions = List.of("Bangdik", "Bangpusi", "Bangtits");

    @Test
    @Order(1)
    public void testCreateUser() {
        User user = this.service.ensureUser(userTelegramId, userTelegramNickname);
        assertEquals(user.getTelegram(), userTelegramId);
    }

    @Test
    @Order(2)
    public void testNoQuestionFound() {
        User user = this.service.ensureUser(userTelegramId, userTelegramNickname);
        Optional<QuestionWithOptions> questionFound;
        questionFound = this.service.randomQuestion(user);
        assertTrue(questionFound.isEmpty());
        questionFound = this.service.randomQuestion(user, questionTag);
        assertTrue(questionFound.isEmpty());
    }

    @Test
    @Order(3)
    public void testCreateQuestion() {
        User user = this.service.ensureUser(userTelegramId, userTelegramNickname);
        QuestionFormStatus status = this.service.formStatus(user);
        assertEquals(status, QuestionFormStatus.WaitingQuestion);
        
        // Submitting to question form
        Optional<QuestionFormStatus> addingStatus;
        addingStatus = this.service.addData2QuestionForm(user, questionText);
        assertTrue(addingStatus.isPresent());
        assertEquals(addingStatus.get(), QuestionFormStatus.WaitingTag);
        addingStatus = this.service.addData2QuestionForm(user, questionTag);
        assertTrue(addingStatus.isPresent());
        assertEquals(addingStatus.get(), QuestionFormStatus.WaitingCorrectOption);
        addingStatus = this.service.addData2QuestionForm(user, correctOption);
        assertTrue(addingStatus.isPresent());
        assertEquals(addingStatus.get(), QuestionFormStatus.AddingWrongOptions);
        wrongOptions.forEach(option -> this.service.addData2QuestionForm(user, option));

        // Confirm submitting into database
        Optional<QuestionWithOptions> createdQuestion = this.service.submitQuestionForm(user);
        assertTrue(createdQuestion.isPresent());
        assertEquals(createdQuestion.get().getQuestion().getContent(), questionText);
    }

    @Test
    @Order(4)
    public void testZeroScore() {
        User user = this.service.ensureUser(userTelegramId, userTelegramNickname);
        Integer score = this.service.calculateScore(user);
        assertEquals(score, 0);
    }

    @Test
    @Order(5)
    public void testAnswerQuestion() {
        User user = this.service.ensureUser(userTelegramId, userTelegramNickname);
        Optional<AnswerHistory> hitsory = this.service.answerQuestion(user, 1, 1);
        assertTrue(hitsory.isPresent());
    }

    @Test
    @Order(6)
    public void testScoreQuerying() {
        User user = this.service.ensureUser(userTelegramId, userTelegramNickname);
        Integer score = this.service.calculateScore(user);
        assertEquals(1, score);
        Integer taggedScore = this.service.calculateScore(user, questionTag);
        assertEquals(1, taggedScore);
    }

    @Test
    @Order(7)
    public void testRandomQuestion() {
        User user = this.service.ensureUser(userTelegramId, userTelegramNickname);
        Optional<QuestionWithOptions> questionWithOptions = this.service.randomQuestion(user);
        assertTrue(questionWithOptions.isPresent());;
        Question question = questionWithOptions.get().getQuestion();
        List<Option> options = questionWithOptions.get().getOptions();
        assertEquals(question.getTag(), questionTag);
        assertEquals(options.size(), wrongOptions.size() + 1);
        
        // Test random question querying with tag
        questionWithOptions = this.service.randomQuestion(user, questionTag);
        assertTrue(questionWithOptions.isPresent());
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
