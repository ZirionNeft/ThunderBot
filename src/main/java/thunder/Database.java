package thunder;

import sx.blah.discord.handle.obj.IUser;

import java.sql.*;
import java.util.ArrayList;
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

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `thunder_weather` (id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, discord_user_id CHAR(64) NOT NULL, city CHAR(128) NOT NULL, display_time CHAR(128) NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `thunder_emoji_usages` (id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, emoji_name CHAR(64) NOT NULL, counter INT DEFAULT 0 NOT NULL, discord_server CHAR(128) NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `thunder_timezones` (id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, city CHAR(128) NOT NULL, timezone CHAR(32) NOT NULL, offset CHAR(32) NOT NULL)");
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
            statement.execute();
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

}
