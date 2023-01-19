package ru.yandex.practicum.filmorate.storage.film.daoImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.dao.GenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAll() {
        String sql = "select * from GENRES";
        return jdbcTemplate.query(sql, this::rowMapper);
    }

    @Override
    public Genre getId(int id) {
        String sql = "select * from GENRES where GENRE_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::rowMapper, id);
        } catch (DataAccessException e) {
            throw new IdNotFoundException("Genre ID " + id + " not found");
        }
    }

    @Override
    public Set<Genre> getFilmGenres(int filmId) {
        Set<Genre> genresMap = new HashSet<>();
        String sql = "select GENRE_ID from FILMS_GENRES where FILM_ID = ?";
        List<Integer> genreIds = jdbcTemplate.queryForList(sql, Integer.class, filmId);
        for (int id : genreIds) {
            genresMap.add(getId(id));
        }
        return genresMap;
    }

    @Override
    public void addGenresToFilm(Film film) {
        String sql = "insert into FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }

    @Override
    public void updateGenresOfFilm(Film film) {
        String sqlQuery = "delete from FILMS_GENRES where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        addGenresToFilm(film);
    }

    private Genre rowMapper(ResultSet resultSet, int i) throws SQLException {
        return new Genre(resultSet.getInt("GENRE_ID"),
                resultSet.getString("GENRE_NAME"));
    }
}
