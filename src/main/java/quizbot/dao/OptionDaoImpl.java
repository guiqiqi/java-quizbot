package quizbot.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import quizbot.model.Option;
import quizbot.model.Question;

@Repository
public class OptionDaoImpl implements OptionDao {

    @Autowired
    private JdbcTemplate template;

    @Override
    public Option create(String content, Integer mark, Boolean correctness, Question question) {
        String query = "INSERT INTO Options (content, mark, correctness, question) VALUES (?, ?, ?, ?)";
        KeyHolder holder = new GeneratedKeyHolder();
        this.template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, content);
            statement.setInt(2, mark);
            statement.setBoolean(3, correctness);
            statement.setInt(4, question.getId());
            return statement;
        }, holder);
        return new Option(holder.getKey().intValue(), content, mark, correctness, question.getId());
    }

    @Override
    public Optional<Option> findById(Integer id) {
        String query = "SELECT * FROM Options WHERE id = ?";
        RowMapper<Option> mapper = new BeanPropertyRowMapper<>(Option.class);
        try {
            return Optional.of(this.template.queryForObject(query, mapper, id));
        } catch (DataAccessException error) {
            return Optional.empty();
        }
    }

    @Override
    public List<Option> listByQuestion(Question question) {
        String query = "SELECT * FROM Options WHERE question = ?";
        RowMapper<Option> mapper = new BeanPropertyRowMapper<>(Option.class);
        return this.template.query(query, mapper, question.getId());
    }
}
