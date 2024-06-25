package quizbot.controller;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import quizbot.model.User;
import reactor.core.publisher.Mono;

public class HelpCommand implements Command {

    public static final String helpMessage = """
Welcome to polytech-quizbot, here is an instruction of how to use this bot:

/help show this help message.
/add add a new quiz.
/random start an random quiz.
/random <tag> start an random quiz with given tag.
/score query your current score.
/score <tag> score gathered by questions with given tag.
/clear reset your current score to 0.""";

    @Override
    public Mono<BotApiMethodMessage> reply(Message message, User user) {
        return Mono.just(SendMessage.builder()
                .chatId(message.getChatId())
                .text(helpMessage)
                .build());
    }
}
