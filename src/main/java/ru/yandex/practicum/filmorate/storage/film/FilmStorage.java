package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    List<Film> get();

    Film create(Film film);

    Film updateFilm(Film film);

    Film findById(Integer id);

}
