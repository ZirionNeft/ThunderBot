package com.thunderbot.zirionneft;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

import java.io.*;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

public class BotUtils {
    static Logger logger = Logger.getLogger("BotUtils.class");

    static IDiscordClient getBotDiscordClient(String token) {
        IDiscordClient client = null;
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token);
        clientBuilder.withRecommendedShardCount();

        try {
             client = clientBuilder.build();
        } catch (DiscordException e) {
            logger.error("Auth failed!");
            e.printStackTrace();
        }
        return client;
    }

    public static void sendMessage(IChannel channel, String message) {
        RequestBuffer.request(() -> {
           try {
               channel.sendMessage(message);
           } catch (DiscordException e) {
               e.printStackTrace();
           }
        });
    }

    public static void sendImage(IChannel channel, File image) {
        RequestBuffer.request(() -> {
            try {
                channel.sendFile(image);
            } catch (DiscordException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {}
        });
    }

    public static void sendLocaleMessage(IChannel channel, String message) {
        RequestBuffer.request(() -> {
            try {
                channel.sendMessage(Settings.getLocaleString(message));
            } catch (DiscordException e) {
                e.printStackTrace();
            }
        });
    }

    public static void sendLocaleMessage(IChannel channel, String message, Object... args) {
        RequestBuffer.request(() -> {
            try {
                channel.sendMessage(String.format(Settings.getLocaleString(message), args));
            } catch (DiscordException e) {
                e.printStackTrace();
            }
        });
    }

    public static void sendMessage(IChannel channel, EmbedObject embedObject) {
        RequestBuffer.request(() -> {
            try {
                channel.sendMessage(embedObject);
            } catch (DiscordException e) {
                e.printStackTrace();
            }
        });
    }

    public static void sendNSFWMessage(IChannel channel, String message) {
        RequestBuffer.request(() -> {
            try {
                if (channel.isNSFW())
                    channel.sendMessage(message);
                else
                    channel.sendMessage(Settings.getLocaleString("general_nsfw_content_error"));
            } catch (DiscordException e) {
                e.printStackTrace();
            }
        });
    }

    public static String rounding (double d, int precise) {
        StringBuilder stringBuilder = new StringBuilder("#.");

        for (int i = 0; i < precise; i++)
            stringBuilder.append("#");
        DecimalFormat df = new DecimalFormat(stringBuilder.toString());
        df.setRoundingMode(RoundingMode.CEILING);

        return df.format(d).replace(",", ".");
    }

    public static JSONObject HTTPQuery (String url) throws IOException,ParseException {
        JSONParser parser = new JSONParser();
        StringBuilder buffer = new StringBuilder();
        URL query = new URL(url);
        URLConnection connection = query.openConnection();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
            String input;
            while ((input = br.readLine()) != null) {
                buffer.append(input);
            }
        }
        return (JSONObject) parser.parse(buffer.toString());
    }

    @Deprecated
    public static String JSONSearch(JSONObject object, String valueOf) {
        String result = "";

        int currentPos = object.toString().indexOf(valueOf);
        char after = object.toString().charAt(currentPos+valueOf.length()+2);
        result += object.toString().substring(currentPos-1, currentPos+valueOf.length()+2);

        if (after == '{') {
            int s = 1;
            String b = object.toString().substring(currentPos+valueOf.length()+2);
            int i = 1;
            while (s != 0 && i < b.length()) {
                if (b.charAt(i) == '{') {
                    s++;
                } else if (b.charAt(i) == '}') {
                    s--;
                }
                i++;
            }
            return "{" + result + b.substring(0, i) + "}";
        } else if (after == '[') {
            int s = 1;
            String b = object.toString().substring(currentPos+valueOf.length()+2);
            int i = 1;
            while (s != 0 && i < b.length()) {
                if (b.charAt(i) == '[') {
                    s++;
                } else if (b.charAt(i) == ']') {
                    s--;
                }
                i++;
            }
            return "{" + result + b.substring(0, i) + "}";
        } else if (after == '"') {
            String b = object.toString().substring(currentPos+valueOf.length()+3);
            return "{" + result + "\"" + b.substring(0, b.indexOf("\"")) + "\"}";
        }
        return null;
    }
}
