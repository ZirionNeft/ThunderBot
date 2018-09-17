package thunder.command;

import org.apache.log4j.Logger;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import thunder.BotUtils;
import thunder.Database;
import thunder.handler.Commands;
import thunder.handler.obj.CommandStamp;
import thunder.handler.obj.CommandState;

import java.util.EnumSet;
import java.util.List;

public class Set {
    static Logger logger = Logger.getLogger("Set.java");

    public static void run(MessageReceivedEvent event, List<String> args) {
        IUser author = event.getAuthor();
        EnumSet<Permissions> userPerms = author.getPermissionsForGuild(event.getGuild());

        if (!userPerms.contains(Permissions.MANAGE_SERVER) &&
            !userPerms.contains(Permissions.ADMINISTRATOR) &&
            !Database.getGuildManagers(event.getGuild()).contains(author) &&
            !checkRole(author, event.getGuild())
        ) {
            BotUtils.sendMessage(event.getChannel(), ":disappointed_relieved: *Sorry, you don't have permissions for this command*");
            return;
        }

        try {
            if (args.isEmpty() || args.get(0).equals("help")) {
                help(event);
            }

            else if (args.get(0).equals("prefix")) {
                if (args.size() == 2) {
                    if (args.get(1).length() > 0 && args.get(1).length() < 5) {
                        if(Database.updateGuildPrefix(event.getGuild(), args.get(1))) {
                            Commands.updateGuildsPrefixes();
                            BotUtils.sendMessage(event.getChannel(), ":ballot_box_with_check: **Guild settings successful updated!**");
                        } else {
                            BotUtils.sendMessage(event.getChannel(), ":frowning2: Sorry, but something went wrong... Try later!");
                        }
                    } else {
                        BotUtils.sendMessage(event.getChannel(), ":information_source: Length of command prefix must be in range of 1 to 4 symbols");
                    }
                } else {
                    BotUtils.sendMessage(event.getChannel(), ":information_source: **Usage:** `set prefix 'prefix'`");
                }
            }

            else if (args.get(0).equals("role")) {
                if (args.size() == 2) {
                    List<IRole> roles = event.getMessage().getRoleMentions();

                    if (args.get(1).equals("0")) {
                        sendStatusMessage(event.getChannel(), Database.updateGuildManageRole(event.getGuild(), null));
                    } else if (!roles.isEmpty()) {
                        sendStatusMessage(event.getChannel(), Database.updateGuildManageRole(event.getGuild(), roles.get(0)));
                    } else if(!event.getGuild().getRolesByName(args.get(1)).isEmpty()) {
                        sendStatusMessage(event.getChannel(), Database.updateGuildManageRole(event.getGuild(), event.getGuild().getRolesByName(args.get(1)).get(0)));
                    } else {
                        BotUtils.sendMessage(event.getChannel(), ":question: Role not found");
                    }
                } else {
                    BotUtils.sendMessage(event.getChannel(), ":information_source: **Usage:** `set role 'role'`");
                }
            }

            else if (args.get(0).equals("manager")) {
                if (args.size() == 2) {
                    List<IUser> users = event.getMessage().getMentions();
                    if (!users.isEmpty()) {
                        sendStatusMessage(event.getChannel(), Database.updateGuildManager(event.getGuild(), users.get(0)));
                    } else if(!event.getGuild().getUsersByName(args.get(1)).isEmpty()) {
                        sendStatusMessage(event.getChannel(), Database.updateGuildManager(event.getGuild(), event.getGuild().getUsersByName(args.get(1)).get(0)));
                    } else {
                        BotUtils.sendMessage(event.getChannel(), ":question: User not found");
                    }
                } else {
                    BotUtils.sendMessage(event.getChannel(), ":information_source: **Usage:** `set manager 'name_or_mention'`");
                }
            }

            else if (args.get(0).equals("rmmanager")) {
                if (args.size() == 2) {
                    List<IUser> users = event.getMessage().getMentions();
                    if (!users.isEmpty()) {
                        if (!users.get(0).equals(author))
                            sendStatusMessage(event.getChannel(), Database.removeGuildManager(event.getGuild(), users.get(0)));
                        else {
                            CommandStamp commandStamp = new CommandStamp(event, CommandState.ACCEPT_REMOVE);
                            commandStamp.generateCaptcha();
                            Commands.addCommandStamp(author, commandStamp);

                            BotUtils.sendMessage(event.getChannel(), "**Please, accept the action: Enter numbers under this message.**\n"+commandStamp.getFormattedCaptcha());
                        }
                    } else if(!event.getGuild().getUsersByName(args.get(1)).isEmpty()) {
                        if (!event.getGuild().getUsersByName(args.get(1)).get(0).equals(author))
                            sendStatusMessage(event.getChannel(), Database.removeGuildManager(event.getGuild(), event.getGuild().getUsersByName(args.get(1)).get(0)));
                        else {
                            CommandStamp commandStamp = new CommandStamp(event, CommandState.ACCEPT_REMOVE);
                            commandStamp.generateCaptcha();
                            Commands.addCommandStamp(author, commandStamp);

                            BotUtils.sendMessage(event.getChannel(), "**Please, accept the action: Enter this numbers in next message.**\n"+commandStamp.getFormattedCaptcha());
                        }
                    } else {
                        BotUtils.sendMessage(event.getChannel(), ":question: User not found");
                    }
                } else {
                    BotUtils.sendMessage(event.getChannel(), ":information_source: **Usage:** `set rmmanager 'name_or_mention'`");
                }
            }

            else {
                help(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void help(MessageReceivedEvent event) {
        BotUtils.sendMessage(event.getChannel(), ":gear: **Settings commands** :gear:\n" +
                "`set role 'role'` - Change or set bot manager role; Specify 0 as a role to remove\n" +
                "`set prefix 'prefix'` - Change commands prefix\n" +
                "`set manager 'user'` - Add a user to the bot manager list\n" +
                "`set rmmanager 'user'` - Remove a user from bot manager list");
    }

    public static void remove(MessageReceivedEvent event) {
        sendStatusMessage(event.getChannel(), Database.removeGuildManager(event.getGuild(), event.getAuthor()));
    }

    private static void sendStatusMessage(IChannel channel, boolean status) {
        if(status) {
            BotUtils.sendMessage(channel, ":ballot_box_with_check: **Guild settings successful updated!**");
        } else {
            BotUtils.sendMessage(channel, ":frowning2: Sorry, but something went wrong... Try later!");
        }
    }

    private static boolean checkRole(IUser user, IGuild guild) {
        if (Database.getGuildManageRole(guild) == null)
            return false;
        return user.hasRole(Database.getGuildManageRole(guild));
    }
}
