package quizbot.form;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import quizbot.model.User;

/**
 * When a user submits a request to create a new question, 
 * system will create a QuestionForm object to collect the information they provide. 
 * Once the user indicates that they have completed the question form, 
 * system will create the corresponding Question and Option objects and save them to the database.
 * 
 * This also means that incomplete question objects will be stored in memory, 
 * with the manager using the user ID as a form mapping. 
 * This allows users to create incomplete forms. However, 
 * NOTE: if the system restarts, these questions that have not been saved to the database will be lost.
 */
@Component
public class QuestionFormManager {
    private Map<Integer, QuestionForm> forms;

    public QuestionFormManager() {
        this.forms = new HashMap<>();
    }

    /**
     * Throws when user are trying to operate form with not permitted operations.
     * Like: form are now in status WaittingQuestion, but setTag called.
     */
    public class OperationNotPermitted extends Exception {
        public OperationNotPermitted(String message) {
            super(message);
        }
    }

    /**
     * Query user's current question form.
     * 
     * If user don't have an saved form in memory, this query will 
     * create an new empty form for him.
     * 
     * @param user who is submitting request
     * @return user's question form
     */
    public QuestionForm getQuestionForm(User user) {
        if (this.forms.get(user.getId()) == null)
            this.forms.put(user.getId(), new QuestionForm());
        return this.forms.get(user.getId());
    }

    /**
     * Query user's current question form status.
     * @param user who is submitting request
     * @return current question form status
     */
    public QuestionFormStatus status(User user) {
        QuestionForm form = this.getQuestionForm(user);
        return form.status();
    }

    /**
     * Remove user's current question form.
     * @param user whois submitting request
     */
    public QuestionForm remove(User user) {
        return this.forms.remove(user.getId());
    }

    /**
     * Check if user is submitting a question form.
     * @param user who is operating with bot
     * @return if user is submitting a question form
     */
    public Boolean ifUserSubmittingForm(User user) {
        if (this.forms.containsKey(user.getId()))
            return true;
        return false;
    }

    /**
     * Set question for given user's form.
     * @param user who is submitting request
     * @param question will be added to form
     * @throws OperationNotPermitted if question not in WaitingQuestion status
     */
    public void setQuestion(User user, String question) throws OperationNotPermitted {
        QuestionForm form = this.getQuestionForm(user);
        if (form.status() != QuestionFormStatus.WaitingQuestion)
            throw new OperationNotPermitted("operation setQuestion not permitted");
        form.setQuestion(question);
    }

    /**
     * Set question's tag for given user's form
     * @param user who is submitting request
     * @param tag of question
     * @throws OperationNotPermitted if question not in WaittingTag status
     */
    public void setTag(User user, String tag) throws OperationNotPermitted {
        QuestionForm form = this.getQuestionForm(user);
        if (form.status() != QuestionFormStatus.WaitingTag)
            throw new OperationNotPermitted("operation setTag not permitted");
        form.setTag(tag);
    }

    /**
     * Set correct option for given user's form
     * @param user who is submitting request
     * @param correctOption of question
     * @throws OperationNotPermitted if question not in WaittingCorrectOption status
     */
    public void setCorrectOption(User user, String correctOption) throws OperationNotPermitted {
        QuestionForm form = this.getQuestionForm(user);
        if (form.status() != QuestionFormStatus.WaitingCorrectOption)
            throw new OperationNotPermitted("operation setCorrectOption not permitted");
        form.addOption(correctOption);
    }

    /**
     * Add wrong option for given user's form
     * @param user who is submitting request
     * @param wrongOption of question
     * @throws OperationNotPermitted if question not in AddingWrongOptions status
     */
    public void addWrongOption(User user, String wrongOption) throws OperationNotPermitted {
        QuestionForm form = this.getQuestionForm(user);
        if (form.status() != QuestionFormStatus.AddingWrongOptions)
            throw new OperationNotPermitted("operation addWrongOption not permitted");
        form.addOption(wrongOption);
    }
}
