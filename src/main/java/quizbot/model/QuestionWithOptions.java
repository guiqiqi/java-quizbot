package quizbot.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionWithOptions {
    private Question question;
    private List<Option> options;
}
