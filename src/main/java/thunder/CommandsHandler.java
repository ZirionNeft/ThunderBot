package thunder;

import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sun.misc.Regexp;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import javax.xml.crypto.Data;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandsHandler {
    static String PREFIX = (String)Thunder.settings.getOne("thunder_chat_prefix");

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            String command[] = event.getMessage().getContent().split(" ");

            if (command.length == 0)
                return;

            if (!command[0].startsWith(PREFIX))
                return;

            String cmd = command[0].substring(1);

            List<String> argsList = new ArrayList<>(Arrays.asList(command));
            argsList.remove(0);

            switch (cmd) {
                case "help":
                    helpCommand(event);
                    break;
                case "about":
                case "info":
                    //infoCommand
                    break;
                case "weather":
                    weatherCommand(event, argsList);
                    break;
                case "tr":
                    translateCommand(event, argsList);
                    break;
                case "settings":
                    settingsCommand(event, argsList);
                    break;
                case "stats":
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
        ArrayList<String> row = Database.getWeatherRow(author);
        try {
            if (args.isEmpty()) {
                if (row == null) {
                    BotUtils.sendMessage(event.getChannel(), ":information_source: You are not provided details about your location.\n" +
                            "**How to:**```>weather set 'city' ['HH:mm']```");
                } else {
                    EmbedObject embedObject = BotUtils.embedWeatherBuilder(row);
                    BotUtils.sendMessage(event.getChannel(), embedObject);
                }
            }

            else if (args.get(0).equals("time")) {
                if (args.size() != 2) {
                    BotUtils.sendMessage(event.getChannel(), ":information_source: **Usage:** ```>weather time 'time'" +
                            "\n* time - Time to send to PM in format HH:mm; -1 to disable feature```");
                } else {
                    Matcher matcher = Pattern.compile("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$").matcher(args.get(1));
                    if (matcher.find() || args.get(1).equals("-1")) {
                        if (row != null) {
                            Database.updateWeatherRow(event.getAuthor(), "", args.get(1));
                            BotUtils.sendMessage(event.getChannel(), ":ballot_box_with_check: **Your weather settings successful updated!**");
                        } else {
                            BotUtils.sendMessage(event.getChannel(), ":information_source: You are not provided details about your location.\n" +
                                    "**How to:**```>weather set 'city' ['hh:mm']```");
                        }
                    } else {
                        BotUtils.sendMessage(event.getChannel(), ":information_source: **Usage:** ```>weather time 'time'" +
                                "\n* time - Time to send to PM in format HH:mm; -1 to disable feature```");
                    }
                }
            }

            else if (args.get(0).equals("set")) {
                if (args.size() != 2 && args.size() != 3) {
                    BotUtils.sendMessage(event.getChannel(), ":information_source: **Usage:** ```>weather set 'city' ['time']" +
                            "\n* city - City for which the weather is needed" +
                            "\n* time - (Optional) Time to send to PM in format hh:mm; -1 to disable feature```");
                } else if (args.size() == 3) {
                    Matcher matcher = Pattern.compile("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$").matcher(args.get(2));
                    if (matcher.find() || args.get(2).equals("-1")) {
                        if (row != null)
                            Database.updateWeatherRow(event.getAuthor(), args.get(1), args.get(2));
                        else
                            Database.addWeatherRow(event.getAuthor(), args.get(1), args.get(2));

                        BotUtils.sendMessage(event.getChannel(), ":ballot_box_with_check: **Your weather settings successful updated!**");
                    } else {
                        BotUtils.sendMessage(event.getChannel(), ":information_source: **Usage:** ```>weather set 'city' ['time']" +
                                "\n* city - City for which the weather is needed" +
                                "\n* time - (Optional) Time to send to PM in format HH:mm; -1 to disable feature```");
                    }

                } else {
                    if (row != null)
                        Database.updateWeatherRow(event.getAuthor(), args.get(1), "-1");
                    else
                        Database.addWeatherRow(event.getAuthor(), args.get(1), "-1");

                    BotUtils.sendMessage(event.getChannel(), ":ballot_box_with_check: **Your weather settings successful updated!**");
                }
            }

            else if (args.get(0).equals("help")) {
                BotUtils.sendMessage(event.getChannel(), ":zap: **Weather commands** :zap:\n" +
                        "```>weather```:arrow_up: Displays the weather for your settings\n" +
                        "```>weather set 'city' ['HH:mm']```:arrow_up: Sets the city and time of auto-display weather in PM\n" +
                        "```>weather time 'HH:mm'```:arrow_up: Change or set time of PM weather broadcast. -1 to disable feature" +
                        "```>weather 'city'```:arrow_up: Displays the weather in this city\n");
            }

            else if (args.size() == 1) {
                row.set(0, args.get(0));
                EmbedObject embedObject = BotUtils.embedWeatherBuilder(row);
                BotUtils.sendMessage(event.getChannel(), embedObject);
            }
        } catch (Exception e) {
            BotUtils.sendMessage(event.getChannel(), ":frowning2: Something went wrong...\nMaybe couldn't connect to the weather provider host ("+Thunder.settings.getOne("thunder_weather_service")+"), try later!");
            e.printStackTrace();
        }
    }

    private void settingsCommand(MessageReceivedEvent event, List<String> args) {

    }

    private void translateCommand(MessageReceivedEvent event, List<String> args) {
        IUser author = event.getAuthor();
        String service = Thunder.settings.getOne("thunder_translate_service").toString();

        try {
            if (args.isEmpty()) {
                BotUtils.sendMessage(event.getChannel(), ":information_source: **Usage:** ```>tr```");
            } else if (args.get(0).equals("info")) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
