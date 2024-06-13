package quizbot.form;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

@Getter
public class QuestionForm {
    private String question;
    private String tag;
    private List<String> options;

    public QuestionForm() {
        this.options = new LinkedList<>();
        this.question = null;
        this.tag = null;
    }

    /**
     * Return form filling status.
     * 
     * Filling question form should follow steps:
     * 0. Add question content
     * 1. Add question tag
     * 2. Add correct option of question
     * 3. Add other options of question
     * 
     * If question is null, means user just created an question form;
     * If tag is null (but question is not null), means user added question content;
     * If options is empty and tag is not null, means user added a tag to question;
     * If options has only 1 option, means user added correct option to question;
     * If options has more than 1 option, means question form has been finished.
     * @return current question form status
     */
    public QuestionFormStatus status() {
        if (this.question == null)
            return QuestionFormStatus.WaitingQuestion;
        if (this.tag == null)
            return QuestionFormStatus.WaitingTag;
        if (this.options.size() == 0)
            return QuestionFormStatus.WaitingCorrectOption;
        return QuestionFormStatus.AddingWrongOptions;
    }

    // Setters
    public void setQuestion(String question) {
        this.question = question;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void addOption(String option) {
        this.options.add(option);
    }
}
