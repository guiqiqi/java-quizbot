package quizbot.dao;

import java.util.List;

import quizbot.model.*;

public interface AnswerHistoryDao {
    /**
     * Create user question answering history.
     * @param user who answered question
     * @param question question answered by user
     * @param option user answered with question
     * @return created AnswerHistory object.
     */
    AnswerHistory create(User user, Question question, Option option);

    /**
     * List all user's answer history.
     * @param user who answered questions
     * @return listed answer histories
     */
    List<AnswerHistory> listByUser(User user);

    /**
     * Clear all user's answer historires.
     * @param user who answered questions
     */
    void clear(User user);
}
