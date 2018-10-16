package zirionneft.thunder.command;

import org.apache.log4j.Logger;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import zirionneft.thunder.BotUtils;
import zirionneft.thunder.Database;
import zirionneft.thunder.Settings;
import zirionneft.thunder.handler.Commands;
import zirionneft.thunder.handler.obj.CommandStamp;
import zirionneft.thunder.handler.obj.CommandState;

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
            BotUtils.sendLocaleMessage(event.getChannel(), "general_user_permissions_error");
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
                            BotUtils.sendLocaleMessage(event.getChannel(), "general_settings_successful", "Prefix");
                        } else {
                            BotUtils.sendLocaleMessage(event.getChannel(), "general_unknown_error");
                        }
                    } else {
                        BotUtils.sendLocaleMessage(event.getChannel(), "manage_set_prefix_length_tip");
                    }
                } else {
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_command_usage", "`set prefix 'prefix'`");
                }
            }

            else if (args.get(0).equals("role")) {
                if (args.size() == 2) {
                    List<IRole> roles = event.getMessage().getRoleMentions();

                    if (args.get(1).equals("0")) {
                        sendStatusMessage(event.getChannel(), Database.updateGuildManageRole(event.getGuild(), null), "Role remove");
                    } else if (!roles.isEmpty()) {
                        sendStatusMessage(event.getChannel(), Database.updateGuildManageRole(event.getGuild(), roles.get(0)), "Manager role");
                    } else if(!event.getGuild().getRolesByName(args.get(1)).isEmpty()) {
                        sendStatusMessage(event.getChannel(), Database.updateGuildManageRole(event.getGuild(), event.getGuild().getRolesByName(args.get(1)).get(0)), "Manager role");
                    } else {
                        BotUtils.sendLocaleMessage(event.getChannel(), "general_role_not_found_error");
                    }
                } else {
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_command_usage", "`set role 'role'`");
                }
            }

            else if (args.get(0).equals("manager")) {
                if (args.size() == 2) {
                    List<IUser> users = event.getMessage().getMentions();
                    if (users.size() == 1) {
                        if (!users.get(0).isBot())
                            sendStatusMessage(event.getChannel(), Database.updateGuildManager(event.getGuild(), users.get(0)), "Manager user");
                        else
                            BotUtils.sendLocaleMessage(event.getChannel(), Settings.getLocaleString("general_user_is_bot_error"));
                    } else if(!event.getGuild().getUsersByName(args.get(1)).isEmpty()) {
                        if (!event.getGuild().getUsersByName(args.get(1)).get(0).isBot())
                            sendStatusMessage(event.getChannel(), Database.updateGuildManager(event.getGuild(), event.getGuild().getUsersByName(args.get(1)).get(0)), "Manager user");
                        else
                            BotUtils.sendLocaleMessage(event.getChannel(), "general_user_is_bot_error");
                    } else {
                        BotUtils.sendLocaleMessage(event.getChannel(), "general_role_not_found_error");
                    }
                } else {
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_command_usage", "`set manager 'name_or_mention'`");
                }
            }

            else if (args.get(0).equals("rmmanager")) {
                if (args.size() == 2) {
                    List<IUser> users = event.getMessage().getMentions();
                    if (users.size() == 1) {
                        if (!users.get(0).isBot()) {
                            CommandStamp commandStamp = new CommandStamp(event, CommandState.ACCEPT_REMOVE);
                            commandStamp.generateCaptcha();
                            Commands.addCommandStamp(author, commandStamp);

                            BotUtils.sendLocaleMessage(event.getChannel(), "general_captcha", commandStamp.getFormattedCaptcha());
                        } else {
                            BotUtils.sendLocaleMessage(event.getChannel(), "general_user_is_bot_error");
                        }
                    } else if(!event.getGuild().getUsersByName(args.get(1)).isEmpty()) {
                        IUser user = event.getGuild().getUsersByName(args.get(1)).get(0);
                        if (!user.isBot()) {
                            CommandStamp commandStamp = new CommandStamp(event, CommandState.ACCEPT_REMOVE);
                            commandStamp.generateCaptcha();
                            Commands.addCommandStamp(author, commandStamp);

                            BotUtils.sendLocaleMessage(event.getChannel(), "general_captcha", commandStamp.getFormattedCaptcha());
                        } else {
                            BotUtils.sendLocaleMessage(event.getChannel(), "general_user_is_bot_error");
                        }
                    } else {
                        BotUtils.sendLocaleMessage(event.getChannel(), "general_role_not_found_error");
                    }
                } else {
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_command_usage", "`set rmmanager 'name_or_mention'`");
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
        BotUtils.sendLocaleMessage(event.getChannel(), "manage_set_help_message");
    }

    public static void remove(MessageReceivedEvent event) {
        sendStatusMessage(event.getChannel(), Database.removeGuildManager(event.getGuild(), event.getAuthor()), "Remove user manager");
    }

    private static void sendStatusMessage(IChannel channel, boolean status, String type) {
        if(status) {
            BotUtils.sendLocaleMessage(channel, "general_settings_successful", type);
        } else {
            BotUtils.sendLocaleMessage(channel, "general_unknown_error");
        }
    }

    private static boolean checkRole(IUser user, IGuild guild) {
        if (Database.getGuildManageRole(guild) == null)
            return false;
        return user.hasRole(Database.getGuildManageRole(guild));
    }
}
