package thunder.command;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import thunder.BotUtils;
import thunder.Thunder;
import thunder.handler.Events;

import javax.swing.text.html.parser.Parser;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Logger;

public class Translate {
    static Logger logger = Logger.getLogger("Translate.class");

    private static final String provider = Thunder.getSettingsInstance().getOne("thunder_translate_service");

    public static void run(MessageReceivedEvent event, List<String> args) {
        IUser author = event.getAuthor();
        String service = Thunder.getSettingsInstance().getOne("thunder_translate_service");

        try {
            if (args.isEmpty()) {
                BotUtils.sendMessage(event.getChannel(), ":information_source: **Usage:** ```>tr```");
            }

            else if (args.get(0).equals("list")) {
                if (args.size() != 2) {
                    BotUtils.sendMessage(event.getChannel(), ":information_source: **Usage:** ```>tr list 'lang'" +
                            "\n* lang - Language for which the list is needed```");
                } else {
                    JSONObject list = BotUtils.HTTPQuery("https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=" +
                            Thunder.getSettingsInstance().getApiKey("translate_key") +
                            "&ui=" + args.get(1));
                    if (list.get("code") == null) {
                        StringBuilder buffer = new StringBuilder("Supported languages for translate from **[" + args.get(1).toUpperCase() + "]**\n```");

                        ((JSONObject) list.get("langs")).forEach((key, value) -> {
                            buffer.append(value).append("[").append(key).append("], ");
                        });
                        buffer.append("```");

                        BotUtils.sendMessage(event.getChannel(), buffer.toString());
                    }
                }
            }

            else if (args.get(0).equals("help")) {
                BotUtils.sendMessage(event.getChannel(), ":zap: **Translate commands** :zap:\n" +
                        "```>tr list 'lang'```:arrow_up: Shows an available translation language list\n" +
                        "```>tr 'lang'```:arrow_up: Translation. Lang format: *en*(to) or *en-ru*(from-to)\n");
            }

            else if (args.size() == 1) {
                if (args.get(0).length() == 2 || (args.get(0).length() == 5 && args.get(0).charAt(2) == '-')) {
                    if (Events.setTranslateUserWatch(event.getAuthor(), args.get(0))) {
                        BotUtils.sendMessage(event.getChannel(), ":arrow_down: Enter text to be translated in the next message.");
                    } else {
                        BotUtils.sendMessage(event.getChannel(), ":warning: User with that name is already use this command!");
                    }
                } else {
                    BotUtils.sendMessage(event.getChannel(), "Wrong language code! Try to use help command ```>tr help```");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showTranslate(MessageReceivedEvent event, String lang, String msg){
        try {
            JSONObject trQuery = BotUtils.HTTPQuery("https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + Thunder.getSettingsInstance().getApiKey("translate_key")
                    + "&text=" + URLEncoder.encode(msg, "UTF-8") + "&lang=" + lang);

            String code = trQuery.get("code").toString();
            if (code.equals("200")) {
                String[] l = trQuery.get("lang").toString().split("-");

                String outMsg = "Original text from **[" + l[0].toUpperCase() + "]**: ```" + msg + "```\n";
                outMsg += "Translated to **[" + l[1].toUpperCase() + "]**: ```";
                outMsg += ((JSONArray)trQuery.get("text")).get(0) + "```\nTranslated by " + provider + " - https://translate.yandex.com/";

                BotUtils.sendMessage(event.getChannel(), outMsg);
            } else {
                logger.warning("Translate ERROR: code " + code + " - Visit API-provider to get more info");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Events.removeTranslateUserWatch(event.getAuthor());
            String eMsg = e.getMessage();
            int pos = eMsg.lastIndexOf("response code: ");
            if (pos != -1) {
                int code = Integer.parseInt(eMsg.substring(pos + "response code: ".length(), pos + "response code: ".length() + 3));
                switch (code) {
                    case 400:
                        BotUtils.sendMessage(event.getChannel(), "Wrong language code! Try to use help command ```>tr help```");
                        break;
                }
            } else {
                e.printStackTrace();
            }
        }
    }
}
