package com.thunderbot.zirionneft.database.dao;

import com.thunderbot.zirionneft.database.entity.Guild;

import java.util.List;
import java.util.Optional;

public interface IGuildDao {
    Optional<Guild> get(long guildId);

    List<Guild> getAll();

    void save(Guild guild);

    void update(Guild guild);

    void delete(Guild guild);
}
