package ru.yandex.practicum.filmorate.storage.user.daoImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.dao.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

@Component("UserStorageImpl")
public class UserStorageImpl implements UserStorage {
    private final JdbcTemplate jdbc;
    private final FriendsDaoImpl fD;

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
        String sqlQuery = "insert into USERS (USER_NAME, USER_EMAIL, USER_LOGIN, USER_BIRTHDAY) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getLogin());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User update(User user) {
        if (idCheck(user.getId()) == 0) {
            throw new IdNotFoundException("User not found");
        }
        String sqlQuery = "update USERS set USER_NAME = ?, USER_EMAIL = ?, USER_LOGIN = ?, USER_BIRTHDAY = ?" +
                "where USER_ID = ?";
        jdbc.update(sqlQuery, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User findById(Integer id) {
        if (idCheck(id) == 0) {
            throw new IdNotFoundException("User with ID " + id + " not found");
        }
        String sqlQuery = "select * from USERS where USER_ID = ?";
        return jdbc.queryForObject(sqlQuery, this::RowMapper, id);
    }

    @Override
    public void deleteUserById(int id) {
        if (idCheck(id) == 0) {
            throw new IdNotFoundException("User with ID " + id + " not found");
        }
        String sqlQuery = "delete from USERS where USER_ID = ?";
        jdbc.update(sqlQuery, id);
    }

    @Override
    public void deleteAllUsers() {
        String sqlQuery = "delete from USERS";
        jdbc.update(sqlQuery);
    }

    @Override
    public List<User> getMutualFriends(Integer userId, Integer otherId) {
        String sql = "SELECT  u.* " +
                "FROM FRIENDS AS fs " +
                "JOIN USERS AS u ON fs.FRIEND_ID = u.USER_ID " +
                "WHERE fs.USER_ID = ? AND fs.FRIEND_ID IN (" +
                "SELECT FRIEND_ID FROM FRIENDS WHERE user_id = ?)";

        return jdbc.query(sql, this::RowMapper, userId, otherId);
    }

    @Override
    public List<User> getAllFriends(Integer userId) {
        String sql = "SELECT u.USER_ID AS id,u.USER_LOGIN,u.USER_NAME,u.USER_EMAIL,u.USER_BIRTHDAY " +
                "FROM FRIENDS AS f " +
                "LEFT JOIN USERS AS u ON u.USER_ID = f.FRIEND_ID " +
                "WHERE f.USER_ID = ?";

        return jdbc.query(sql, this::RowMapper, userId);
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

    private int idCheck(int id) {
        String sqlQuery = "select count(*) from USERS where USER_ID = ?";
        int response = jdbc.queryForObject(sqlQuery, Integer.class, id);
        return response;
    }
}
