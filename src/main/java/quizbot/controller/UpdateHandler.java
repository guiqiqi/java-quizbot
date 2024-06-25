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
import reactor.core.publisher.Mono;

@Controller
public class UpdateHandler implements LongPollingSingleThreadUpdateConsumer {

    @Autowired
    private TelegramClient client;

    @Autowired
    private QuestionService service;

    /**
     * Handler and dispatcher of new comming message,
     * all reply will be generated and pushed into replies queue when possible,
     * then the sender will be able to collect and send them to telegram server.
     * @param messageText is message text content
     * @param chatId
     * @param user
     */
    private void handler(Message message, User user) {
        Command command = new EchoCommand();
        Mono<SendMessage> reply = command.reply(message, user);
        reply.subscribe(this::sendReply);
    }

    /**
     * Consume update from telegram bot API and generate reply.
     */
    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            User user = this.localizeUser(message.getFrom());
            this.handler(message, user);
        }
    }

    /**
     * Send reply back to telegram server.
     * @param reply generated from command handler
     */
    private void sendReply(SendMessage reply) {
        try {
            client.execute(reply);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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