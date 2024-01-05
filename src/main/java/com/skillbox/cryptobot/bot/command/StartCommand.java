package com.skillbox.cryptobot.bot.command;


import com.skillbox.cryptobot.models.Subscribers;
import com.skillbox.cryptobot.service.CryptoCurrencyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.*;

import java.util.UUID;


/**
 * Обработка команды начала работы с ботом
 */
@Service

@Slf4j
public class StartCommand implements IBotCommand {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Override
    public String getCommandIdentifier() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Запускает бота";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        answer.setText("""
                Привет! Данный бот помогает отслеживать стоимость биткоина.
                Поддерживаемые команды:
                 /get_price - получить стоимость биткоина
                 /subscribe - подписаться на желаемую стоимость биткоина
                 /get_subscription - получение сведений о текущей подписке
                 /unsubscribe - удаление текущей подписки
                """);
        try {
            Subscribers subscribers = new Subscribers();
            subscribers.setTelegramId(message.getChatId());
            subscribers.setUuid(UUID.randomUUID());
            insertSubscriber(subscribers);
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error occurred in /start command", e);
        }
    }
    private void insertSubscriber(Subscribers subscribers) {
        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, "subscribers", null);
            if (!resultSet.next()) {
                createSubscribersTable(connection);
            }
            String checkSql = "SELECT COUNT(*) FROM subscribers WHERE telegram_id = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setLong(1, subscribers.getTelegramId());
            ResultSet checkResult = checkStatement.executeQuery();
            checkResult.next();
            int count = checkResult.getInt(1);
            if (count == 0) {
                String sql = "INSERT INTO subscribers (uuid, telegram_id, price) VALUES (?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setObject(1, subscribers.getUuid());
                statement.setLong(2, subscribers.getTelegramId());
                statement.setNull(3, java.sql.Types.DOUBLE);
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Data insert success!");
                }
                statement.close();
            } else {
                System.out.println("User with telegram_id " + subscribers.getTelegramId() + " already exists in the database.");
            }
            checkStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createSubscribersTable(Connection connection) {
        try {
            String sql = "CREATE TABLE subscribers (" + "uuid UUID PRIMARY KEY," + "telegram_id SERIAL," +
                    "price DOUBLE PRECISION NULL" + ")";
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}