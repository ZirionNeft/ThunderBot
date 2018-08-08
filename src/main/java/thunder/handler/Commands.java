package thunder.handler;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import thunder.Command;
import thunder.Thunder;
import thunder.command.Help;
import thunder.command.Translate;
import thunder.command.Weather;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class Commands {
    static String PREFIX = (String)Thunder.getSettingsInstance().getOne("thunder_chat_prefix");
    static Logger logger = Logger.getLogger("Commands.class");

    private static HashMap<String, Command> commands = new HashMap<>();

    static {
        commands.put("help", Help::run);

        commands.put("about", Help::run);
        commands.put("info", Help::run);

        commands.put("weather", Weather::run);
        commands.put("wr", Weather::run);

        commands.put("tr", Translate::run);
        commands.put("translate", Translate::run);
    }

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

            if (commands.containsKey(cmd))
                commands.get(cmd).run(event, argsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
