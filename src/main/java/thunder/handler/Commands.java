package thunder.handler;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import thunder.BotUtils;
import thunder.Command;
import thunder.Database;
import thunder.Thunder;
import thunder.command.*;
import thunder.command.Set;

import java.util.*;
import java.util.logging.Logger;

public class Commands {
    static private String PREFIX = Thunder.getSettingsInstance().getOne("thunder_chat_prefix");
    static private HashMap<Long, String> PREFIXES = Database.getGuildPrefixes();
    static Logger logger = Logger.getLogger("Commands.class");

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
        commands.put("stats", Stats::run);
        commands.put("set", Set::run);
    }

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            String command[] = event.getMessage().getContent().split(" ");
            long guildId = event.getGuild().getLongID();

            if (event.getAuthor().isBot())
                return;

            if (command.length == 0)
                return;

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

    public static void updateGuildsPrefixes() {
        PREFIXES = Database.getGuildPrefixes();
    }

    public static HashMap<Long, String> getGuildPrefixes() {
        return PREFIXES;
    }
}
