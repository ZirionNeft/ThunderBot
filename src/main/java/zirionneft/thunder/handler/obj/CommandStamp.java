package zirionneft.thunder.handler.obj;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

import java.util.*;

public class CommandStamp {
    private static final int LIFETIME = 3; // Stamp lifetime in minutes

    private static ArrayList<String> emojiNumbers = new ArrayList<>();
    private static HashMap<IUser, CommandStamp> stampList = new HashMap<>();

    static {
        emojiNumbers.add(":zero:");
        emojiNumbers.add(":one:");
        emojiNumbers.add(":two:");
        emojiNumbers.add(":three:");
        emojiNumbers.add(":four:");
        emojiNumbers.add(":five:");
        emojiNumbers.add(":six:");
        emojiNumbers.add(":seven:");
        emojiNumbers.add(":eight:");
        emojiNumbers.add(":nine:");
    }

    private MessageReceivedEvent event;
    private CommandState state;
    private Object[] data;
    private Integer captcha;
    private Date date;

    public CommandStamp(MessageReceivedEvent event, CommandState state, Object[] data) {
        this.event = event;
        this.state = state;
        this.data = data;
        this.date = new Date();
        this.date.setTime(this.date.getTime() + (LIFETIME * 1000 * 60));
    }

    public CommandStamp(MessageReceivedEvent event, CommandState state) {
        this.event = event;
        this.state = state;
        this.date = new Date();
        this.date.setTime(this.date.getTime() + (LIFETIME * 1000 * 60));
    }

    public CommandStamp(MessageReceivedEvent event, CommandState state, int lifetime) {
        this.event = event;
        this.state = state;
        this.date = new Date();
        this.date.setTime(this.date.getTime() + (lifetime * 1000 * 60));
    }

    public CommandState getState() {
        return this.state;
    }

    public MessageReceivedEvent getEvent() {
        return this.event;
    }

    public Object[] getData() {
        return this.data;
    }

    public Date getDate() {
        return this.date;
    }

    public void generateCaptcha() {
        Random random = new Random();
        this.captcha = random.nextInt(88888) + 11111;
    }

    public String getFormattedCaptcha() {
        if (this.captcha != null) {
            StringBuilder result = new StringBuilder();
            this.captcha.toString().chars().forEach(x -> result.append(emojiNumbers.get(x-48)));
            return result.toString();
        }
        return null;
    }

    public boolean isValidCaptcha(String input) {
       return this.captcha.toString().equals(input);
    }

    public static CommandStamp getCommandStamp(IUser user) {
        return Optional.ofNullable(stampList.get(user)).orElseGet(() -> null);
    }

    public static void addCommandStamp(IUser user, CommandStamp stamp) {
        if (!stampList.containsKey(user)) {
            stampList.put(user, stamp);
        }
    }

    public static void removeCommandStamp(IUser user) {
        stampList.remove(user);
    }
}
