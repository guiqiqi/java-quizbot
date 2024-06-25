package quizbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import quizbot.QuestionService;
import quizbot.model.User;

@Controller
public class UpdateHandler implements LongPollingSingleThreadUpdateConsumer {

    @Autowired
    private TelegramClient client;

    @Autowired
    private QuestionService service;

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            User user = this.localizeUser(message.getFrom());
            String messageText = message.getText();
            Long chatId = message.getChatId();
            SendMessage reply = SendMessage
                    .builder()
                    .chatId(chatId)
                    .text(String.format("%s: %s", user.getId(), messageText))
                    .build();
            try {
                client.execute(reply);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Convert telegram user into local database user.
     * @param user informations gathered from telegram API
     * @return user mapped to local database
     */
    private User localizeUser(org.telegram.telegrambots.meta.api.objects.User user) {
        return service.ensureUser(user.getId().toString(), user.getUserName());
    }
}