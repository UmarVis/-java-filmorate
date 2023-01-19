package ru.yandex.practicum.filmorate.storage.film.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {
    List<Film> getAll();

    Film create(Film film);

    Film updateFilm(Film film);

    Film getById(Integer id);

    void deleteById(Integer id);

    void deleteAll();

    List<Film> findPopularFilms(Integer count);

}
