package ru.yandex.practicum.filmorate.storage.film.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

public interface GenreDao {
    Collection<Genre> getAll();
    Genre getId(int id);
    Set<Genre> getFilmGenres(int filmId);
    void addGenresToFilm(Film film);

    void updateGenresOfFilm(Film film);
}
