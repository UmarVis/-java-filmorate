package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.dao.GenreDao;

import java.util.List;

@Service
public class GenreService {
    private GenreDao genreDao;

    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public List<Genre> getAll() {
        return genreDao.getAll();
    }

    public Genre getId(int id) {
        return genreDao.getId(id);
    }
}
