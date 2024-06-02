package quizbot.dao;

import java.util.List;
import java.util.Optional;

import quizbot.model.Question;
import quizbot.model.User;

public interface QuestionDao {
    /**
     * Create Question by its tag, content and creator.
     * @param tag is question's tag
     * @param content is question's main content
     * @param creator is user who created question
     * @return created Question obejct
     */
    Question create(String tag, String content, User creator);

    /**
     * Find question by its id.
     * @param id is id of question
     * @return Optional<Question> for found question
     */
    Optional<Question> findById(Integer id);

    /**
     * List all question created by given User.
     * @param creator is user who created these questions
     * @return listed questions
     */
    List<Question> listAll(User creator);

    /**
     * List all created question by given user with given tag.
     * @param creator us user who created these questions
     * @param tag is question's tag
     * @return listed questions
     */
    List<Question> listByTag(User creator, String tag);
}
