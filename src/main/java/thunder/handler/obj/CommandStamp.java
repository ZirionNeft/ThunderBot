package thunder.handler.obj;

import com.sun.istack.internal.NotNull;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import thunder.BotUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CommandStamp {
    private static ArrayList<String> emojiNumbers = new ArrayList<>();
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
    private String[] data;
    private Integer captcha;

    public CommandStamp(MessageReceivedEvent event, CommandState state, String[] data) {
        this.event = event;
        this.state = state;
        this.data = data.clone();
    }

    public CommandStamp(MessageReceivedEvent event, CommandState state) {
        this.event = event;
        this.state = state;
    }

    public CommandState getState() {
        return this.state;
    }

    public MessageReceivedEvent getEvent() {
        return this.event;
    }

    public String[] getData() {
        return this.data;
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
}
