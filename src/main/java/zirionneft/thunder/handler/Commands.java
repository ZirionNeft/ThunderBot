package zirionneft.thunder.handler;

import org.apache.log4j.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import zirionneft.thunder.BotUtils;
import zirionneft.thunder.Thunder;
import zirionneft.thunder.command.*;
import zirionneft.thunder.database.entity.Guild;
import zirionneft.thunder.database.entity.User;
import zirionneft.thunder.database.service.GuildService;
import zirionneft.thunder.database.service.UserService;
import zirionneft.thunder.handler.obj.CommandStamp;
import zirionneft.thunder.handler.obj.ICommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Commands {
    static private String PREFIX = Thunder.getSettingsInstance().getOne("thunder_chat_prefix");
    static Logger logger = Logger.getLogger("Commands.class");

    private static HashMap<String, ICommand> botCommands = new HashMap<>();

    static {
        botCommands.put("help", Help::run);
        botCommands.put("about", About::run);
        botCommands.put("info", About::run);
        botCommands.put("support", Support::run);
        botCommands.put("weather", Weather::run);
        botCommands.put("wr", Weather::run);
        botCommands.put("tr", Translate::run);
        botCommands.put("translate", Translate::run);
        botCommands.put("stats", Info::run);
        botCommands.put("set", Set::run);
        botCommands.put("credits", Coins::run);
        botCommands.put("coins", Coins::run);
    }

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {
        String command[] = event.getMessage().getContent().split(" ");
        IUser author = event.getAuthor();

        try {
            if (event.getAuthor().isBot())
                return;

            if (command.length == 0)
                return;

            if(isContainsInCommandStamps(event, author))
                return;

            List<String> argsList;
            String cmd;

            if (event.getMessage().getMentions().contains(Thunder.getClientInstance().getOurUser()) &&
                    !event.getMessage().mentionsEveryone() &&
                    !event.getMessage().mentionsHere()
            ) {
                if (command.length == 1 && event.getMessage().getMentions().size() == 1) {
                    botCommands.get("about").run(event, null);
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

            if (botCommands.containsKey(cmd)) {
                botCommands.get(cmd).run(event, argsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Boolean isContainsInCommandStamps(MessageReceivedEvent event, IUser author) {
        CommandStamp commandStamp = CommandStamp.getCommandStamp(author);

        if (commandStamp == null)
            return false;

        if (!event.getGuild().equals(commandStamp.getEvent().getGuild()))
            return false;

        switch (commandStamp.getState()) {
            case ACCEPT_ROLE_REMOVE:
                if (commandStamp.isValidCaptcha(event.getMessage().getContent().trim())) {
                    Guild guildEntity = GuildService.getGuild(event.getGuild().getLongID());
                    guildEntity.setManagerRoleId(null);
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_settings_successful", "Remove Role Manager");
                } else {
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_captcha_error");
                }
                break;

            case TRANSLATE:
                String msg = event.getMessage().toString();
                if (msg.equals("0")) {
                    BotUtils.sendLocaleMessage(event.getChannel(), "utils_translate_skip", author.getName());
                } else {
                    Translate.showTranslate(event, commandStamp.getData()[0].toString(), msg);
                }
                break;

            case COINS_TRANSFER:
                if (commandStamp.isValidCaptcha(event.getMessage().getContent().trim())) {
                    if (Coins.coinsTransaction(
                            author,
                            commandStamp.getEvent().getMessage().getMentions().get(0),
                            Integer.parseInt(commandStamp.getData()[0].toString())
                    )) {
                        User userEntity = UserService.getUser(author.getLongID());
                        BotUtils.sendLocaleMessage(event.getChannel(), "social_coins_transfer_success", userEntity.getCoins());
                    } else {
                        BotUtils.sendLocaleMessage(event.getChannel(), "social_coins_not_enough_error");
                    }
                } else {
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_captcha_error");
                }
                break;
        }
        CommandStamp.removeCommandStamp(author);

        return true;
    }
}
