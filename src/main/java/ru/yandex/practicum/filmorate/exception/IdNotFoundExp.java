package ru.yandex.practicum.filmorate.exception;

public class IdNotFoundExp extends RuntimeException {
    public IdNotFoundExp(String message) {
        super(message);
    }
}
