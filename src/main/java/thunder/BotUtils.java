package thunder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

public class BotUtils {
    static IDiscordClient getBuiltDiscrodClient(String token) {
        IDiscordClient client = null;
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token);

        try {
             client = clientBuilder.build();
        } catch (DiscordException e) {
            System.out.println("ERROR: Auth error!");
            e.printStackTrace();
        }
        return client;
    }

    static void sendMessage(IChannel channel, String message) {
        RequestBuffer.request(() -> {
           try {
               channel.sendMessage(message);
           } catch (DiscordException e) {
               e.printStackTrace();
           }
        });
    }

    static void sendMessage(IChannel channel, EmbedObject embedObject) {
        RequestBuffer.request(() -> {
            try {
                channel.sendMessage(embedObject);
            } catch (DiscordException e) {
                e.printStackTrace();
            }
        });
    }

    static void JSONWeatherParser () {

    }

    static String getWeatherIcon (String icon) {
        return "http://openweathermap.org/img/w/" + icon +".png";
    }

    static JSONObject HTTPQuery (String url) throws IOException,ParseException {
        JSONParser parser = new JSONParser();
        StringBuilder buffer = new StringBuilder();
        URL query = new URL(url);
        URLConnection connection = query.openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        try {
            String input;
            while ((input = br.readLine()) != null) {
                buffer.append(input);
            }
        } finally {
            br.close();
        }
        return (JSONObject) parser.parse(buffer.toString());
    }

    static String JSONObjectSearch (JSONObject object, String target) {
        for(Object fieldName : object.keySet()) {
            if (object.get(fieldName) instanceof JSONArray) {
                JSONArray array = (JSONArray) object.get(fieldName);
                // soon......
            }
        }
        return null;
    }

    static String getToken (String type) {
        String buffer = "";

        try {
            if (type.equals("weather")) {
                if (new File("weather_api_key").exists()) {
                    FileReader file = new FileReader("weather_api_key");
                    int b;
                    while ((b = file.read()) != -1) {
                        buffer += (char) b;
                    }
                } else {
                    System.out.println("ERROR: Weather API key file not found! File name: weather_api_key");
                    System.exit(0);
                }
            } else if (type.equals("discord")) {
                if (new File("discord_bot_key").exists()) {
                    FileReader file = new FileReader("discord_bot_key");
                    int b;
                    while ((b = file.read()) != -1) {
                        buffer += (char) b;
                    }
                } else {
                    System.out.println("ERROR: Discord Token file not found! File name: discord_bot_key");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
}
