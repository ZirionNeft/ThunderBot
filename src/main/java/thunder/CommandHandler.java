package thunder;

import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sun.java2d.pipe.SpanShapeRenderer;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommandHandler {
    static String PREFIX = (String)Thunder.settings.getOne("thunder_chat_prefix");

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            String command[] = event.getMessage().getContent().split(" ");

            if (command.length == 0)
                return;

            if (!command[0].startsWith(PREFIX))
                return;

            //Command String
            String cmd = command[0].substring(1);

            // Arguments Array
            List<String> argsList = new ArrayList<>(Arrays.asList(command));
            argsList.remove(0);

            switch (cmd) {
                case "help":
                    helpCommand(event);
                    break;
                case "weather":
                    weatherCommand(event, argsList);
                    break;
                case "translate":
                    translateCommand(event, argsList);
                    break;
                case "settings":
                    settingsCommand(event, argsList);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void helpCommand(MessageReceivedEvent event) {
        BotUtils.sendMessage(event.getChannel(), ":zap: **Commands List** :zap:\n" +
                "```>weather help```:arrow_up: Weather command list\n" +
                "```>translate help```:arrow_up: Translate command list\n" +
                "```>settings```:arrow_up: Settings command list");
    }

    private void weatherCommand(MessageReceivedEvent event, List<String> args) {
        IUser author = event.getAuthor();
        List<String> row = Thunder.db.getWeatherRow(author);
        try {
            if (args.isEmpty()) {
                if (row == null) {
                    BotUtils.sendMessage(event.getChannel(), "You are not provided details about your location.\n" +
                            "Usage: ```>weather set **city** **hh:mm**```" +
                            " or type ```>weather help to more info```");
                } else {

                    String s = (String)Thunder.settings.getOne("thunder_weather_service");
                    JSONObject data;
                    if (s.equals("OpenWeatherMap")) {
                        data = BotUtils.HTTPQuery("http://api.openweathermap.org/data/2.5/weather?q=" + row.get(1) + "&appid=" + BotUtils.getToken("weather"));
                        JSONArray weather = (JSONArray) data.get("weather");
                        JSONObject wmain = (JSONObject)data.get("main");
                        JSONObject country = (JSONObject)data.get("sys");

                        EmbedBuilder builder = new EmbedBuilder();

                        builder.withColor(25, 160, 200);
                        builder.withAuthorName("Current weather in " + data.get("name") + "[" + country.get("country") + "]");

                        builder.appendField(":thermometer: Temperature", ":small_blue_diamond: Current:\t"+((double)wmain.get("temp")-273.15)+" °C :white_small_square: [ "+((double)wmain.get("temp_min")-273.15)+" °C / "+((double)wmain.get("temp_max")-273.15)+" °C ]\n"+
                                ":small_orange_diamond: Current:\t"+wmain.get("temp")+" °F :white_small_square: [ "+wmain.get("temp_min")+" °F / "+wmain.get("temp_max")+" °F ]", false);
                        builder.appendField(":umbrella: Humidity", ":small_blue_diamond: "+wmain.get("humidity")+" %", true);
                        builder.appendField(":control_knobs: Presure", ":small_blue_diamond: "+wmain.get("pressure")+" hPa", true);

                        SimpleDateFormat timeF = new SimpleDateFormat("HH:mm");
                        builder.withDescription(((JSONObject)weather.get(0)).get("main").toString()+" - "+((JSONObject)weather.get(0)).get("description").toString());
                        builder.appendField(":city_dusk: Sunrise", ":small_blue_diamond: "+timeF.format(new Date(((long)country.get("sunrise")*1000))), true);
                        builder.appendField(":night_with_stars: Sunset", ":small_blue_diamond: "+timeF.format(new Date(((long)country.get("sunset")*1000))), true);

                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM, HH:mm");
                        builder.withTitle(":calendar_spiral: " + sdf.format(new Date((long)data.get("dt")*1000)));
                        //builder.withImage("https://i.imgur.com/HOZy8gg.png");
                        builder.withFooterIcon(BotUtils.getWeatherIcon("01d"));
                        builder.withFooterText("Weather provided by OpenWeatherMap");
                        builder.withThumbnail(BotUtils.getWeatherIcon((String)((JSONObject)weather.get(0)).get("icon")));

                        BotUtils.sendMessage(event.getChannel(), event.getAuthor().mention(true));
                        BotUtils.sendMessage(event.getChannel(), builder.build());
                    }

                }
            } else if (args.get(0).equals("set")) {
                if (args.size() != 2) {
                    BotUtils.sendMessage(event.getChannel(), ":information_source: **Usage:** ```>weather set 'city'```");
                }
                else {
                    Thunder.db.addWeatherRow(event.getAuthor(), args.get(1), "00:00");
                    BotUtils.sendMessage(event.getChannel(), ":ballot_box_with_check: **Your weather settings successful updated!**");
                }
            } else if (args.get(0).equals("help")) {
                BotUtils.sendMessage(event.getChannel(), ":zap: **Weather commands** :zap:\n" +
                        "```>weather```:arrow_up: Displays the weather for your settings\n" +
                        "```>weather set 'city'```:arrow_up: Sets the city and time of auto-weather display\n" +
                        "```>weather 'city'```:arrow_up: Displays the weather in this city\n");
            } else if (args.size() == 1) {
                String s = (String)Thunder.settings.getOne("thunder_weather_service");
                JSONObject data;
                if (s.equals("OpenWeatherMap")) {
                    data = BotUtils.HTTPQuery("http://api.openweathermap.org/data/2.5/weather?q=" + args.get(0) + "&appid=" + BotUtils.getToken("weather"));
                    JSONArray weather = (JSONArray) data.get("weather");
                    JSONObject wmain = (JSONObject)data.get("main");
                    JSONObject country = (JSONObject)data.get("sys");

                    EmbedBuilder builder = new EmbedBuilder();

                    builder.withColor(25, 160, 200);
                    builder.withAuthorName("Current weather in " + data.get("name") + "[" + country.get("country") + "]");

                    builder.appendField(":thermometer: Temperature", ":small_blue_diamond: Current:\t"+((double)wmain.get("temp")-273.15)+" °C :white_small_square: [ "+((double)wmain.get("temp_min")-273.15)+" °C / "+((double)wmain.get("temp_max")-273.15)+" °C ]\n"+
                            ":small_orange_diamond: Current:\t"+wmain.get("temp")+" °F :white_small_square: [ "+wmain.get("temp_min")+" °F / "+wmain.get("temp_max")+" °F ]", false);
                    builder.appendField(":umbrella: Humidity", ":small_blue_diamond: "+wmain.get("humidity")+" %", true);
                    builder.appendField(":control_knobs: Presure", ":small_blue_diamond: "+wmain.get("pressure")+" hPa", true);

                    SimpleDateFormat timeF = new SimpleDateFormat("HH:mm");
                    builder.withDescription(((JSONObject)weather.get(0)).get("main").toString()+" - "+((JSONObject)weather.get(0)).get("description").toString());
                    builder.appendField(":city_dusk: Sunrise", ":small_blue_diamond: "+timeF.format(new Date(((long)country.get("sunrise")*1000))), true);
                    builder.appendField(":night_with_stars: Sunset", ":small_blue_diamond: "+timeF.format(new Date(((long)country.get("sunset")*1000))), true);

                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM, HH:mm");
                    builder.withTitle(":calendar_spiral: " + sdf.format(new Date((long)data.get("dt")*1000)));
                    //builder.withImage("https://i.imgur.com/HOZy8gg.png");
                    builder.withFooterIcon(BotUtils.getWeatherIcon("01d"));
                    builder.withFooterText("Weather provided by OpenWeatherMap");
                    builder.withThumbnail(BotUtils.getWeatherIcon((String)((JSONObject)weather.get(0)).get("icon")));

                    BotUtils.sendMessage(event.getChannel(), builder.build());
                }
            }
        } catch (Exception e) {
            BotUtils.sendMessage(event.getChannel(), ":frowning2: Couldn't connect to the weather provider host ("+Thunder.settings.getOne("thunder_weather_service")+"), try later!");
            e.printStackTrace();
        }
    }

    private void settingsCommand(MessageReceivedEvent event, List<String> args) {

    }

    private void translateCommand(MessageReceivedEvent event, List<String> args) {

    }
}
