package quizbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import quizbot.QuestionService;
import quizbot.model.User;
import reactor.core.publisher.Mono;

/**
 * Query score command also works on two ways:
 * 0. If user send "/score" without tag string, then it will printout user's current score.
 * 1. If user send "/score <tag>", it will return user's score by given them.
 * 
 * Note: if <tag> provided not found in database, it results 0 points got as result.
 */
public class QueryScoreCommand implements Command {

    @Autowired
    QuestionService service;

    public static final String scoreQueryHint = "Your current score is: %d";
    public static final String scoreQueryHintByTag = "Your current score by theme %s is: %d";

    @Override
    public Mono<BotApiMethodMessage> reply(Message message, User user) {
        String[] commands = message.getText().split(" ");
        String reply;
        if (commands.length == 1) {
            reply = String.format(
                    scoreQueryHint,
                    this.service.calculateScore(user));
        } else {
            reply = String.format(
                    scoreQueryHintByTag,
                    commands[1],
                    this.service.calculateScore(user, commands[1]));
        }
        return Mono.just(SendMessage.builder()
                .chatId(message.getChatId())
                .text(reply)
                .build());
    }
}
