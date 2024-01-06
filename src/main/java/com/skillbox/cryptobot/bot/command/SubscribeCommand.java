package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.bot.security.RegexCommandChecker;
import com.skillbox.cryptobot.service.CryptoCurrencyService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/** Обработка команды подписки на курс валюты */
@Service
@Slf4j
public class SubscribeCommand implements IBotCommand {
  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Value("${spring.datasource.username}")
  private String dbUsername;

  @Value("${spring.datasource.password}")
  private String dbPassword;

  private final CryptoCurrencyService service;

  public SubscribeCommand(CryptoCurrencyService service, RegexCommandChecker regexCommandChecker) {
    this.service = service;
    this.regexCommandChecker = regexCommandChecker;
  }

  @Override
  public String getCommandIdentifier() {
    return "subscribe";
  }

  @Override
  public String getDescription() {
    return "Подписывает пользователя на стоимость биткоина";
  }

  RegexCommandChecker regexCommandChecker;

  @Override
  public void processMessage(AbsSender absSender, Message message, String[] arguments) {
    SendMessage answer = new SendMessage();
    answer.setChatId(message.getChatId());
    try {
      String userInput = message.getText();
      if (regexCommandChecker.subscribeMethodChecker(userInput)) {
        long telegramUserId = message.getChatId();
        String[] userInputParts = userInput.split(" ");
        Double price = Double.parseDouble(userInputParts[1]);
        Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        String sql = "UPDATE subscribers SET price = ? WHERE telegram_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setDouble(1, price);
        statement.setLong(2, telegramUserId);
        int rowsUpdated = statement.executeUpdate();
        if (rowsUpdated > 0) {
          answer.setText(
              "Текущая цена биткоина "
                  + TextUtil.toString(service.getBitcoinPrice())
                  + " USD"
                  + System.lineSeparator()
                  + "Новая подписка создана на стоимость "
                  + price);
          absSender.execute(answer);
        }
        statement.close();
        connection.close();
      } else {
        answer.setText(
            "Выввели неверную команду. Введите /subscribe число."
                + " Пример правильного ввода '/subscribe 2001135' или /subscribe 200000.321531");
        absSender.execute(answer);
      }
    } catch (Exception e) {
      log.error("Ошибка возникла /subscribe методе при добавлении в БД", e);
    }
  }
}
