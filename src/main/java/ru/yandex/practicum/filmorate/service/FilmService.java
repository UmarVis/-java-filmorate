package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmIdException;
import ru.yandex.practicum.filmorate.exception.UserIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Getter
@Slf4j
public class FilmService {
    FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        if (filmId <= 0 || userId <= 0) {
            log.error("Film with id: {} or user with id: {} not found", filmId, userId);
            throw new FilmIdException("Film with id: " + filmId + " or user with id: " + userId + " not found");
        }
        filmStorage.getFilmById(filmId).getLikeId().add(userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        if (filmId <= 0 || userId <= 0) {
            log.error("Film with id: {} or user with id: {} not found", filmId, userId);
            throw new UserIdException("Film with id: " + filmId + " or user with id: " + userId + " not found");
        }
        filmStorage.getFilmById(filmId).getLikeId().remove(userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getFilms().stream()
                .sorted((o1, o2) -> o2.getLikeId().size() - o1.getLikeId().size())
                .limit(count)
                .collect(Collectors.toList());

    }
}

