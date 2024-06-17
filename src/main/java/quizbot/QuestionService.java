package quizbot;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import quizbot.dao.AnswerHistoryDao;
import quizbot.dao.OptionDao;
import quizbot.dao.QuestionDao;
import quizbot.dao.UserDao;
import quizbot.form.QuestionForm;
import quizbot.form.QuestionFormManager;
import quizbot.form.QuestionFormStatus;
import quizbot.model.AnswerHistory;
import quizbot.model.Question;
import quizbot.model.Option;
import quizbot.model.User;

@Service
public class QuestionService {
    private final UserDao userDao;
    private final QuestionDao questionDao;
    private final OptionDao optionDao;
    private final AnswerHistoryDao answerHistoryDao;
    private final QuestionFormManager formManager;

    @Autowired
    public QuestionService(
            UserDao userDao,
            QuestionDao questionDao,
            OptionDao optionDao,
            AnswerHistoryDao answerHistoryDao,
            QuestionFormManager formManager) {
        this.userDao = userDao;
        this.questionDao = questionDao;
        this.optionDao = optionDao;
        this.answerHistoryDao = answerHistoryDao;
        this.formManager = formManager;
    }

    /**
     * Find user by its telegram id or create a new one.
     * @param telegram is user's telegram id
     * @param nickname is user's telegram nickname
     * @return found or created User object
     */
    public User ensureUser(String telegram, String nickname) {
        Optional<User> user = userDao.findByTelegram(telegram);
        if (user.isEmpty())
            return userDao.create(telegram, nickname);
        return user.get();
    }

    /**
     * Query an random question for user, if no question created, return nothing.
     * @param user who owned questions
     * @return random question or nothing
     */
    public Optional<Question> randomQuestion(User user) {
        List<Question> questions = this.questionDao.listAll(user);
        if (questions.isEmpty())
            return Optional.empty();
        Random rand = new Random();
        return Optional.of(questions.get(rand.nextInt(questions.size())));
    }

    /**
     * Query an random question for user with tag, if no question listed, return nothing.
     * @param user who owned questions
     * @return random question or nothing
     */
    public Optional<Question> randomQuestion(User user, String tag) {
        List<Question> questions = this.questionDao.listByTag(user, tag);
        if (questions.isEmpty())
            return Optional.empty();
        Random rand = new Random();
        return Optional.of(questions.get(rand.nextInt(questions.size())));
    }

    /**
     * Calculate user's total score.
     * @param user who answered questions
     * @return user's total score
     */
    public Integer calculateScore(User user) {
        List<AnswerHistory> histories = this.answerHistoryDao.listByUser(user);
        return histories.stream().collect(Collectors.summingInt(AnswerHistory::getEarned));
    }

    /**
     * Calculate user's total score with given tag.
     * @param user who answered questions
     * @return user's total score
     */
    public Integer calculateScore(User user, String tag) {
        List<AnswerHistory> histories = this.answerHistoryDao.listByUser(user);
        return histories.stream()
                .filter(history -> history.getTag() == tag)
                .collect(Collectors.summingInt(AnswerHistory::getEarned));
    }

    /**
     * Answer a question with specified option and create corresponded AnswerHistory.
     * @param user who are answering question
     * @param questionId is question's id returned from telegram
     * @param optionId is option's id returned from telegem
     * @return created answer history if question and option found, otherwise nothing
     */
    public Optional<AnswerHistory> answerQuestion(User user, Integer questionId, Integer optionId) {
        Optional<Question> question = this.questionDao.findById(optionId);
        Optional<Option> option = this.optionDao.findById(optionId);
        if (question.isEmpty() || option.isEmpty())
            return Optional.empty();
        return Optional.of(this.answerHistoryDao.create(user, question.get(), option.get()));
    }

    /**
     * Check current user question form status, if not exist, create a new one.
     * @param user is who sending query form
     * @return found form status
     */
    public QuestionFormStatus formStatus(User user) {
        QuestionForm form = this.formManager.getQuestionForm(user);
        return form.status();
    }
}
