import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
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
import quizbot.form.QuestionForm;
import quizbot.form.QuestionFormManager;
import quizbot.form.QuestionFormStatus;
import quizbot.model.User;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfig.class, TestConfig.class })
@ActiveProfiles("development")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestQuestionFormManager {
    @Autowired
    private QuestionFormManager formManager;

    public static final Integer userId = 0;
    public static final String question = "question";
    public static final String tag = "tag";
    public static final String correctOption = "correct";
    public static final String wrongOption = "wrong";
    public static final User user = new User();

    /**
     * Set user id before running all tests.
     */
    @BeforeAll
    public static void setUserId() {
        user.setId(userId);
    }

    @Test
    @Order(1)
    public void testCreateQuestionForm() {
        QuestionForm form = formManager.getQuestionForm(user);
        assertEquals(form.status(), QuestionFormStatus.WaitingQuestion);
    }

    @Test
    @Order(2)
    public void testInvalidOpeation() {
        assertThrows(QuestionFormManager.OperationNotPermitted.class,
                () -> formManager.setTag(user, tag));
        assertThrows(QuestionFormManager.OperationNotPermitted.class,
                () -> formManager.setCorrectOption(user, correctOption));
        assertThrows(QuestionFormManager.OperationNotPermitted.class,
                () -> formManager.addWrongOption(user, wrongOption));
    }

    @Test
    @Order(3)
    public void testFillingQuestionForm() throws RuntimeException {
        try {
            formManager.setQuestion(user, question);
            formManager.setTag(user, tag);
            formManager.setCorrectOption(user, correctOption);
            formManager.addWrongOption(user, wrongOption);
        } catch (QuestionFormManager.OperationNotPermitted error) {
            throw new RuntimeException("filiing question form failed");
        }
    }

    @Test
    @Order(4)
    public void testFinishForm() {
        QuestionForm form = formManager.remove(user);
        assertEquals(form.getTag(), tag);
        assertEquals(form.getQuestion(), question);
        assertEquals(form.getOptions().size(), 2);
        assertEquals(form.getOptions().get(0), correctOption);
    }
}
