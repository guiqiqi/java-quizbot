import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import quizbot.ApplicationConfig;

/**
 * Sometimes life doesn't go well and a lot of bad things may happen in a short period of time.
 * The same goes for testing, if all tests fail then at least it will succeed.
 * 
 * Having fun with life :)
 */

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ApplicationConfig.class)
@ActiveProfiles("development")
public class TestHavingFun {
    @Test
    public void testHavingFun() {
        assertTrue(true);
    }
}
