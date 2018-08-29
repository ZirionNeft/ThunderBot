package thunder;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Settings {
    static Logger logger = Logger.getLogger("Settings.class");

    private Properties settings;
    private Properties keys;
    private static final String cfgPath = "src/main/resources/settings.properties";
    private static final String keysPath = "src/main/resources/keys.properties";

    private static final String CFG_VERSION = "0.5";

    private static HashMap<String, String> CFG = new HashMap<String, String>();
    private static ArrayList<String> KEYS = new ArrayList<String>();

    public Settings() {
        settings = new Properties();
        keys = new Properties();

        KEYS.add("discord_key");
        KEYS.add("weather_key");
        KEYS.add("translate_key");
        KEYS.add("support_server_key");

        CFG.put("thunder_chat_prefix", ">");
        CFG.put("thunder_weather_service", "OpenWeatherMap");
        CFG.put("thunder_translate_service", "Yandex");
        CFG.put("db_host", "localhost");
        CFG.put("db_user", "root");
        CFG.put("db_password", "");

        CFG.put("config_version", CFG_VERSION);
    }

    public void init() {
        try {
            if (!new File(cfgPath).exists()) {
                File file = new File(cfgPath);
                FileWriter f = new FileWriter(file);
                CFG.forEach((Key, Value) -> {
                    settings.setProperty(Key, Value);
                });
                settings.store(f, null);
            }

            FileReader file = new FileReader(cfgPath);
            settings.load(file);

            if (!new File(keysPath).exists()) {
                logger.error("File '" + keysPath + "' not found!");
                FileWriter fw = new FileWriter(new File(keysPath));
                for (String s : KEYS) {
                    keys.setProperty(s, "");
                }
                keys.store(fw, null);
                logger.warn("The file was created, specify all possible keys for the correct work of the bot!");

                System.exit(0);
            } else {
                FileReader fileReader = new FileReader(keysPath);
                keys.load(fileReader);

                keys.forEach((K, V) -> {
                    if (V.toString().trim().equals("")) logger.warn("Property '" + K + "' in '" + keysPath + "' not specified.");
                });
            }

            String ver = settings.get("config_version").toString();
            if (!ver.equals(CFG_VERSION) || settings.size() != CFG.size()) {
                CFG.forEach((key, value) -> {
                    settings.putIfAbsent(key, value);
                });
                settings.setProperty("config_version", CFG_VERSION);
                settings.store(new FileWriter(cfgPath), "");
                logger.info("File '" + cfgPath + "' has been updated to ver." + CFG_VERSION);
            }

        } catch (IOException e ) {
            e.printStackTrace();
        }
    }

    public String getKey(String type) { return this.keys.getProperty(type).trim(); }

    public String getOne (String key) {
        return this.settings.get(key).toString();
    }

}
