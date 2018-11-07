package zirionneft.thunder.database.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "thunder_users")
public class User {
    @Id
    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "exp")
    private double exp = 0;

    @Column(name = "coins")
    private double coins = 200;

    @Column(name = "donator")
    private boolean donator = false;

    @Column(name = "city")
    private String city = "";

    @Column(name = "broadcast_time")
    private String broadcastTime = "-1";

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GuildManager> guildManager = new HashSet<>();

    public User() {
    }

    public User(Long discord_user_id) {
        userId = discord_user_id;
    }

    public long getUserId() {
        return userId;
    }

    public double getExp() {
        return exp;
    }

    public double getCoins() {
        return coins;
    }

    public boolean getDonator() {
        return donator;
    }

    public String getBroadcastTime() {
        return broadcastTime;
    }

    public String getCity() {
        return city;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }

    public void setExp(double exp) {
        this.exp = exp;
    }

    public void setDonator(boolean donator) {
        this.donator = donator;
    }

    public void setBroadcastTime(String broadcastTime) {
        this.broadcastTime = broadcastTime;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Set<GuildManager> getGuildManager() {
        return guildManager;
    }
}
