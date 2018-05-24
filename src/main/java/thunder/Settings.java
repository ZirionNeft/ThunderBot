package thunder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Settings {

    private JSONObject settings;
    private static final String cfgPath = "settings.json";

    public Settings() {

    }

    public void init() {
        settings = new JSONObject();
        JSONParser parser = new JSONParser();

        try {
            if (!new File(cfgPath).exists()) {
                FileWriter f = new FileWriter(cfgPath);

                // Locale messages - RU
                settings.put("args_not_found", "Не указаны аргументы!");
                settings.put("auth_fail", "Ошибка авторизации!");

                // General settings
                settings.put("thunder_chat_prefix", ">");

                settings.put("config_version", "0.1");
                f.write(settings.toJSONString());
                f.close();
            } else {
                FileReader file = new FileReader(cfgPath);
                settings = (JSONObject) parser.parse(file);
            }
        } catch (ParseException | IOException e ) {
            e.printStackTrace();
        }
    }

    public JSONObject getAll () {
        return this.settings;
    }

    public Object getOne (String key) {
        return this.settings.get((Object) key);
    }

    public String getMsg (String key) {
        return (String) this.settings.get((Object) key);
    }

}
