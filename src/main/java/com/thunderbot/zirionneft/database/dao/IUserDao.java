package com.thunderbot.zirionneft.database.dao;

import com.thunderbot.zirionneft.database.entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserDao {

    Optional<User> get(long userId);

    List<User> getAll();

    List<User> getMatchesBroadcastTime(String time);

    void save(User user);

    void update(User user);

    void delete(User user);
}
