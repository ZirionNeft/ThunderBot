package thunder;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public class Settings {
    static Logger logger = Logger.getLogger("Settings.class");

    private Properties settings;
    private Properties keys;
    private static final String cfgPath = "settings.properties";
    private static final String keysPath = "keys.properties";

    private static final String CFG_VERSION = "0.5";

    private static HashMap<String, String> CFG = new HashMap<String, String>();

    public Settings() {
        settings = new Properties();
        keys = new Properties();

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

    public String getApiKey (String type) { return this.keys.getProperty(type).trim(); }

    public String getOne (String key) {
        return this.settings.get(key).toString();
    }

}
