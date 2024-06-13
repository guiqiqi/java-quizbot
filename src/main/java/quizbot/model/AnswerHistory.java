package quizbot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnswerHistory {
    private Integer id;
    private Integer answerer;
    private Integer question;
    private Integer option;
    private String tag;
    private Integer earned;
}
