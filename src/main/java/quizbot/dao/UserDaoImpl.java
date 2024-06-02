package quizbot.dao;

import java.util.Optional;

import java.sql.Statement;
import java.sql.PreparedStatement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import quizbot.model.User;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private JdbcTemplate template;

    @Override
    public User create(String telegram, String nickname) {
        String query = "INSERT INTO Users (telegram, nickname) VALUES (?, ?)";
        KeyHolder holder = new GeneratedKeyHolder();
        this.template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, telegram);
            statement.setString(2, nickname);
            return statement;
        }, holder);
        return new User(holder.getKey().intValue(), telegram, nickname);
    }

    @Override
    public Optional<User> findByTelegram(String telegram) {
        String query = "SELECT * FROM Users WHERE telegram = ?";
        RowMapper<User> mapper = new BeanPropertyRowMapper<>(User.class);
        try {
            return Optional.of(this.template.queryForObject(query, mapper, telegram));
        } catch (DataAccessException error) {
            return Optional.empty();
        }
    }
}
