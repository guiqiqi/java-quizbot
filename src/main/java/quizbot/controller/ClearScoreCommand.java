package quizbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import quizbot.QuestionService;
import quizbot.model.User;
import reactor.core.publisher.Mono;

public class ClearScoreCommand implements Command {

    @Autowired
    private QuestionService service;

    public static final String clearSuccessfullyHint = "Your score has been reset to 0 :)";

    @Override
    public Mono<BotApiMethodMessage> reply(Message message, User user) {
        this.service.resetScore(user);
        return Mono.just(SendMessage.builder()
                .chatId(message.getChatId())
                .text(clearSuccessfullyHint)
                .build());
    }
}
