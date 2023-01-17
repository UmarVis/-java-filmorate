package ru.yandex.practicum.filmorate.storage.film.daoImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundExp;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.dao.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class MpaDaoImpl implements MpaDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> getAll() {
        String sql = "select * from MPA_RATINGS";
        return jdbcTemplate.query(sql, this::rowMapper);
    }

    @Override
    public Mpa getId(int id) {
        if (idCheck(id) == 0) {
            throw new IdNotFoundExp("MPA rating with " + id + " not found");
        }
        String sql = "select * from MPA_RATINGS where RATING_ID = ?";
        return jdbcTemplate.queryForObject(sql, this::rowMapper, id);
    }

    private Mpa rowMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(resultSet.getInt("RATING_ID"),
                resultSet.getString("RATING"));
    }

    private int idCheck(int id) {
        String sql = "select count(*) from MPA_RATINGS where RATING_ID = ?";
        int response = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return response;
    }
}
