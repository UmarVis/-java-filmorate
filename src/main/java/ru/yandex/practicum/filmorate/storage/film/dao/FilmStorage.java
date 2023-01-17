package ru.yandex.practicum.filmorate.storage.film.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAll();

    Film create(Film film);

    Film updateFilm(Film film);

    Film getById(Integer id);

    void deleteById(Integer id);
    void deleteAll();

}
