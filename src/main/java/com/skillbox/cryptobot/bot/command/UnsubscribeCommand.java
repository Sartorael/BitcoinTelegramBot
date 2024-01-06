package com.skillbox.cryptobot.bot.command;

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
import java.sql.ResultSet;

/** Обработка команды отмены подписки на курс валюты */
@Service
@Slf4j
public class UnsubscribeCommand implements IBotCommand {
  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Value("${spring.datasource.username}")
  private String dbUsername;

  @Value("${spring.datasource.password}")
  private String dbPassword;

  @Override
  public String getCommandIdentifier() {
    return "unsubscribe";
  }

  @Override
  public String getDescription() {
    return "Отменяет подписку пользователя";
  }

  @Override
  public void processMessage(AbsSender absSender, Message message, String[] arguments) {
    SendMessage answer = new SendMessage();
    answer.setChatId(message.getChatId());
    long telegramUserId = message.getChatId();
    try {
      Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
      String sql = "SELECT price FROM subscribers WHERE telegram_id = ?";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setLong(1, telegramUserId);
      ResultSet result = statement.executeQuery();
      if (result.next()) {
        Double currentPrice = (Double) result.getObject("price");
        if (currentPrice == null) {
          answer.setText("Активные подписки отсутствуют");
        } else {
          String updateSql = "UPDATE subscribers SET price = ? WHERE telegram_id = ?";
          PreparedStatement updateStatement = connection.prepareStatement(updateSql);
          updateStatement.setNull(1, java.sql.Types.DOUBLE);
          updateStatement.setLong(2, telegramUserId);
          int rowsUpdated = updateStatement.executeUpdate();
          if (rowsUpdated > 0) {
            answer.setText("Подписка удалена");
          }

          updateStatement.close();
        }
      } else {
        answer.setText("Активные подписки отсутствуют");
      }
      absSender.execute(answer);
      statement.close();
      connection.close();
    } catch (Exception e) {
      log.error("Ошибка возникла в методе при чтении из БД", e);
    }
  }
}
