package thunder;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

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

    static EmbedObject embedWeatherBuilder(List<String> row) throws IOException,ParseException{
        EmbedObject result = null;

        String s = Thunder.settings.getOne("thunder_weather_service");
        JSONObject data;
        if (s.equals("OpenWeatherMap")) {
            data = Misc.HTTPQuery("http://api.openweathermap.org/data/2.5/weather?q=" + row.get(0) + "&appid=" + Thunder.settings.getApiKey("weather_key"));
            JSONArray weather = (JSONArray) data.get("weather");
            JSONObject wmain = (JSONObject) data.get("main");
            JSONObject country = (JSONObject) data.get("sys");

            String C = Misc.rounding((double) wmain.get("temp") - 273.15, 1);
            String F = Misc.rounding((double) wmain.get("temp") * 1.8 - 459.67, 1);

            EmbedBuilder builder = new EmbedBuilder();

            builder.withColor(25, 160, 200);
            builder.withAuthorName("Current weather in " + data.get("name") + "[" + country.get("country") + "]");

            builder.appendField(":thermometer: Temperature", ":small_blue_diamond: Current:\t" + Misc.calculateTemperature((double) wmain.get("temp"), "C") + " °C :white_small_square: [ " + Misc.calculateTemperature((double) wmain.get("temp_min"), "C") + " °C / " + Misc.calculateTemperature((double) wmain.get("temp_max"),"C") + " °C ]\n" +
                    ":small_orange_diamond: Current:\t" + Misc.calculateTemperature((double) wmain.get("temp"), "F") + " °F :white_small_square: [ " + Misc.calculateTemperature((double) wmain.get("temp_min"), "F") + " °F / " + Misc.calculateTemperature((double) wmain.get("temp_max"), "F") + " °F ]", false);
            builder.appendField(":umbrella: Humidity", ":small_blue_diamond: " + wmain.get("humidity") + " %", true);
            builder.appendField(":control_knobs: Presure", ":small_blue_diamond: " + wmain.get("pressure") + " hPa", true);

            SimpleDateFormat timeF = new SimpleDateFormat("HH:mm");
            builder.withDescription(((JSONObject) weather.get(0)).get("main").toString() + " - " + ((JSONObject) weather.get(0)).get("description").toString());
            builder.appendField(":night_with_stars: Sunset", ":small_blue_diamond: " + timeF.format(new Date(((long) country.get("sunset") * 1000))), true);
            builder.appendField(":city_dusk: Sunrise", ":small_blue_diamond: " + timeF.format(new Date(((long) country.get("sunrise") * 1000))), true);

            builder.appendDesc("\n\n*Weather auto-sending in PM:* " + ((row.get(1).equals("-1"))?("**Disabled**"):("**"+row.get(1)+"**")));

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM, HH:mm");
            builder.withTitle(":calendar_spiral: " + sdf.format(new Date((long) data.get("dt") * 1000)));
            builder.withFooterIcon(Misc.getWeatherIcon("01d"));
            builder.withFooterText("Weather provided by OpenWeatherMap");
            builder.withThumbnail(Misc.getWeatherIcon((String) ((JSONObject) weather.get(0)).get("icon")));

            result = builder.build();
        }
        return result;
    }
}
