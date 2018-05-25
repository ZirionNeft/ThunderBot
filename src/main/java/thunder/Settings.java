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
    private JSONObject locale;
    private static final String cfgPath = "settings.json";

    private static final String CFG_VERSION = "0.11";

    public Settings() {

    }

    public void init() {
        settings = new JSONObject();
        locale = new JSONObject();
        JSONParser parser = new JSONParser();

        try {
            if (!new File(cfgPath).exists()) {
                cfgBuilder();
            } else {
                FileReader file = new FileReader(cfgPath);
                settings = (JSONObject) parser.parse(file);

                if (getOne("config_version") == null || getOne("config_version") != CFG_VERSION)
                    cfgBuilder();
            }

            String locale = (String)getOne("thunder_chat_locale");
            if (!new File(locale+".json").exists() && locale != "en") {
                settings.replace("thunder_chat_locale", "en");
            }
            if (!new File(locale+".json").exists()) {
                localeBuilder(locale);
            } else {
                FileReader file = new FileReader(locale + ".json");
                this.locale = (JSONObject) parser.parse(file);

                if (getMsg("config_version") == null || getMsg("config_version") != CFG_VERSION)
                    localeBuilder(locale);
            }

        } catch (ParseException | IOException e ) {
            e.printStackTrace();
        }
    }

    private void cfgBuilder () {
        try {
            File file = new File(cfgPath);
            if (file.exists())
                file.renameTo(new File(cfgPath.substring(0, cfgPath.length()-5)+"_old.json"));

            FileWriter f = new FileWriter(cfgPath);

            // General settings
            settings.put("thunder_chat_prefix", ">");
            settings.put("thunder_chat_locale", "en");
            settings.put("thunder_db_type", "sqlite");
            settings.put("thunder_db_path", "data.db");

            settings.put("config_version", CFG_VERSION);
            f.write(settings.toJSONString());
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void localeBuilder (String locale) {
        try {
            File file = new File(locale+".json");
            if (file.exists())
                file.renameTo(new File(locale + "_old.json"));

            FileWriter f = new FileWriter(locale + ".json");

            // Locale messages - EN
            this.locale.put("error_args_not_found", "ERROR: The first argument is the bot token not specified");
            this.locale.put("error_auth_fail", "ERROR: Auth error!");

            this.locale.put("db_connection", "Connection to database...");

            this.locale.put("config_version", CFG_VERSION);
            f.write(this.locale.toJSONString());
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getCfgObject () {
        return this.settings;
    }

    public JSONObject getLocaleObject () {
        return this.locale;
    }

    public Object getOne (String key) {
        return this.settings.get((Object) key);
    }

    public String getMsg (String key) {
        return (String) this.locale.get((Object) key);
    }

}
