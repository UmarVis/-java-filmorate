package ru.yandex.practicum.filmorate.storage.film.daoImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.dao.GenreDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

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
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToGenre(rs));
    }

    @Override
    public Genre getId(int id) {
        String sql = "select * from GENRES where GENRE_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToGenre(rs), id);
        } catch (DataAccessException e) {
            throw new IdNotFoundException("Genre ID " + id + " not found");
        }
    }

    @Override
    public void getFilmGenres(List<Film> films) {
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));

        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));

        final String sqlQuery = "SELECT * FROM GENRES AS g, FILMS_GENRES AS fg " +
                "WHERE fg.GENRE_ID = g.GENRE_ID AND fg.FILM_ID in ("
                + inSql + ")";

        jdbcTemplate.query(sqlQuery, (rs) -> {
            final Film film = filmById.get(rs.getInt("FILM_ID"));
            film.getGenres().add(mapRowToGenre(rs));
        }, films.stream().map(Film::getId).toArray());
    }


    @Override
    public void addGenresToFilm(Film film) {
        Set<Genre> genres = film.getGenres();
        if (genres == null || genres.isEmpty()) {
            return;
        }
        final List<Genre> genreList = new ArrayList<>(genres);
        jdbcTemplate.batchUpdate(
                "MERGE INTO FILMS_GENRES key(FILM_ID,GENRE_ID) values (?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement statement, int i) throws SQLException {
                        statement.setInt(1, film.getId());
                        statement.setInt(2, genreList.get(i).getId());
                    }

                    public int getBatchSize() {
                        return genreList.size();
                    }
                }
        );
    }

    @Override
    public void updateGenresOfFilm(Film film) {
        String sqlQuery = "delete from FILMS_GENRES where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        addGenresToFilm(film);
    }

    public static Genre mapRowToGenre(ResultSet resultSet) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();
    }
}
