package ru.yandex.practicum.filmorate.storage.film.daoImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundExp;
import ru.yandex.practicum.filmorate.storage.film.dao.LikesDao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class LikesDaoImpl implements LikesDao {
    private JdbcTemplate jdbc;

    @Autowired
    public LikesDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Set<Integer> get(Integer filmId) {
        String sql = "select USER_ID from FILMS_LIKES where FILM_ID = ?";
        List<Integer> likes = jdbc.queryForList(sql, Integer.class, filmId);
        return new HashSet<>(likes);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        String sqlQuery = "insert into FILMS_LIKES(FILM_ID, USER_ID)" +
                          "values (?, ?)";
        jdbc.update(sqlQuery, filmId, userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        if (idCheck(filmId) == 0) {
            throw new IdNotFoundExp("Film id " + filmId + " not found");
        }
        String sqlQuery = "delete from FILMS_LIKES where USER_ID = ? and FILM_ID = ?";
        jdbc.update(sqlQuery, userId, filmId);
    }

    private int idCheck(int id) {
        String sql = "select count(*) from FILMS_LIKES where FILM_ID = ?";
        return jdbc.queryForObject(sql, Integer.class, id);

    }
}
