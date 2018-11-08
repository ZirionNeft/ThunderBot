package com.thunderbot.zirionneft;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Settings {
    static Logger logger = Logger.getLogger("Settings.class");

    private Properties settings;
    private Properties keys;
    private static Properties locale;

    private static final String CFG_PATH = "src/main/resources/settings.properties";
    private static final String KEYS_PATH = "src/main/resources/keys.properties";
    private static String LOCALE_PATH;

    private static final String CFG_VERSION = "0.5";

    private static HashMap<String, String> CFG = new HashMap<String, String>();
    private static ArrayList<String> KEYS = new ArrayList<String>();

    static {
        locale = new Properties();
        try {
            if (LOCALE_PATH == null)
                locale.load(new FileReader("src/main/resources/locales/en.properties"));
            else
                locale.load(new FileReader(LOCALE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Settings() {
        settings = new Properties();
        keys = new Properties();

        KEYS.add("discord_key");
        KEYS.add("discord_test_key");
        KEYS.add("weather_key");
        KEYS.add("translate_key");
        KEYS.add("support_server_key");

        CFG.put("thunder_chat_prefix", ">");
        CFG.put("thunder_weather_service", "OpenWeatherMap");
        CFG.put("thunder_translate_service", "Yandex");
        CFG.put("thunder_locale", "en");

        CFG.put("config_version", CFG_VERSION);
    }

    public void init() {
        try {
            if (!new File(CFG_PATH).exists()) {
                FileWriter f = new FileWriter(new File(CFG_PATH));
                CFG.forEach((Key, Value) -> {
                    settings.setProperty(Key, Value);
                });
                settings.store(f, null);
            }

            FileReader file = new FileReader(CFG_PATH);
            settings.load(file);

            if (!new File(KEYS_PATH).exists()) {
                logger.error("File '" + KEYS_PATH + "' not found!");
                FileWriter fw = new FileWriter(new File(KEYS_PATH));
                for (String s : KEYS) {
                    keys.setProperty(s, "");
                }
                keys.store(fw, null);
                logger.warn("The file have been created. Specify all possible keys for the correct work of the bot!");

                System.exit(0);
            } else {
                FileReader fileReader = new FileReader(KEYS_PATH);
                keys.load(fileReader);

                keys.forEach((K, V) -> {
                    if (V.toString().trim().equals(""))
                        logger.warn("Property '" + K + "' in '" + KEYS_PATH + "' not specified.");
                });
            }

            String ver = settings.get("config_version").toString();
            if (!ver.equals(CFG_VERSION) || settings.size() != CFG.size()) {
                CFG.forEach((key, value) -> {
                    settings.putIfAbsent(key, value);
                });
                settings.setProperty("config_version", CFG_VERSION);
                settings.store(new FileWriter(CFG_PATH), "");
                logger.info("File '" + CFG_PATH + "' has been updated to ver." + CFG_VERSION);
            }

            LOCALE_PATH = "src/main/resources/locales/" + getOne("thunder_locale") + ".properties";
            locale.load(new FileReader(LOCALE_PATH));
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }

    public String getKey(String type) { return this.keys.getProperty(type).trim(); }

    public String getOne (String key) {
        return this.settings.get(key).toString();
    }

    public static String getLocaleString (String string) {
        return locale.getProperty(string, string);
    }

}
