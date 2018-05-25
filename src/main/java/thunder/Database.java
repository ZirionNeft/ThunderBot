package thunder;

import org.sqlite.JDBC;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class Database {
    Connection connection = null;

    public Database() {
        try {

            File file = new File((String)Thunder.settings.getOne("thunder_db_path"));
            if (!file.exists()) {
                if (file.createNewFile())
                    System.out.println("Database file has been created.");
            }
            DriverManager.registerDriver(new JDBC());
            this.connection = DriverManager.getConnection("jdbc:sqlite:"+ Thunder.settings.getOne("thunder_db_path"));
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        try {

            Statement statement = connection.createStatement();
            System.out.println(Thunder.settings.getMsg("db_connection"));
            statement.setQueryTimeout(30);

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS thunder_weather (id INTEGER PRIMARY KEY AUTOINCREMENT, discord_user_id TEXT, city TEXT, display_time TEXT)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS thunder_emoji_usages (id INTEGER PRIMARY KEY AUTOINCREMENT, emoji_name TEXT, counter INTEGER DEFAULT 0, discord_server TEXT)");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addWeatherRow(String user_id, String city, String time) {
        try {
            PreparedStatement statement = this.connection.prepareStatement("INSERT INTO thunder_weather(`discord_user_id`,`city`,`display_time`) VALUES(?, ?, ?)");
            statement.setObject(1, user_id);
            statement.setObject(2, city);
            statement.setObject(3, time);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
