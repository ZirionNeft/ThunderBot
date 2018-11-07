package zirionneft.thunder.handler;

import org.apache.log4j.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import zirionneft.thunder.BotUtils;
import zirionneft.thunder.Database;
import zirionneft.thunder.Thunder;
import zirionneft.thunder.command.*;
import zirionneft.thunder.database.entity.Guild;
import zirionneft.thunder.database.service.GuildService;
import zirionneft.thunder.handler.obj.ICommand;
import zirionneft.thunder.handler.obj.CommandStamp;
import zirionneft.thunder.handler.obj.CommandState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import static zirionneft.thunder.handler.obj.CommandState.FREE;

public class Commands {
    static private String PREFIX = Thunder.getSettingsInstance().getOne("thunder_chat_prefix");
    static Logger logger = Logger.getLogger("Commands.class");

    private static HashMap<IUser, CommandStamp> stampList = new HashMap<>();
    private static HashMap<String, ICommand> commands = new HashMap<>();

    static {
        commands.put("help", Help::run);
        commands.put("about", About::run);
        commands.put("info", About::run);
        commands.put("support", Support::run);
        commands.put("weather", Weather::run);
        commands.put("wr", Weather::run);
        commands.put("tr", Translate::run);
        commands.put("translate", Translate::run);
        commands.put("stats", Info::run);
        commands.put("set", Set::run);
        commands.put("credits", Coins::run);
        commands.put("coins", Coins::run);
    }

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            String command[] = event.getMessage().getContent().split(" ");
            long guildId = event.getGuild().getLongID();
            IUser author = event.getAuthor();

            if (event.getAuthor().isBot())
                return;

            if (command.length == 0)
                return;

            if (stampList.containsKey(author)) {
                if (event.getGuild().equals(getStampEvent(author).getGuild())) {
                    switch (getStampState(author)) {
                        case ACCEPT_REMOVE:
                            if (stampList.get(author).isValidCaptcha(event.getMessage().getContent().trim())) {
                                Set.remove(event);
                            } else {
                                BotUtils.sendLocaleMessage(event.getChannel(), "general_captcha_error");
                            }
                            break;

                        case TRANSLATE:
                            String msg = event.getMessage().toString();
                            if (msg.equals("0")) {
                                BotUtils.sendLocaleMessage(event.getChannel(), "utils_translate_skip", author.getName());
                            } else {
                                Translate.showTranslate(event, stampList.get(author).getData()[0], msg);
                            }
                            break;

                        case COINS_TRANSFER:
                            if (stampList.get(author).isValidCaptcha(event.getMessage().getContent().trim())) {
                                if (Coins.transaction(
                                        author,
                                        stampList.get(author).getEvent().getMessage().getMentions().get(0),
                                        Integer.parseInt(stampList.get(author).getData()[0])
                                )) {
                                    BotUtils.sendLocaleMessage(event.getChannel(), "social_coins_transfer_success", Database.getUserCash(author));
                                } else {
                                    BotUtils.sendLocaleMessage(event.getChannel(), "social_coins_not_enough_error");
                                }
                            } else {
                                BotUtils.sendLocaleMessage(event.getChannel(), "general_captcha_error");
                            }
                            break;
                    }
                    stampList.remove(author);

                    return;
                }
            }

            List<String> argsList;
            String cmd;

            if (event.getMessage().getMentions().contains(Thunder.getClientInstance().getOurUser()) &&
                    !event.getMessage().mentionsEveryone() &&
                    !event.getMessage().mentionsHere()
            ) {
                if (command.length == 1 && event.getMessage().getMentions().size() == 1) {
                    commands.get("about").run(event, null);
                    return;
                }

                cmd = command[1];
                argsList = new ArrayList<>(Arrays.asList(command));
                argsList.remove(0);
            } else {
                Guild g = GuildService.getGuild(event.getGuild().getLongID());
                if (!g.getBotPrefix().isEmpty())
                    PREFIX = g.getBotPrefix();

                if (!command[0].startsWith(PREFIX))
                    return;

                cmd = command[0].substring(PREFIX.length());
                argsList = new ArrayList<>(Arrays.asList(command));
            }
            argsList.remove(0);

            if (commands.containsKey(cmd)) {
                commands.get(cmd).run(event, argsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static CommandState getStampState(IUser user) {
        if (stampList.containsKey(user))
            return stampList.get(user).getState();
        return FREE;
    }

    public static MessageReceivedEvent getStampEvent(IUser user) {
        return stampList.get(user).getEvent();
    }

    public static boolean addCommandStamp(IUser user, CommandStamp stamp) {
        if (!stampList.containsKey(user)) {
            stampList.put(user, stamp);
            return true;
        }
        return false;
    }

    public static void removeCommandStamp(ArrayList<IUser> users) {
        for (IUser user : users) {
            stampList.remove(user);
        }
    }

    public static HashMap<IUser, CommandStamp> getCommandStamps() {
        return stampList;
    }
}
