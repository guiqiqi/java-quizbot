package quizbot.dao;

import java.util.List;
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

import quizbot.model.Question;
import quizbot.model.User;

@Repository
public class QuestionDaoImpl implements QuestionDao {

    @Autowired
    private JdbcTemplate template;

    @Override
    public Question create(String tag, String content, User creator) {
        String query = "INSERT INTO Questions (tag, content, creator) VALUES (?, ?, ?)";
        KeyHolder holder = new GeneratedKeyHolder();
        this.template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, tag);
            statement.setString(2, content);
            statement.setInt(3, creator.getId());
            return statement;
        }, holder);
        return new Question(holder.getKey().intValue(), tag, content, creator.getId());
    }

    @Override
    public Optional<Question> findById(Integer id) {
        String query = "SELECT * FROM Questions WHERE id = ?";
        RowMapper<Question> mapper = new BeanPropertyRowMapper<>(Question.class);
        try {
            return Optional.of(this.template.queryForObject(query, mapper, id));
        } catch (DataAccessException error) {
            return Optional.empty();
        }
    }

    @Override
    public List<Question> listAll(User creator) {
        String query = "SELECT * FROM Questions WHERE creator = ?";
        RowMapper<Question> mapper = new BeanPropertyRowMapper<>(Question.class);
        return this.template.query(query, mapper, creator.getId());
    }

    @Override
    public List<Question> listByTag(User creator, String tag) {
        String query = "SELECT * FROM Questions WHERE creator = ? AND tag = ?";
        RowMapper<Question> mapper = new BeanPropertyRowMapper<>(Question.class);
        return this.template.query(query, mapper, creator.getId(), tag);
    }
}
