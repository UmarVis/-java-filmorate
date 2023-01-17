package ru.yandex.practicum.filmorate.storage.film.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundExp;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.film.dao.LikesDao;
import ru.yandex.practicum.filmorate.storage.film.dao.MpaDao;
import ru.yandex.practicum.filmorate.storage.film.dao.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;


@Slf4j
@Component("FilmStorageDaoImpl")
public class FilmStorageDaoImpl implements FilmStorage {
    private JdbcTemplate jdbc;
    private MpaDao mpaDao;
    private LikesDao likesDao;
    private GenreDao genreDao;

    @Autowired
    public FilmStorageDaoImpl(JdbcTemplate jdbc, MpaDao mpaDao, LikesDao likesDao, GenreDao genreDao) {
        this.jdbc = jdbc;
        this.mpaDao = mpaDao;
        this.likesDao = likesDao;
        this.genreDao = genreDao;
    }

    @Override
    public List<Film> getAll() {
        String sql = "select * from FILMS";
        return jdbc.query(sql, this::rowMapper);
    }

    @Override
    public Film create(Film film) {
        if (film.getMpa() != null) {
            film.setMpa(mpaDao.getId(film.getMpa().getId()));
        } else {
            throw new ValidationException("MPA is null");
        }
        String sqlQuery = "insert into FILMS (FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, " +
                "FILM_RATE, MPA_ID)" + "values (?, ?, ?, ?, ?, ?)";
        jdbc.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getRate(),film.getMpa().getId());
        String count = "select FILM_ID from FILMS order by FILM_ID desc limit 1";
        film.setId(jdbc.queryForObject(count, Integer.class));
        if (film.getGenres() != null) {
            genreDao.addGenresToFilm(film);
        }
        return getById(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        if (idCheck(film.getId()) == 0) {
            throw new IdNotFoundExp("Film with ID " + film.getId() + " not found");
        }
        if (film.getGenres() != null) {
            genreDao.updateGenresOfFilm(film);
        }
        String sqlQuery = "update FILMS set FILM_NAME = ?, FILM_DESCRIPTION = ?, FILM_RELEASE_DATE = ?, " +
                "FILM_DURATION = ?, FILM_RATE = ?, MPA_ID = ? where FILM_ID = ?";
        jdbc.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getRate(),film.getMpa().getId(), film.getId());
        return getById(film.getId());
    }

    public Film getById(Integer id) {
        if (idCheck(id) == 0) {
            throw new IdNotFoundExp("Film with ID " + id + " not found");
        }
        String sqlQuery = "select * from FILMS where FILM_ID = ?";
        return jdbc.queryForObject(sqlQuery, this::rowMapper, id);
    }

    @Override
    public void deleteById(Integer id) {
        if (idCheck(id) == 0) {
            throw new IdNotFoundExp("Film with ID " + id + " not found");
        }
        String sqlQuery = "delete from FILMS where FILM_ID = ?";
        jdbc.update(sqlQuery, id);
    }

    @Override
    public void deleteAll() {
        String sql = "delete from FILMS";
        jdbc.update(sql);
    }

    private Film rowMapper(ResultSet resultSet, int i) throws SQLException {
        return new Film(resultSet.getInt("FILM_ID"),
                resultSet.getString("FILM_NAME"),
                resultSet.getString("FILM_DESCRIPTION"),
                resultSet.getDate("FILM_RELEASE_DATE").toLocalDate(),
                resultSet.getInt("FILM_DURATION"),
                resultSet.getInt("FILM_RATE"),
                new HashSet<Integer>(likesDao.get(resultSet.getInt("FILM_ID"))),
                new HashSet<Genre>(genreDao.getFilmGenres(resultSet.getInt("FILM_ID"))),
                mpaDao.getId(resultSet.getInt("MPA_ID"))
        );
    }

    private int idCheck(int id) {
        String sql = "select count(*) from FILMS where FILM_ID = ?";
        int response = jdbc.queryForObject(sql, Integer.class, id);
        return response;
    }
}
