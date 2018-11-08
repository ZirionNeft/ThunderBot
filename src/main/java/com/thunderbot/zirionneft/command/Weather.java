package com.thunderbot.zirionneft.command;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import com.thunderbot.zirionneft.BotUtils;
import com.thunderbot.zirionneft.Thunder;
import com.thunderbot.zirionneft.database.entity.User;
import com.thunderbot.zirionneft.database.service.UserService;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Weather {
    static Logger logger = Logger.getLogger("Weather.class");

    public static void run(MessageReceivedEvent event, List<String> args) {
        IUser author = event.getAuthor();
        User authorEntity = UserService.getUser(author.getLongID());
        String authorCity = authorEntity.getCity();
        String authorBroadcastTime = authorEntity.getBroadcastTime();

        try {
            if (args.isEmpty()) {
                if (!authorEntity.getCity().isEmpty()) {
                    EmbedObject embedObject = embedWeatherBuilder(authorCity, authorBroadcastTime);
                    BotUtils.sendMessage(event.getChannel(), embedObject);
                } else {
                    BotUtils.sendLocaleMessage(event.getChannel(), "utils_weather_details_not_provided_error");
                }
            }

            else if (args.get(0).equals("time")) {
                if (args.size() == 2) {
                    Matcher matcher = Pattern.compile("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$").matcher(args.get(1));

                    if (matcher.find() || args.get(1).equals("-1")) {
                        authorEntity.setBroadcastTime(args.get(1));
                        UserService.updateUser(authorEntity);

                        BotUtils.sendLocaleMessage(event.getChannel(), "general_settings_successful","Change PM broadcast time");
                    } else {
                        BotUtils.sendLocaleMessage(event.getChannel(), "general_command_usage","`weather time 'time'`");
                    }
                } else
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_command_usage","`weather time 'time'`");
            }

            else if (args.get(0).equals("set")) {

                if (args.size() == 2 || args.size() == 3) {
                    authorEntity.setCity(args.get(1));
                    if (args.size() == 3) {
                        Matcher matcher = Pattern.compile("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$").matcher(args.get(2));
                        if (matcher.find() || args.get(2).equals("-1"))
                            authorEntity.setBroadcastTime(args.get(2));
                    }
                    UserService.updateUser(authorEntity);

                    BotUtils.sendLocaleMessage(event.getChannel(), "general_settings_successful","Weather");
                } else
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_command_usage", "`weather set 'city' [time(UTC)]`");
            }

            else if (args.get(0).equals("help")) {
                help(event);
            }

            else if (args.size() == 1) {
                EmbedObject embedObject = embedWeatherBuilder(args.get(0), authorBroadcastTime);
                BotUtils.sendMessage(event.getChannel(), embedObject);
            }
        } catch (Exception e) {
            BotUtils.sendLocaleMessage(event.getChannel(), "general_unknown_error");
            e.printStackTrace();
        }
    }

    public static void help(MessageReceivedEvent event) {
        BotUtils.sendLocaleMessage(event.getChannel(), "utils_weather_help_message");
    }

    public static EmbedObject embedWeatherBuilder(String city, String broadcastTime) throws IOException,ParseException {
        String weatherService = Thunder.getSettingsInstance().getOne("thunder_weather_service");
        JSONObject data;
        EmbedBuilder builder = new EmbedBuilder();
        
        if (weatherService.equals("OpenWeatherMap")) {
            data = BotUtils.HTTPQuery("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + Thunder.getSettingsInstance().getKey("weather_key"));
            JSONArray weather = (JSONArray) data.get("weather");
            JSONObject wmain = (JSONObject) data.get("main");
            JSONObject country = (JSONObject) data.get("sys");
            

            builder.withColor(25, 160, 200);
            builder.withAuthorName("Current weather in " + data.get("name") + "[" + country.get("country") + "]");

            builder.appendField(":thermometer: Temperature", ":small_blue_diamond: Current:\t" + convertTemperature((double) wmain.get("temp"), "C") + " °C :white_small_square: [ " + convertTemperature((double) wmain.get("temp_min"), "C") + " °C / " + convertTemperature((double) wmain.get("temp_max"),"C") + " °C ]\n" +
                    ":small_orange_diamond: Current:\t" + convertTemperature((double) wmain.get("temp"), "F") + " °F :white_small_square: [ " + convertTemperature((double) wmain.get("temp_min"), "F") + " °F / " + convertTemperature((double) wmain.get("temp_max"), "F") + " °F ]", false);
            builder.appendField(":umbrella: Humidity", ":small_blue_diamond: " + wmain.get("humidity") + " %", true);
            builder.appendField(":control_knobs: Presure", ":small_blue_diamond: " + wmain.get("pressure") + " hPa", true);

            SimpleDateFormat timeF = new SimpleDateFormat("HH:mm");
            builder.withDescription(((JSONObject) weather.get(0)).get("main").toString() + " - " + ((JSONObject) weather.get(0)).get("description").toString());
            builder.appendField(":night_with_stars: Sunset", ":small_blue_diamond: " + timeF.format(new Date(((long) country.get("sunset") * 1000))), true);
            builder.appendField(":city_dusk: Sunrise", ":small_blue_diamond: " + timeF.format(new Date(((long) country.get("sunrise") * 1000))), true);

            builder.appendDesc("\n\n*Weather auto-sending in PM:* " + ((broadcastTime.equals("-1"))?("**Disabled**"):("**"+broadcastTime+" (UTC)**")));

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM, HH:mm");
            builder.withTitle(":calendar_spiral: " + sdf.format(new Date((long) data.get("dt") * 1000)));
            builder.withFooterIcon(getWeatherIcon("01d"));
            builder.withFooterText("Weather provided by OpenWeatherMap");
            builder.withThumbnail(getWeatherIcon((String) ((JSONObject) weather.get(0)).get("icon")));
        }
        return builder.build();
    }

    private static String getWeatherIcon (String icon) {
        return "http://openweathermap.org/img/w/" + icon +".png";
    }

    private static String convertTemperature(double T, String degreesType) {
        if (degreesType.equals("C")) {
            return BotUtils.rounding(T - 273.15, 1);
        } else if (degreesType.equals("F")) {
            return BotUtils.rounding(T * 1.8 - 459.67, 1);
        }
        return "error ";
    }
}
