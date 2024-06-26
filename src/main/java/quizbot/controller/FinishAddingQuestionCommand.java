package quizbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import quizbot.QuestionService;
import quizbot.model.User;
import reactor.core.publisher.Mono;

public class FinishAddingQuestionCommand implements Command {
    @Autowired
    QuestionService service;

    public static final String finishAddingHint = "You successfully add a new quiz :)";

    @Override
    public Mono<BotApiMethodMessage> reply(Message message, User user) {
        this.service.submitQuestionForm(user);
        return Mono.just(new SendMessage(message.getChatId().toString(), finishAddingHint));
    }
}
