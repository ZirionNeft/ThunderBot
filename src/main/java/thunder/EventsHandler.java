package thunder;

import org.json.simple.parser.ParseException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.*;


import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

public class EventsHandler {
    private static IDiscordClient client = Thunder.client;
    private int presenceQueue = 0;
    static Logger logger = Logger.getLogger("EventsHandler.class");

    @EventSubscriber
    public void onThunderReady(ReadyEvent event) {
        Timer presenceTimer = new Timer();
        Timer weatherSendingTimer = new Timer();

        ArrayList<String> phrases = new ArrayList<String>();
        phrases.add("Use " + Thunder.settings.getOne("thunder_chat_prefix") + "help");
        phrases.add(client.getGuilds().size() + " Guilds");
        phrases.add("https://thunder-bot.com");

        presenceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                client.changePresence(StatusType.ONLINE, ActivityType.PLAYING, phrases.get(presenceQueue));

                presenceQueue++;
                if (presenceQueue >= phrases.size())
                    presenceQueue = 0;
            }
        }, 500, 5*60*1000);

        weatherSendingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String currentTime = sdf.format(cal.getTime());

                logger.info("Check time - Current: " + currentTime);

                ArrayList<IUser> usersList = Database.getBroadcastMatchList(currentTime);
                for (IUser user : usersList) {
                    try {
                        IPrivateChannel PMChannel = Thunder.client.getOrCreatePMChannel(user);
                        ArrayList<String> row = Database.getWeatherRow(user);
                        EmbedObject embedObject = BotUtils.embedWeatherBuilder(row);
                        BotUtils.sendMessage(PMChannel, embedObject);
                    } catch (IOException | ParseException e) {
                        /*BotUtils.sendMessage(PM, ":frowning2: Something went wrong..." +
                            "\nMaybe couldn't connect to the weather provider host (" + Thunder.settings.getOne("thunder_weather_service") + ")." +
                            "\n*Type **>weather time -1** to disable weather PM-broadcast*");*/
                        e.printStackTrace();
                    }
                }
            }
        }, 0, 60*1000);


        List<IGuild> guilds = client.getGuilds();
        /*guilds.forEach(guild -> {
            List<IRole> role = guild.getRolesByName("Thunder");
            if (role.isEmpty()) {
                IRole tRole = guild.createRole();
                tRole.changeHoist(false);
                tRole.changeName("Thunder");
                tRole.changeColor(new Color(50,150,200));

                guild.getUserByID(client.getOurUser().getLongID()).addRole(tRole);
            }
        });*/

    }
}
