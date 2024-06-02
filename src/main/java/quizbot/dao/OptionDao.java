package quizbot.dao;

import java.util.List;
import java.util.Optional;

import quizbot.model.Option;
import quizbot.model.Question;

public interface OptionDao {
    /**
     * Create an option by its content, mark and correctness.
     * @param content is option's text
     * @param mark is mark that user will get after select this option is option is correct
     * @param correctness indicates if option is correct
     * @param question is question related to this option
     * @return created Option object
     */
    Option create(String content, Integer mark, Boolean correctness, Question question);

    /**
     * Find option by its id.
     * @param id is option's id
     * @return Optional<Option> for found option
     */
    Optional<Option> findById(Integer id);

    /**
     * List all options related to given question.
     * @param question has relation with options
     * @return listed all options of question
     */
    List<Option> listByQuestion(Question question);
}
