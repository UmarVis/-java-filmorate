package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmIdException;
import ru.yandex.practicum.filmorate.exception.UserIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.dao.FilmDao;
import ru.yandex.practicum.filmorate.storage.film.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.film.dao.LikesDao;
import ru.yandex.practicum.filmorate.storage.film.dao.MpaDao;

import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmDao filmStorage;
    private final LikesDao likesDao;
    private final GenreDao genreDao;

    public FilmService(FilmDao filmStorage, LikesDao likesDao, GenreDao genreDao) {
        this.filmStorage = filmStorage;
        this.likesDao = likesDao;
        this.genreDao = genreDao;
    }

    public void addLike(Integer filmId, Integer userId) {
        if (filmId <= 0 || userId <= 0) {
            log.error("Film with id: {} or user with id: {} not found", filmId, userId);
            throw new FilmIdException("Film with id: " + filmId + " or user with id: " + userId + " not found");
        }
        likesDao.addLike(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        if (filmId <= 0 || userId <= 0) {
            log.error("Film with id: {} or user with id: {} not found", filmId, userId);
            throw new UserIdException("Film with id: " + filmId + " or user with id: " + userId + " not found");
        }
        likesDao.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> popularFilms = filmStorage.findPopularFilms(count);
        genreDao.getFilmGenres(popularFilms);
        for (int i = 0; i < popularFilms.size(); i++) {
            likesDao.get(i);
        }
        return popularFilms;
    }

    public List<Film> get() {
        List<Film> films = filmStorage.getAll();
        genreDao.getFilmGenres(films);
        for (int i = 0; i < films.size(); i++) {
            likesDao.get(i);
        }
        return films;
    }

    public Film findById(int filmId) {
        Film film = filmStorage.getById(filmId);
        genreDao.getFilmGenres(List.of(film));
        likesDao.get(filmId);
        return film;
    }

    public Film create(Film film) {
        Film newFilm = filmStorage.create(film);
        if (newFilm.getGenres() != null) {
            genreDao.addGenresToFilm(newFilm);
        }
        return newFilm;
    }

    public Film update(Film film) {
        Film newFilm = filmStorage.updateFilm(film);
        if (newFilm.getGenres() != null) {
            genreDao.updateGenresOfFilm(newFilm);
        }
        return newFilm;
    }
}

