package thunder.handler;

import org.json.simple.parser.ParseException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import thunder.BotUtils;
import thunder.Database;
import thunder.Thunder;
import thunder.command.Translate;
import thunder.command.Weather;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class Events {
    private int presenceQueue = 0;
    static Logger logger = Logger.getLogger("Events.class");

    private static HashMap<IUser, String> watchList = new HashMap<>();

    @EventSubscriber
    public void onThunderReady(ReadyEvent event) {
        Timer presenceTimer = new Timer();
        Timer weatherSendingTimer = new Timer();

        ArrayList<String> phrases = new ArrayList<String>();
        phrases.add("Mention me!");
        phrases.add(Thunder.getClientInstance().getGuilds().size() + " Guilds");
        phrases.add("https://thunder-bot.com");

        presenceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Thunder.getClientInstance().changePresence(StatusType.ONLINE, ActivityType.PLAYING, phrases.get(presenceQueue));

                presenceQueue++;
                if (presenceQueue >= phrases.size())
                    presenceQueue = 0;
            }
        }, 500, 5*60*1000);

        weatherSendingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimeZone tz = TimeZone.getTimeZone("UTC");
                Calendar cal = Calendar.getInstance(tz);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                sdf.setTimeZone(tz);
                String currentTime = sdf.format(cal.getTime());

                ArrayList<IUser> usersList = Database.getBroadcastMatchList(currentTime);
                for (IUser user : usersList) {
                    try {
                        IPrivateChannel PMChannel = Thunder.getClientInstance().getOrCreatePMChannel(user);
                        ArrayList<String> row = Database.getWeatherRow(user);
                        EmbedObject embedObject = Weather.embedWeatherBuilder(row);
                        BotUtils.sendMessage(PMChannel, embedObject);
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, 60*1000);
    }

    @EventSubscriber
    public void onTranslateMessageReceived(MessageReceivedEvent event) {
        IUser author = event.getAuthor();
        if (watchList.containsKey(author)) {
            String msg = event.getMessage().toString();
            if (msg.contains(Commands.getGuildPrefixes().get(event.getGuild().getLongID()) + "tr"))
                return;
            if (msg.equals("0")) {
                watchList.remove(author);
                BotUtils.sendMessage(event.getChannel(),  "*Translate command skipped for " + author.getName() + "*");
                return;
            }
            String lang = watchList.get(author);
            Translate.showTranslate(event, lang, msg);
            watchList.remove(author);
        }
    }

    @EventSubscriber
    public void onNewGuild(GuildCreateEvent event) {
        Database.insertGuildConfigRow(event.getGuild());
    }

    public static boolean setTranslateUserWatch(IUser user, String lang) {
        if (watchList.containsKey(user))
            return false;
        watchList.put(user, lang);
        return true;
    }

    public static boolean removeTranslateUserWatch(IUser user) {
        if (watchList.containsKey(user)) {
            watchList.remove(user);
            return true;
        }
        return false;
    }
}
