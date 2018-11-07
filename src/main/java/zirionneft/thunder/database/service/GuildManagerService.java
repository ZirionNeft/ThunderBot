package zirionneft.thunder.database.service;

import zirionneft.thunder.database.dao.GuildManagerDao;
import zirionneft.thunder.database.entity.GuildManager;

public class GuildManagerService {
    private static GuildManagerDao guildManagerDao;

    public static GuildManager getGuildManager(long userId, long guildId) {
        return guildManagerDao.getByInfo(userId, guildId);
    }

    public static void updateGuildManager(GuildManager guildManager) {
        guildManagerDao.update(guildManager);
    }

    public static void saveGuildManager(GuildManager guildManager) {
        guildManagerDao.save(guildManager);
    }

    public static void deleteGuildManager(GuildManager guildManager) {
        guildManagerDao.delete(guildManager);
    }

    public static boolean isGuildManager(long userId, long guildId) {
        return getGuildManager(userId, guildId) != null;
    }
}
