package com.skillbox.cryptobot.bot;



import com.skillbox.cryptobot.models.Subscribers;
import com.skillbox.cryptobot.service.CryptoCurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;



@Service
@Slf4j
public class Reminder {
    ArrayList<Subscribers> subscriberList = new ArrayList<>();
    private Map<Long, Long> notificatedUsers = new HashMap<>();
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    private final CryptoCurrencyService service;

    public Reminder(CryptoCurrencyService service) {
        this.service = service;
    }
    private Subscribers subscribers;


    public void priceReminder(AbsSender absSender, Message message, String[] arguments){
        log.info(" priceReminder initialised");
        String sql = "SELECT * FROM subscribers";
        Subscribers subscriber = new Subscribers();
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        subscriber.setUuid(UUID.fromString(resultSet.getString("uuid")));
                        subscriber.setTelegramId(resultSet.getLong("telegram_id"));
                        subscriber.setPrice(resultSet.getDouble("price"));
                        subscriberList.add(subscriber);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Ошибка в получении данных из бд для напоминания");
        }
        try {
            double currentBitcoinPrice = service.getBitcoinPrice();
            for (Subscribers subscribe : subscriberList) {
                SendMessage answer = new SendMessage();
                answer.setChatId(subscribe.getTelegramId());
                if (subscribe.getPrice() > currentBitcoinPrice && !notificatedUsers.containsKey(subscribe
                        .getTelegramId())) {
                    answer.setText("Пора покупать, стоимость биткоина " + currentBitcoinPrice);
                    absSender.execute(answer);
                    notificatedUsers.put(subscribe.getTelegramId(),System.currentTimeMillis());
                }
            }
        } catch (Exception e) {log.error(" проблема с отправкой или получением цены ");}
    }

    public void clearNotificatedUsers(){
        int tenMinutes = 10 * 60 * 1000;
        log.info("priceClearence initialised");
        long currentTime = System.currentTimeMillis();
        notificatedUsers.entrySet().removeIf(entry -> (currentTime - entry.getValue()) > (tenMinutes));
    }
}
