package thunder;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class Database {
    static Logger logger = Logger.getLogger("Database.class");

    private static Connection connect() throws SQLException {
        String url = "jdbc:mysql://" + Thunder.getSettingsInstance().getOne("db_host") + ":3306/thunder_bot?useSSL=false";
        String user = Thunder.getSettingsInstance().getOne("db_user");
        String pass = Thunder.getSettingsInstance().getOne("db_password");

        return DriverManager.getConnection(url, user, pass);
    }

    static void init() {
        try (Connection c = connect()) {
            Statement statement = c.createStatement();
            statement.setQueryTimeout(30);

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `thunder_weather` (id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, discord_user_id CHAR(64) NOT NULL, city CHAR(128) NOT NULL, display_time CHAR(128) NOT NULL, UNIQUE(discord_user_id))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `thunder_emoji_usages` (id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, emoji_name CHAR(64) NOT NULL, counter INT DEFAULT 0 NOT NULL, discord_server CHAR(128) NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `thunder_stats` (id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, guild_id CHAR(64) NOT NULL, users_summary INT DEFAULT 0 NOT NULL, messages INT DEFAULT 0 NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `thunder_users` (discord_user_id CHAR(64) PRIMARY KEY NOT NULL, exp INTEGER DEFAULT 0, money INT DEFAULT 200, is_donator BOOLEAN DEFAULT 0)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `thunder_guilds_config` (id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, guild_id CHAR(64) NOT NULL, prefix CHAR(4) DEFAULT '>' NOT NULL, manage_role CHAR(64) DEFAULT '' NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `thunder_guilds_managers` (id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, guild_id CHAR(64) NOT NULL, user_id CHAR(64) NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void addWeatherRow(IUser user_id, String city, String time) {
        try (Connection c = connect()) {
            PreparedStatement statement = c.prepareStatement("INSERT INTO thunder_weather(`discord_user_id`,`city`,`display_time`) VALUES(?, ?, ?)");
            statement.setString(1, Long.toString(user_id.getLongID()));
            statement.setString(2, city);
            statement.setString(3, time);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateWeatherRow(IUser user_id, String city, String time) {
        try (Connection c = connect()) {
            PreparedStatement statement;
            statement = c.prepareStatement("UPDATE thunder_weather SET city = ?, display_time = ? WHERE discord_user_id = ?");
            statement.setString(1, city);
            statement.setString(2, time);
            statement.setString(3, Long.toString(user_id.getLongID()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateWeatherRow(IUser user_id, String time) {
        try (Connection c = connect()) {
            PreparedStatement statement;
            statement = c.prepareStatement("UPDATE thunder_weather SET display_time = ? WHERE discord_user_id = ?");
            statement.setString(1, time);
            statement.setString(2, Long.toString(user_id.getLongID()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getWeatherRow(IUser user) {
        ArrayList<String> list = new ArrayList<String>();
        try (Connection c = connect()) {
            PreparedStatement preparedStatement = c.prepareStatement("SELECT city,display_time FROM thunder_weather WHERE discord_user_id=? LIMIT 1");
            preparedStatement.setObject(1, Long.toString(user.getLongID()));
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                list.add(rs.getString("city"));
                list.add(rs.getString("display_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<IUser> getBroadcastMatchList(String time) {
        ArrayList<IUser> list = new ArrayList<IUser>();
        try (Connection c = connect()) {
            PreparedStatement preparedStatement = c.prepareStatement("SELECT discord_user_id FROM thunder_weather WHERE display_time=?");
            preparedStatement.setObject(1, time);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                IUser user = Thunder.getClientInstance().getUserByID(rs.getLong("discord_user_id"));
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static HashMap<Long, String> getGuildPrefixes() {
        HashMap<Long, String> prefixes = new HashMap<>();
        try (Connection c = connect()) {
            PreparedStatement preparedStatement = c.prepareStatement("SELECT prefix,guild_id FROM thunder_guilds_config");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Long guild = rs.getLong("guild_id");
                prefixes.put(guild, rs.getString("prefix"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prefixes;
    }

    public static boolean insertGuildConfigRow(IGuild guild) {
        try (Connection c = connect()) {
            long guild_id = guild.getLongID();
            PreparedStatement preparedStatement = c.prepareStatement("SELECT id FROM thunder_guilds_config WHERE guild_id=?");
            preparedStatement.setObject(1, Long.toString(guild_id));
            ResultSet rs = preparedStatement.executeQuery();
            if (!rs.next()) {
                PreparedStatement insert = c.prepareStatement("INSERT INTO thunder_guilds_config(`guild_id`) VALUES(?)");
                insert.setObject(1, Long.toString(guild_id));
                insert.executeUpdate();
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateGuildPrefix(IGuild guild, String prefix) {
        try (Connection c = connect()) {
            long guild_id = guild.getLongID();
            PreparedStatement update = c.prepareStatement("UPDATE thunder_guilds_config SET prefix=? WHERE guild_id=?");
            update.setString(1, prefix);
            update.setObject(2, Long.toString(guild_id));

            return update.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateGuildManager(IGuild guild, IUser user) {
        try (Connection c = connect()) {
            PreparedStatement insert = c.prepareStatement("INSERT IGNORE INTO thunder_guilds_managers(`guild_id`,`user_id`) VALUES(?, ?)");
            insert.setLong(1, guild.getLongID());
            insert.setLong(2, user.getLongID());

            return insert.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean removeGuildManager(IGuild guild, IUser user) {
        try (Connection c = connect()) {
            PreparedStatement delete = c.prepareStatement("DELETE FROM thunder_guilds_managers WHERE guild_id=? AND user_id=?");
            delete.setLong(1, guild.getLongID());
            delete.setLong(2, user.getLongID());

            return delete.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<IUser> getGuildManagers(IGuild guild) {
        ArrayList<IUser> users = new ArrayList<>();
        try (Connection c = connect()) {
            PreparedStatement preparedStatement = c.prepareStatement("SELECT user_id FROM thunder_guilds_managers WHERE guild_id=?");
            preparedStatement.setLong(1, guild.getLongID());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                IUser user = Thunder.getClientInstance().getUserByID(rs.getLong("user_id"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static IRole getGuildManageRole(IGuild guild) {
        try (Connection c = connect()) {
            PreparedStatement preparedStatement = c.prepareStatement("SELECT manage_role FROM thunder_guilds_config WHERE guild_id=?");
            preparedStatement.setLong(1, guild.getLongID());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return Thunder.getClientInstance().getRoleByID(rs.getLong("manage_role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateGuildManageRole(IGuild guild, IRole role) {
        try (Connection c = connect()) {
            PreparedStatement preparedStatement = c.prepareStatement("UPDATE thunder_guilds_config SET manage_role=? WHERE guild_id=?");
            if (role != null)
                preparedStatement.setLong(1, role.getLongID());
            else
                preparedStatement.setString(1, "");
            preparedStatement.setLong(2, guild.getLongID());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int addUsersRows(List<IUser> users) {
        try (Connection c = connect()) {
            StringBuilder q = new StringBuilder();
            for (int i = 0; i < users.size(); i++)
                q.append("(?),");
            q.deleteCharAt(q.lastIndexOf(","));

            PreparedStatement insertion = c.prepareStatement("INSERT IGNORE INTO thunder_users(`discord_user_id`) VALUES"+q.toString());

            ListIterator<IUser> iterator = users.listIterator();
            while(iterator.hasNext()) {
                insertion.setLong(iterator.nextIndex()+1, iterator.next().getLongID());
            }

            return insertion.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Integer getUserCash(IUser user) {
        try (Connection c = connect()) {
            PreparedStatement preparedStatement = c.prepareStatement("SELECT money FROM thunder_users WHERE discord_user_id=? LIMIT 1");
            preparedStatement.setLong(1, user.getLongID());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("money");
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
