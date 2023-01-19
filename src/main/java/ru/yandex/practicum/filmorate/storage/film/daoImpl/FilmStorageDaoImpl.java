package ru.yandex.practicum.filmorate.storage.film.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.dao.FilmDao;
import ru.yandex.practicum.filmorate.storage.film.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.film.dao.LikesDao;
import ru.yandex.practicum.filmorate.storage.film.dao.MpaDao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;


@Slf4j
@Component("FilmStorageDaoImpl")
public class FilmStorageDaoImpl implements FilmDao {
    private final JdbcTemplate jdbc;
    private final MpaDao mpaDao;
    private final LikesDao likesDao;
    private final GenreDao genreDao;

    @Autowired
    public FilmStorageDaoImpl(JdbcTemplate jdbc, MpaDao mpaDao, LikesDao likesDao, GenreDao genreDao) {
        this.jdbc = jdbc;
        this.mpaDao = mpaDao;
        this.likesDao = likesDao;
        this.genreDao = genreDao;
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT f.*, " +
                "m.RATING as mpa_name, " +
                "m.RATING_ID as mpa_id, " +
                "FROM FILMS as f " +
                "JOIN MPA_RATINGS as m ON f.MPA_ID = m.RATING_ID ";
        return jdbc.query(sqlQuery, this::rowMapper);
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "insert into FILMS (FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, " +
                "FILM_RATE, MPA_ID)" + "values (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        if (film.getGenres() != null) {
            genreDao.addGenresToFilm(film);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (idCheck(film.getId()) == 0) {
            throw new IdNotFoundException("Film with ID " + film.getId() + " not found");
        }
        if (film.getGenres() != null) {
            genreDao.updateGenresOfFilm(film);
        }
        String sqlQuery = "update FILMS set FILM_NAME = ?, FILM_DESCRIPTION = ?, FILM_RELEASE_DATE = ?, " +
                "FILM_DURATION = ?, MPA_ID = ? where FILM_ID = ?";
        jdbc.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        return film;
    }

    public Film getById(Integer id) {
        String sqlQuery = "SELECT f.*, " +
                "m.RATING as mpa_name, " +
                "m.RATING_ID as mpa_id, " +
                "FROM FILMS as f " +
                "JOIN MPA_RATINGS as m ON f.MPA_ID = m.RATING_ID " +
                "WHERE f.FILM_ID = ?";
        try {
            return jdbc.queryForObject(sqlQuery, this::rowMapper, id);
        } catch (DataAccessException e) {
            throw new IdNotFoundException("Film with ID " + id + " not found");
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sqlQuery = "delete from FILMS where FILM_ID = ?";
        try {
            jdbc.update(sqlQuery, id);
        } catch (DataAccessException e) {
            throw new IdNotFoundException("Film with ID " + id + " not found");
        }
    }

    @Override
    public void deleteAll() {
        String sql = "delete from FILMS";
        jdbc.update(sql);
    }

    @Override
    public List<Film> findPopularFilms(Integer count) {
        String sqlQuery = "select f.*, m.RATING as mpa_name, m.RATING_ID as mpa_id from FILMS f " +
                "JOIN MPA_RATINGS as m ON f.MPA_ID = m.RATING_ID " +
                "left join FILMS_LIKES as l on l.FILM_ID = f.FILM_ID" +
                " group by f.FILM_ID order by count(l.USER_ID) desc limit ?";
        return jdbc.query(sqlQuery, this::rowMapper, count);
    }

    private Film rowMapper(ResultSet resultSet, int i) throws SQLException {
        return new Film(resultSet.getInt("FILM_ID"),
                resultSet.getString("FILM_NAME"),
                resultSet.getString("FILM_DESCRIPTION"),
                resultSet.getDate("FILM_RELEASE_DATE").toLocalDate(),
                resultSet.getInt("FILM_DURATION"),
                resultSet.getInt("FILM_RATE"),
                new HashSet<Integer>(likesDao.get(resultSet.getInt("FILM_ID"))),
                new LinkedHashSet<Genre>(genreDao.getFilmGenres(resultSet.getInt("FILM_ID"))),
                mpaDao.getId(resultSet.getInt("MPA_ID"))
        );
    }

    private int idCheck(int id) {
        String sql = "select count(*) from FILMS where FILM_ID = ?";
        int response = jdbc.queryForObject(sql, Integer.class, id);
        return response;
    }
}
