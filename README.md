## BitcoinTelegramBot

This Telegram bot allows you to subscribe to the Bitcoin exchange rate against the US dollar.

### Available Commands

- /start: Begin using the bot. Subsequent presses will show available commands.
- /subscribe: subscribe to the price. Example: /subscribe 2, /subscribe 11351.44574. If an incorrect value is entered,
  such as "/subscribe" without anything or "/subscribe" without a space or not with numbers, the subscription will not
  work.
- /get_subscription: Show the current subscription.
- /unsubscribe: Unsubscribe from the Bitcoin price.

The bot automatically checks the exchange rate every 2 minutes, and if Bitcoin falls below the subscription value, a
notification is sent. The next notification will be sent after 10-20 minutes.

### Getting Started

1. Download the program from the repository.
2. Create a bot using Botfather.
3. Write the necessary variables in the yaml file.
4. Start Docker.
5. Deploy the container using docker-compose.
6. Run the application.

