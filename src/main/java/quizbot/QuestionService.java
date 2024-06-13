package quizbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import quizbot.dao.AnswerHistoryDao;
import quizbot.dao.OptionDao;
import quizbot.dao.QuestionDao;
import quizbot.dao.UserDao;
import quizbot.form.QuestionFormManager;

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
}
