package com.skillbox.cryptobot.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.UUID;


@Entity
@Table(name = "Subscribers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subscribers {

    @Column(unique = true, nullable = false)
    private UUID uuid;


    @Column(unique = true, nullable = false)
    private long telegramId;

    @Column(nullable = true)
    private Double price;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(long telegramId) {
        this.telegramId = telegramId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
