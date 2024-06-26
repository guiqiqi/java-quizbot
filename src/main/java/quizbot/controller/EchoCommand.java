package quizbot.controller;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import reactor.core.publisher.Mono;

import quizbot.model.User;

@Component
public class EchoCommand implements Command {
    /**
     * Simple echo command message handler for testing.
     */
    @Override
    public Mono<BotApiMethodMessage> reply(Message message, User user) {
        SendMessage reply = SendMessage
                .builder()
                .chatId(message.getChatId())
                .text(String.format("%s", message.getText()))
                .build();
        return Mono.just(reply);
    }
}
