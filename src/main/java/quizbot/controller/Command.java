package quizbot.controller;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import quizbot.model.User;
import reactor.core.publisher.Mono;

public interface Command {
    /**
     * Reply user's request with a SendMessage Object.
     * @param messageText obtained from telegram bot API as request of user
     * @param chatId obtained from message body as id of chat
     * @param user who is sending this request
     * @return generated response of request
     */
    public Mono<BotApiMethodMessage> reply(Message message, User user);
}
