package zirionneft.thunder.command;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import zirionneft.thunder.BotUtils;
import zirionneft.thunder.Database;
import zirionneft.thunder.Thunder;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Weather {
    static Logger logger = Logger.getLogger("Weather.class");

    public static void run(MessageReceivedEvent event, List<String> args) {
        IUser author = event.getAuthor();
        ArrayList<String> row = Database.getWeatherRow(author);

        try {
            if (args.isEmpty()) {
                if (row == null) {
                    BotUtils.sendLocaleMessage(event.getChannel(), "utils_weather_details_not_provided_error");
                } else {
                    EmbedObject embedObject = embedWeatherBuilder(row);
                    BotUtils.sendMessage(event.getChannel(), embedObject);
                }
            }

            else if (args.get(0).equals("time")) {
                if (args.size() != 2) {
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_command_usage","`weather time 'time'`");
                } else {
                    Matcher matcher = Pattern.compile("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$").matcher(args.get(1));
                    if (matcher.find() || args.get(1).equals("-1")) {
                        if (row != null) {
                            Database.updateWeatherRow(event.getAuthor(), args.get(1));
                            BotUtils.sendLocaleMessage(event.getChannel(), "general_settings_successful","PM broadcast time");
                        } else {
                            BotUtils.sendLocaleMessage(event.getChannel(), "utils_weather_details_not_provided_error");
                        }
                    } else {
                        BotUtils.sendLocaleMessage(event.getChannel(), "general_command_usage","`weather time 'time'`");
                    }
                }
            }

            else if (args.get(0).equals("set")) {
                if (args.size() != 2 && args.size() != 3) {
                    BotUtils.sendMessage(event.getChannel(), ":information_source: **Usage:** `weather set 'city' ['time']`" +
                            "\n* city - City for which the weather is needed" +
                            "\n* time - (Optional) Time(UTC) to send to PM in format hh:mm; -1 to disable feature`");
                } else if (args.size() == 3) {
                    Matcher matcher = Pattern.compile("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$").matcher(args.get(2));
                    if (matcher.find() || args.get(2).equals("-1")) {
                        if (row != null) {
                            Database.updateWeatherRow(event.getAuthor(), args.get(1), args.get(2));
                        } else {
                            Database.addWeatherRow(event.getAuthor(), args.get(1), args.get(2));
                        }

                        BotUtils.sendMessage(event.getChannel(), ":ballot_box_with_check: Your weather settings successful updated!");
                    } else {
                        BotUtils.sendMessage(event.getChannel(), ":information_source: **Usage:** `weather set 'city' ['time']`" +
                                "\n* city - City for which the weather is needed" +
                                "\n* time - (Optional) Time(UTC) to send to PM in format HH:mm; -1 to disable feature");
                    }

                } else {
                    if (row != null)
                        Database.updateWeatherRow(event.getAuthor(), args.get(1), "-1");
                    else
                        Database.addWeatherRow(event.getAuthor(), args.get(1), "-1");

                    BotUtils.sendMessage(event.getChannel(), ":ballot_box_with_check: Your weather settings successful updated!");
                }
            }

            else if (args.get(0).equals("help")) {
                help(event);
            }

            else if (args.size() == 1) {
                row.set(0, args.get(0));
                EmbedObject embedObject = embedWeatherBuilder(row);
                BotUtils.sendMessage(event.getChannel(), embedObject);
            }
        } catch (Exception e) {
            BotUtils.sendMessage(event.getChannel(), ":frowning2: Something went wrong...\nMaybe couldn't connect to the weather provider host ("+Thunder.getSettingsInstance().getOne("thunder_weather_service")+"), try later!");
            e.printStackTrace();
        }
    }

    public static void help(MessageReceivedEvent event) {
        BotUtils.sendMessage(event.getChannel(), ":zap: **Weather commands** :zap:\n" +
                "`weather` - Displays the weather by your city\n" +
                "`weather set 'city' ['HH:mm']` - Sets the city and time(UTC) of auto-display weather in PM\n" +
                "`weather time 'HH:mm'` - Change or set time(UTC) of PM weather broadcast. -1 to disable feature\n" +
                "`weather 'city'` - Displays the weather in this city\n");
    }

    public static EmbedObject embedWeatherBuilder(List<String> row) throws IOException,ParseException {
        EmbedObject result = null;

        String s = Thunder.getSettingsInstance().getOne("thunder_weather_service");
        JSONObject data;
        if (s.equals("OpenWeatherMap")) {
            data = BotUtils.HTTPQuery("http://api.openweathermap.org/data/2.5/weather?q=" + row.get(0) + "&appid=" + Thunder.getSettingsInstance().getKey("weather_key"));
            JSONArray weather = (JSONArray) data.get("weather");
            JSONObject wmain = (JSONObject) data.get("main");
            JSONObject country = (JSONObject) data.get("sys");

            EmbedBuilder builder = new EmbedBuilder();

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

            builder.appendDesc("\n\n*Weather auto-sending in PM:* " + ((row.get(1).equals("-1"))?("**Disabled**"):("**"+row.get(1)+" (UTC)**")));

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM, HH:mm");
            builder.withTitle(":calendar_spiral: " + sdf.format(new Date((long) data.get("dt") * 1000)));
            builder.withFooterIcon(getWeatherIcon("01d"));
            builder.withFooterText("Weather provided by OpenWeatherMap");
            builder.withThumbnail(getWeatherIcon((String) ((JSONObject) weather.get(0)).get("icon")));

            result = builder.build();
        }
        return result;
    }

    private static String getWeatherIcon (String icon) {
        return "http://openweathermap.org/img/w/" + icon +".png";
    }

    private static String convertTemperature(double T, String type) {
        if (type.equals("C")) {
            return BotUtils.rounding(T - 273.15, 1);
        } else if (type.equals("F")) {
            return BotUtils.rounding(T * 1.8 - 459.67, 1);
        }
        return null;
    }
}
