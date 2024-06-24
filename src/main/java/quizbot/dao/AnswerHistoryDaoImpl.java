package quizbot.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import quizbot.model.AnswerHistory;
import quizbot.model.Option;
import quizbot.model.Question;
import quizbot.model.User;

@Repository
public class AnswerHistoryDaoImpl implements AnswerHistoryDao {

    @Autowired
    private JdbcTemplate template;

    @Override
    public AnswerHistory create(User user, Question question, Option option) {
        String query = "INSERT INTO AnswerHistories (answerer, question, `option` , tag, earned) VALUES (?, ?, ?, ?, ?)";
        KeyHolder holder = new GeneratedKeyHolder();
        this.template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, user.getId());
            statement.setInt(2, question.getId());
            statement.setInt(3, option.getId());
            statement.setString(4, question.getTag());
            statement.setInt(5, option.getMark());
            return statement;
        }, holder);
        return new AnswerHistory(
                holder.getKey().intValue(),
                user.getId(),
                question.getId(),
                option.getId(),
                question.getTag(),
                option.getMark());
    }

    @Override
    public List<AnswerHistory> listByUser(User user) {
        String query = "SELECT * FROM AnswerHistories WHERE answerer = ?";
        RowMapper<AnswerHistory> mapper = new BeanPropertyRowMapper<>(AnswerHistory.class);
        return this.template.query(query, mapper, user.getId());
    }

    @Override
    public void clear(User user) {
        String query = "DELETE FROM AnswerHistories WHERE answerer = ?";
        this.template.update(query, user.getId());
    }
}
