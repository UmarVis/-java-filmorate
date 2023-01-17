package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConstraintViolationException;
import ru.yandex.practicum.filmorate.exception.FilmIdException;
import ru.yandex.practicum.filmorate.exception.UserIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.dao.LikesDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikesDao likesDao;

    public FilmService(@Qualifier("FilmStorageDaoImpl") FilmStorage filmStorage, LikesDao likesDao) {
        this.filmStorage = filmStorage;
        this.likesDao = likesDao;
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

    public List<Film> getPopularFilms(Integer count) throws ConstraintViolationException {
        if (count < 0) {
            log.error("Count {} is not positive", count);
            throw new ConstraintViolationException("Count is not positive: " + count);
        }
        return filmStorage.getAll().stream()
                .sorted((o1, o2) -> o2.getLikeId().size() - o1.getLikeId().size())
                .limit(count)
                .collect(Collectors.toList());

    }

    public List<Film> get() {
        return filmStorage.getAll();
    }

    public Film findById(int filmId) {
        return filmStorage.getById(filmId);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.updateFilm(film);
    }
}

