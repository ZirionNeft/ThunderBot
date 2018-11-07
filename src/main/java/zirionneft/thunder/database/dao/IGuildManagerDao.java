package zirionneft.thunder.database.dao;

import zirionneft.thunder.database.entity.Guild;
import zirionneft.thunder.database.entity.GuildManager;
import zirionneft.thunder.database.entity.User;

import java.util.List;

public interface IGuildManagerDao {
    GuildManager get(int id);

    GuildManager getByInfo(long user, long guild);

    List<GuildManager> getAll();

    void save(GuildManager manager);

    void update(GuildManager manager);

    void delete(GuildManager manager);
}
