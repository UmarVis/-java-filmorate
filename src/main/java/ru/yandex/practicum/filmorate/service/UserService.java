package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.dao.FriendsDao;
import ru.yandex.practicum.filmorate.storage.user.dao.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendsDao friendsDao;

    public UserService(UserStorage userStorage, FriendsDao friendsDao) {
        this.userStorage = userStorage;
        this.friendsDao = friendsDao;
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (userId <= 0 || friendId <= 0) {
            log.debug("Check user id {} check friend id {}", userId, friendId);
            throw new UserIdException("User with id: " + userId + " or user friend with id: " + friendId + " not found");
        }
        friendsDao.addFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        if (userId <= 0 || friendId <= 0) {
            log.debug("Check user id {} check friend id {}", userId, friendId);
            throw new UserIdException("User with id: " + userId + " or user friend with id: " + friendId + " not found");
        }
        friendsDao.deleteFriend(userId, friendId);
    }

    public List<User> getAllFriends(Integer userId) {
        if (userId <= 0) {
            log.error("Invalid user ID: ", userId);
            throw new UserIdException("User id " + userId + " not found");
        }
        return friendsDao.getAllFriends(userId);
    }

    public List<User> getMutualFriends(Integer userId, Integer otherId) {
        return friendsDao.getMutualFriends(userId, otherId);
    }

    public List<User> get() {
        return userStorage.get();
    }

    public User findById(int userId) {
        return userStorage.findById(userId);
    }

    public User create(User user) {
        setNameIfLoginEmpty(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        setNameIfLoginEmpty(user);
        return userStorage.update(user);
    }

    private void setNameIfLoginEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
