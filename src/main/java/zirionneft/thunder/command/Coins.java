package zirionneft.thunder.command;

import org.apache.log4j.Logger;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import zirionneft.thunder.BotUtils;
import zirionneft.thunder.Database;
import zirionneft.thunder.Settings;
import zirionneft.thunder.database.entity.User;
import zirionneft.thunder.database.service.UserService;
import zirionneft.thunder.handler.Commands;
import zirionneft.thunder.handler.obj.CommandStamp;
import zirionneft.thunder.handler.obj.CommandState;

import java.util.List;

public class Coins {
    static Logger logger = Logger.getLogger("Coins.java");
    private static final int MIN_TRANSFER_COINS = 30;

    public static void run(MessageReceivedEvent event, List<String> args) {
        IUser author = event.getAuthor();
        User authorEntity = UserService.getUser(author.getLongID());

        if (args.isEmpty()) {
            BotUtils.sendLocaleMessage(event.getChannel(), "social_coins_amount", authorEntity.getCoins());
        }

        else if (args.get(0).equals("help")) {
            help(event);
        }

        else if (args.size() == 2) {
            List<IUser> receiver = event.getMessage().getMentions();

            if (receiver.size() == 1) {
                if (!receiver.get(0).isBot()){
                    if (!receiver.get(0).equals(author)) {
                        try {
                            if (Integer.parseInt(args.get(1)) >= MIN_TRANSFER_COINS) {
                                String[] data = {args.get(1)};
                                CommandStamp commandStamp = new CommandStamp(event, CommandState.COINS_TRANSFER, data);
                                commandStamp.generateCaptcha();
                                Commands.addCommandStamp(author, commandStamp);

                                BotUtils.sendLocaleMessage(event.getChannel(), "general_captcha", commandStamp.getFormattedCaptcha());
                            } else {
                                BotUtils.sendLocaleMessage(event.getChannel(), "social_coins_transfer_min_amount_error", MIN_TRANSFER_COINS);
                            }
                        } catch (NumberFormatException e) {
                            BotUtils.sendLocaleMessage(event.getChannel(), "social_coins_transfer_amount_error");
                        }
                    } else {
                        BotUtils.sendLocaleMessage(event.getChannel(), "general_wrong_user_error");
                    }
                } else {
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_user_is_bot_error");
                }
            } else {
                BotUtils.sendLocaleMessage(event.getChannel(), "general_user_not_found_error", args.get(1));
            }

        } else
            help(event);
    }

    public static void help(MessageReceivedEvent event) {
        BotUtils.sendLocaleMessage(event.getChannel(), "social_coins_help_message");
    }


    public static boolean transaction(IUser sender, IUser receiver, int coins) {
        User senderEntity = UserService.getUser(sender.getLongID());
        User receiverEntity = UserService.getUser(receiver.getLongID());

        if (senderEntity.getCoins() >= coins) {
            senderEntity.setCoins(senderEntity.getCoins()-coins);
            receiverEntity.setCoins(receiverEntity.getCoins()+(coins*0.975));

            logger.info("TRANSACTION: Sender - " + sender.getName() + "; Receiver - " + receiver.getName() + "; Amount - " + coins);

            return true;
        }
        return false;
    }
}
