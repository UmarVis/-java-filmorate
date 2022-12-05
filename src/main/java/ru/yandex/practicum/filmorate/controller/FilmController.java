package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Set<Film> filmSet = new HashSet<>();
    private int id = 0;

    public int idGenerate() {
        return ++id;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmSet;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validate(film);
        film.setId(idGenerate());
        log.info("Фильм создан {}", film.toString());
        filmSet.add(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        validate(film);
        for (Film films : filmSet) {
            if (film.getId() == films.getId()) {
                filmSet.remove(films);
                filmSet.add(film);
                log.info("Фильм обновлен {}", film.toString());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }
        return film;
    }

    private void validate(Film film) {
        if (film.getName().isBlank()) {
            log.info("Отсутствует название фильма {}", film.toString());
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.info("Максимальная длина описания больше 200 символов {}", film.toString());
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата релиза 28 декабря 1895 года {}", film.toString());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.info("Введена отрицательная продолжительсность фильма {}", film.toString());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
