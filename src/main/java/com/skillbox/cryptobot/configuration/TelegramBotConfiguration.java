package com.skillbox.cryptobot.configuration;

import com.skillbox.cryptobot.bot.CryptoBot;
import com.skillbox.cryptobot.bot.Reminder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Slf4j
@EnableScheduling
public class TelegramBotConfiguration {
  int currencyUpdate_rate = 120000;
  int notificationRate = 120000 * 5;
  @Autowired private Reminder reminder;

  @Bean
  public Message message() {
    return new Message();
  }

  @Bean
  public TaskScheduler taskScheduler(AbsSender absSender, Message message, String[] arguments) {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(5);
    scheduler.initialize();

    scheduler.scheduleAtFixedRate(
        () -> reminder.priceReminder(absSender, message, arguments), currencyUpdate_rate);
    scheduler.scheduleAtFixedRate(() -> reminder.clearNotificationUsers(), notificationRate);

    return scheduler;
  }

  @Bean
  TelegramBotsApi telegramBotsApi(CryptoBot cryptoBot) {
    TelegramBotsApi botsApi = null;
    try {
      botsApi = new TelegramBotsApi(DefaultBotSession.class);
      botsApi.registerBot(cryptoBot);
    } catch (TelegramApiException e) {
      log.error("Error occurred while sending message to telegram!", e);
    }
    return botsApi;
  }
}
