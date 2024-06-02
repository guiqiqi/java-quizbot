package quizbot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Question {
    private Integer id;
    private String tag;
    private String content;
    private Integer creator;
}
