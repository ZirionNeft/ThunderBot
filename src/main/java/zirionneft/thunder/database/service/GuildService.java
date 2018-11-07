package zirionneft.thunder.database.service;

import zirionneft.thunder.database.dao.GuildDao;
import zirionneft.thunder.database.entity.Guild;

import java.util.List;
import java.util.Optional;

public class GuildService {
    private static GuildDao guildDao;

    public static Guild getGuild(long guildId) {
        Optional<Guild> guild = guildDao.get(guildId);
        return guild.orElseGet(() -> {
            Guild g = new Guild(guildId);
            saveGuild(g);
            return g;
        });
    }

    public static List<Guild> getAllGuilds() {
        return guildDao.getAll();
    }

    public static void updateGuild(Guild guild) {
        guildDao.update(guild);
    }

    public static void saveGuild(Guild guild) {
        guildDao.save(guild);
    }

    public static void deleteGuild(Guild guild) {
        guildDao.delete(guild);
    }
}
