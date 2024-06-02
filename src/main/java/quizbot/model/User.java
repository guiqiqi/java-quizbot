package quizbot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class User {
    private Integer id;
    private String telegram;
    private String nickname;
}
