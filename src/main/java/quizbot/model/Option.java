package quizbot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Option {
    private Integer id;
    private String content;
    private Integer mark;
    private Boolean correctness;
    private Integer question;
}
