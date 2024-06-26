package quizbot.form;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnsweringRecord {
    private Integer questionId;
    private List<Integer> optionIds;
}