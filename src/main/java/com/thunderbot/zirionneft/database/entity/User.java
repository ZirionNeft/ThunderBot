package com.thunderbot.zirionneft.database.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "thunder_users")
public class User {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "exp")
    private Double exp = .0;

    @Column(name = "coins")
    private Double coins = 200.0;

    @Column(name = "donator")
    private boolean donator = false;

    @Column(name = "city")
    private String city = "";

    @Column(name = "broadcast_time")
    private String broadcastTime = "-1";

    public User() {
    }

    public User(Long discord_user_id) {
        userId = discord_user_id;
    }

    public Long getUserId() {
        return userId;
    }

    public Double getExp() {
        return exp;
    }

    public Double getCoins() {
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
}
