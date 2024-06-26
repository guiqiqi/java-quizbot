package quizbot.dao;

import java.util.List;
import java.util.Optional;

import quizbot.model.User;

public interface UserDao {
    /**
     * Create a new user by its telegram id and nickname.
     * @param telegram is user's telegram id
     * @param nickname is user's telegram nickname
     * @return created User object
     */
    User create(String telegram, String nickname);

    /**
     * Find user by its telegram id.
     * @param telegram is user's telegram id
     * @return Optional<User> of found User object
     */
    Optional<User> findByTelegram(String telegram);

    /**
     * Enumerate all users.
     * @return list of User object
     */
    List<User> listAllUsers();
}
