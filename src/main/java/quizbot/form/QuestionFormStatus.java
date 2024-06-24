package quizbot.form;

/**
 * All question form status.
 * 
 * @see QuestionForm.status for more detailed information.
 */
public enum QuestionFormStatus {
    WaitingQuestion,
    WaitingTag,
    WaitingCorrectOption,
    AddingWrongOptions
}
