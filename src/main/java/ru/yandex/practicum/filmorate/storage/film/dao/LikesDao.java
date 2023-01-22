package ru.yandex.practicum.filmorate.storage.film.dao;

import java.util.Set;

public interface LikesDao {
    Set<Integer> get(Integer filmId);

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);
}
