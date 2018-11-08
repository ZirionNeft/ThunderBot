package com.thunderbot.zirionneft.command;

import com.thunderbot.zirionneft.BotUtils;
import com.thunderbot.zirionneft.Thunder;
import com.thunderbot.zirionneft.handler.obj.CommandStamp;
import com.thunderbot.zirionneft.handler.obj.CommandState;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;


public class Translate {
    static Logger logger = Logger.getLogger("Translate.class");

    private static final String translateProvider = Thunder.getSettingsInstance().getOne("thunder_translate_service");

    @SuppressWarnings("unchecked")
    public static void run(MessageReceivedEvent event, List<String> args) {
        try {
            if (args.isEmpty()) {
                help(event);
            }

            else if (args.get(0).equals("list")) {
                if (args.size() != 2) {
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_command_usage" ,"`tr list 'lang'`");
                } else {
                    JSONObject list = BotUtils.HTTPQuery("https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=" +
                            Thunder.getSettingsInstance().getKey("translate_key") + "&ui=" + args.get(1));

                    if (list.get("code") == null) {
                        StringBuilder buffer = new StringBuilder();

                        ((JSONObject) list.get("langs")).forEach((key, value) -> {
                            buffer.append(value).append("[").append(key).append("], ");
                        });
                        BotUtils.sendLocaleMessage(event.getChannel(), "utils_translate_list", args.get(1).toUpperCase(), buffer.toString());
                    }
                }
            }

            else if (args.size() == 1) {
                if (args.get(0).length() == 2 || (args.get(0).length() == 5 && args.get(0).charAt(2) == '-')) {
                    CommandStamp commandStamp = new CommandStamp(
                            event,
                            CommandState.TRANSLATE,
                            new Object[]{args.get(0)}
                    );
                    CommandStamp.addCommandStamp(event.getAuthor(), commandStamp);
                    BotUtils.sendLocaleMessage(event.getChannel(), "utils_translate_tip");
                } else {
                    BotUtils.sendLocaleMessage(event.getChannel(), "utils_translate_wrong_code_error");
                }
            }

            else if (args.get(0).equals("help")) {
                help(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void help(MessageReceivedEvent event) {
        BotUtils.sendLocaleMessage(event.getChannel(), "utils_translate_help_message");
    }

    public static void showTranslate(MessageReceivedEvent event, String languageToTranslate, String messageToTranslate){
        try {
            JSONObject trQuery = BotUtils.HTTPQuery("https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + Thunder.getSettingsInstance().getKey("translate_key")
                    + "&text=" + URLEncoder.encode(messageToTranslate, "UTF-8") + "&lang=" + languageToTranslate);

            String code = trQuery.get("code").toString();
            if (code.equals("200")) {
                String[] languages = trQuery.get("lang").toString().split("-");
                String langFrom = languages[0].toUpperCase();
                String langTo = languages[1].toUpperCase();
                String translatedMessage = ((JSONArray)trQuery.get("text")).get(0).toString();

                BotUtils.sendLocaleMessage(
                        event.getChannel(),
                        "utils_translate_successful",
                        langFrom,
                        messageToTranslate,
                        langTo,
                        translatedMessage,
                        translateProvider
                );
            } else {
                logger.warn("Translate ERROR: code " + code + " - Visit API-provider site to get more info");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            String eMsg = e.getMessage();
            int pos = eMsg.lastIndexOf("response code: ");
            if (pos != -1) {
                int code = Integer.parseInt(eMsg.substring(pos + "response code: ".length(), pos + "response code: ".length() + 3));
                switch (code) {
                    case 400:
                        BotUtils.sendLocaleMessage(event.getChannel(), "utils_translate_response_error");
                        break;
                }
            } else {
                e.printStackTrace();
            }
        }
    }
}
