package ru.itis.spring.repositories.spring;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.itis.spring.models.Course;
import ru.itis.spring.repositories.CoursesRepository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class CoursesRepositoryJdbcTemplateImpl implements CoursesRepository {

    //language=SQL
    private static final String SQL_SELECT_BY_ID = "select * from course where id = ?";
    //language=SQL
    private static final String SQL_SELECT_ALL = "select * from course";
    //language=SQL
    private static final String SQL_INSERT = "insert into course(title) values (?)";

    private JdbcTemplate jdbcTemplate;

    public CoursesRepositoryJdbcTemplateImpl(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    private RowMapper<Course> courseRowMapper = (row, rowNumber) ->
            Course.builder()
                    .id(row.getLong("id"))
                    .title(row.getString("title"))
                    .build();



    public Optional<Course> find(Long id) {
        try {
            Course course = jdbcTemplate.queryForObject(SQL_SELECT_BY_ID, new Object[]{id}, courseRowMapper);
            return Optional.ofNullable(course);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Course> findAll() {
        return jdbcTemplate.query(SQL_SELECT_ALL, courseRowMapper);
    }

    public void save(Course entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection
                    .prepareStatement(SQL_INSERT);
            statement.setString(1, entity.getTitle());
            return statement;
        }, keyHolder);

        entity.setId((Long)keyHolder.getKey());
    }

    public void delete(Long id) {

    }
}