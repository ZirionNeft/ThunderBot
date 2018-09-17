package thunder.handler;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import thunder.BotUtils;
import thunder.Database;
import thunder.Thunder;
import thunder.command.*;
import thunder.handler.obj.Command;
import thunder.handler.obj.CommandStamp;
import thunder.handler.obj.CommandState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static thunder.handler.obj.CommandState.FREE;

public class Commands {
    static private String PREFIX = Thunder.getSettingsInstance().getOne("thunder_chat_prefix");
    static private HashMap<Long, String> PREFIXES = Database.getGuildPrefixes();
    static Logger logger = Logger.getLogger("Commands.class");

    private static HashMap<IUser, CommandStamp> stampList = new HashMap<>();
    private static HashMap<String, Command> commands = new HashMap<>();

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
        commands.put("credits", Cash::run);
        commands.put("cash", Cash::run);
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
                                BotUtils.sendMessage(event.getChannel(), ":x: Wrong numbers, action has been aborted.");
                            }
                            stampList.remove(author);
                            break;
                        case TRANSLATE:
                            String msg = event.getMessage().toString();
                            if (msg.equals("0")) {
                                BotUtils.sendMessage(event.getChannel(), "*Translate command skipped for " + author.getName() + "*");
                            } else {
                                Translate.showTranslate(event, stampList.get(author).getData()[0], msg);
                            }
                            stampList.remove(author);
                            break;
                    }
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
                if (PREFIXES.containsKey(guildId))
                    PREFIX = PREFIXES.get(guildId);

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

    public static void removeCommandStamp(IUser user) {
        stampList.remove(user);
    }

    public static void removeCommandStamp(ArrayList<IUser> users) {
        for (IUser user : users) {
            stampList.remove(user);
        }
    }

    public static HashMap<IUser, CommandStamp> getCommandStamps() {
        return stampList;
    }

    public static void updateGuildsPrefixes() {
        PREFIXES = Database.getGuildPrefixes();
    }

    public static HashMap<Long, String> getGuildPrefixes() {
        return PREFIXES;
    }
}
