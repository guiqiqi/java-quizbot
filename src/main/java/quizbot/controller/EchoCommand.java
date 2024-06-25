package quizbot.controller;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import quizbot.model.User;
import reactor.core.publisher.Mono;

public class EchoCommand implements Command {
    /**
     * Simple echo command message handler for testing.
     */
    @Override
    public Mono<SendMessage> reply(Message message, User user) {
        SendMessage reply = SendMessage
                .builder()
                .chatId(message.getChatId())
                .text(String.format("%s: %s", user.getId(), message.getText()))
                .build();
        return Mono.just(reply);
    }
}
