package com.thunderbot.zirionneft.command;

import com.thunderbot.zirionneft.BotUtils;
import com.thunderbot.zirionneft.database.entity.User;
import com.thunderbot.zirionneft.database.service.UserService;
import com.thunderbot.zirionneft.handler.Calculations;
import org.apache.log4j.Logger;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class Profile {
    static Logger logger = Logger.getLogger("Profile.class");

    public static void run(MessageReceivedEvent event, List<String> args) {
        IUser author = event.getAuthor();

        try {
            if (args.size() == 0) {
                BotUtils.sendImage(event.getChannel(), drawProfileImage(author));
            }

            else if (args.size() == 1) {
                List<IUser> mentions = event.getMessage().getMentions();

                if (mentions.size() == 1) {
                    if (!mentions.get(0).isBot()) {
                        BotUtils.sendImage(event.getChannel(), drawProfileImage(mentions.get(0)));
                    }
                }
            }

            else
                help(event);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void help(MessageReceivedEvent event) {

    }

    private static File drawProfileImage(IUser discordUser) throws IOException {
        User userEntity = UserService.getUser(discordUser.getLongID());

        Integer currentLevel = userEntity.getLevel();
        Long currentExp = userEntity.getExp();
        Long nextLevelExp = Calculations.nextLevel(currentLevel);

        BufferedImage backgroundImage = ImageIO.read(new File("images/profile_backgrounds/1.png"));
        BufferedImage userAvatar = ImageIO.read(new URL(discordUser.getAvatarURL()));
        BufferedImage canvas = new BufferedImage(
                backgroundImage.getWidth(),
                backgroundImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = canvas.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(backgroundImage, null, 0, 0);

        g2d.drawImage(resizeImage(userAvatar, 80, 80), null, 22, 42);
        g2d.setPaint(Color.BLACK);
        g2d.drawRect(21, 41, 80, 80);

        BufferedImage ribbonImage = ImageIO.read(new File("images/profile_sources/ribbon-blue.png"));
        g2d.drawImage(ribbonImage.getSubimage(4, 3, 110, 30), null, 7, 126);
        g2d.setPaint(new Color(255, 197, 61));
        g2d.fillOval(42, 119, 38, 38);

        g2d.setPaint(Color.WHITE);
        drawCenteredString(
                g2d,
                currentLevel.toString(),
                new Rectangle(6, 123, 110, 30),
                new Font("Arial", Font.BOLD, 30)
        );

        g2d.setPaint(new Color(80, 132, 230, 100));
        g2d.fillRect(106, 91, 275, 30);

        g2d.setPaint(new Color(26, 59, 118));
        g2d.drawRect(106, 91, 275, 30);


        g2d.setPaint(new Color(116, 32,255));
        g2d.fillRect(109, 94, (int)Math.round(((double)currentExp/nextLevelExp)*270), 25);

        g2d.setPaint(Color.WHITE);
        drawCenteredString(
                g2d,
                currentExp + "XP / " + nextLevelExp + "XP",
                new Rectangle(106, 91, 275, 30),
                new Font("Arial", Font.BOLD, 14)
        );


        g2d.setPaint(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN,  25));
        g2d.drawString(discordUser.getName(), 106, 64);

        g2d.setFont(new Font("Arial", Font.PLAIN, 15));
        g2d.drawString("Some fucken text", 106, 84);

        g2d.dispose();

        File profileFile = new File("images/profiles/" + discordUser.getName() + ".png");
        ImageIO.write(canvas, "png", profileFile);

        return profileFile;
    }

    private static BufferedImage resizeImage(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resized;
    }

    private static void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, x, y);
    }
}
