package quizbot;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import quizbot.dao.AnswerHistoryDao;
import quizbot.dao.OptionDao;
import quizbot.dao.QuestionDao;
import quizbot.dao.UserDao;
import quizbot.form.QuestionForm;
import quizbot.form.QuestionFormManager;
import quizbot.form.QuestionFormStatus;
import quizbot.form.QuestionFormManager.OperationNotPermitted;
import quizbot.model.AnswerHistory;
import quizbot.model.Question;
import quizbot.model.QuestionWithOptions;
import quizbot.model.Option;
import quizbot.model.User;

@Service
public class QuestionService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private OptionDao optionDao;

    @Autowired
    private AnswerHistoryDao answerHistoryDao;

    @Autowired
    private QuestionFormManager formManager;

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
    @Transactional
    public Optional<QuestionWithOptions> randomQuestion(User user) {
        List<Question> questions = this.questionDao.listAll(user);
        return this.selectQuestionFromList(questions);
    }

    /**
     * Select an random question from question list and query related options.
     * @param questions is all questions selected for given user
     * @return QuestionWithOptions related to given question
     */
    private Optional<QuestionWithOptions> selectQuestionFromList(List<Question> questions) {
        if (questions.isEmpty())
            return Optional.empty();
        Random rand = new Random();
        Question question = questions.get(rand.nextInt(questions.size()));
        List<Option> options = this.optionDao.listByQuestion(question);
        QuestionWithOptions form = new QuestionWithOptions(question, options);
        return Optional.of(form);
    }

    /**
     * Query an random question for user with tag, if no question listed, return nothing.
     * @param user who owned questions
     * @return random question or nothing
     */
    @Transactional
    public Optional<QuestionWithOptions> randomQuestion(User user, String tag) {
        List<Question> questions = this.questionDao.listByTag(user, tag);
        return this.selectQuestionFromList(questions);
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
                .filter(history -> history.getTag().equals(tag))
                .collect(Collectors.summingInt(AnswerHistory::getEarned));
    }

    /**
     * Reset user's total score.
     * @param user who answered questions
     */
    public void resetScore(User user) {
        this.answerHistoryDao.clear(user);
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

    /**
     * Set data to question form during to its status.
     * @param user who is operating on question form
     * @param data its going to be set on form
     * @return finished question form status, if something wrong in submitting, return nothing
     */
    public Optional<QuestionFormStatus> addData2QuestionForm(User user, String data) {
        QuestionForm form = this.formManager.getQuestionForm(user);
        try {
            switch (form.status()) {
                case QuestionFormStatus.WaitingQuestion:
                    this.formManager.setQuestion(user, data);
                    break;
                case QuestionFormStatus.WaitingTag:
                    this.formManager.setTag(user, data);
                    break;
                case QuestionFormStatus.WaitingCorrectOption:
                    this.formManager.setCorrectOption(user, data);
                    break;
                case QuestionFormStatus.AddingWrongOptions:
                    this.formManager.addWrongOption(user, data);
                    break;
                default:
                    break;
            }
        } catch (OperationNotPermitted error) {
            return Optional.empty();
        }
        return Optional.of(form.status());
    }

    /**
     * Submit wrong options added question form to database and reset user form status.
     * If submitting success, return created question and options object; if not, return nothing.
     * @param user who is operating on question form
     * @return submitted question form
     */
    @Transactional
    public Optional<QuestionWithOptions> submitQuestionForm(User user) {
        QuestionForm form = this.formManager.getQuestionForm(user);

        // If current status not euqals to last (which is AddingWrongOptions) status submitting is not allowed
        if (form.status() != QuestionFormStatus.AddingWrongOptions)
            return Optional.empty();
        List<String> options = form.getOptions();

        Question question = this.questionDao.create(form.getTag(), form.getQuestion(), user);
        List<Option> createdOptions = new LinkedList<>();
        for (Integer index = 0; index < options.size(); index++) {
            // The very first option is the correct one
            String option = options.get(index);
            Option creadtedOption;
            if (index == 0)
                creadtedOption = this.optionDao.create(option, 1, true, question);
            else
                creadtedOption = this.optionDao.create(option, 1, false, question);
            createdOptions.add(creadtedOption);
        }

        // Clear user current submitted form in memory
        this.formManager.remove(user);
        QuestionWithOptions questionWithOptions = new QuestionWithOptions(question, createdOptions);
        return Optional.of(questionWithOptions);
    }
}
