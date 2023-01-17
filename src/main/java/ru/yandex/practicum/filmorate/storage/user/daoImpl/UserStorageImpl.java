package ru.yandex.practicum.filmorate.storage.user.daoImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundExp;
import ru.yandex.practicum.filmorate.exception.UserIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.dao.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

@Component("UserStorageImpl")
public class UserStorageImpl implements UserStorage {
    private JdbcTemplate jdbc;
    private FriendsDaoImpl fD;
    @Autowired
    public UserStorageImpl(JdbcTemplate jdbc, FriendsDaoImpl fD) {
        this.jdbc = jdbc;
        this.fD = fD;
    }

    @Override
    public List<User> get() {
        String sqlQuery = "select * from USERS";
        return jdbc.query(sqlQuery, this::RowMapper);
    }

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        String sqlQuery = "insert into USERS (USER_NAME, USER_EMAIL, USER_LOGIN, USER_BIRTHDAY) VALUES (?, ?, ?, ?)";
        jdbc.update(sqlQuery, user.getName(),user.getEmail(),user.getLogin(),user.getBirthday());
        String count = "select count(USER_ID) from USERS";
        user.setId(jdbc.queryForObject(count, Integer.class));
        return user;
    }

    @Override
    public User update(User user) {
        if (idCheck(user.getId()) == 0) {
            throw new IdNotFoundExp("User not found");
        }
        String sqlQuery = "update USERS set USER_NAME = ?, USER_EMAIL = ?, USER_LOGIN = ?, USER_BIRTHDAY = ?" +
                "where USER_ID = ?";
        jdbc.update(sqlQuery, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User findById(Integer id) {
        if (idCheck(id) == 0) {
            throw new IdNotFoundExp("User with ID " + id + " not found");
        }
        String sqlQuery = "select * from USERS where USER_ID = ?";
        return jdbc.queryForObject(sqlQuery, this::RowMapper, id);
    }

    @Override
    public void deleteUserById(int id) {
        if (idCheck(id) == 0) {
            throw new IdNotFoundExp("User with ID " + id + " not found");
        }
        String sqlQuery = "delete from USERS where USER_ID = ?";
        jdbc.update(sqlQuery, id);
    }

    @Override
    public void deleteAllUsers() {
        String sqlQuery = "delete from USERS";
        jdbc.update(sqlQuery);
    }

    private User RowMapper(ResultSet resultSet, int i) throws SQLException {
        return new User(resultSet.getInt("USER_ID"),
                resultSet.getString("USER_EMAIL"),
                resultSet.getString("USER_LOGIN"),
                resultSet.getString("USER_NAME"),
                resultSet.getDate("USER_BIRTHDAY").toLocalDate(),
                new HashSet<>(fD.getAllFriends(resultSet.getInt("USER_ID")))
        );
    }

    private int idCheck (int id) {
        String sqlQuery = "select count(*) from USERS where USER_ID = ?";
        int response =jdbc.queryForObject(sqlQuery, Integer.class, id);
        return response;
    }
}
