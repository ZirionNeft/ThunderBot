package thunder;

import javafx.application.Application;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public class Settings {

    private Properties settings;
    private JSONObject locale;
    private static final String cfgPath = "settings.properties";

    private static final String CFG_VERSION = "0.2";

    private static HashMap<String, String> CFG = new HashMap<String, String>();

    public Settings() {
        settings = new Properties();

        CFG.put("thunder_chat_prefix", ">");
        CFG.put("thunder_db_type", "sqlite");
        CFG.put("thunder_weather_service", "OpenWeatherMap");
        CFG.put("thunder_db_path", "data.db");

        settings.setProperty("config_version", CFG_VERSION);
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

            if(!checkConfig()) {
                System.out.println("[Thunder] Configuration file is up to date!");
            } else {
                System.out.println("[Thunder] Configuration file has been updated to ver." + CFG_VERSION);
            }
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }

    private boolean checkConfig () {
        boolean updated = false;

        try {
            String ver = settings.get("config_version").toString();
            if (!ver.equals(CFG_VERSION)) {
                updated = true;

                CFG.forEach((key, value) -> {
                    if(settings.get(key).toString().isEmpty()) {
                        settings.put(key, value);
                    }
                });
                settings.setProperty("config_version", CFG_VERSION);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return updated;
    }

    public Object getOne (String key) {
        return this.settings.get(key);
    }

}
