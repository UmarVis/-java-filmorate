package ru.yandex.practicum.filmorate.storage.film.daoImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.dao.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAll() {
        String sql = "select * from MPA_RATINGS";
        return jdbcTemplate.query(sql, this::rowMapper);
    }

    @Override
    public Mpa getId(int id) {
        String sql = "select * from MPA_RATINGS where RATING_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::rowMapper, id);
        } catch (DataAccessException e) {
            throw new IdNotFoundException("MPA rating with " + id + " not found");
        }
    }

    private Mpa rowMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(resultSet.getInt("RATING_ID"),
                resultSet.getString("RATING"));
    }
}
