package quizbot.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import quizbot.model.User;

/**
 * Record which quiz with poll id user is answering.
 * 
 * It maintains a map from userId to a special AnsweringRecord object,
 * it will contains current answering question id and all options index
 * and corresponded optionIds.
 */
@Component
public class QuestionAnsweringManager {
    private Map<Integer, AnsweringRecord> records;

    public QuestionAnsweringManager() {
        this.records = new HashMap<>();
    }

    /**
     * Check if user chat contains unfinished (not answered) quiz
     * @param userr is who answering quiz mapped from database
     * @return true if have, otherwise false
     */
    public Boolean ifChatIdHaveUnfinishedQuestions(User user) {
        return this.records.containsKey(user.getId());
    }

    /**
     * Update user current answering question record.
     * @param user is who answering quiz mapped from database
     * @param questionId is question id from database
     * @param optionIds is all options id from database
     */
    public void updateQuestion(User user, Integer questionId, List<Integer> optionIds) {
        this.records.put(user.getId(), new AnsweringRecord(questionId, optionIds));
    }

    /**
     * Check answered quiz result and remove record from manager.
     * @param user is who answering quiz mapped from database
     * @return AnsweringRecord with quiz questionId and optionIds in database
     */
    public AnsweringRecord answerQuestion(User user) {
        return this.records.remove(user.getId());
    }
}
