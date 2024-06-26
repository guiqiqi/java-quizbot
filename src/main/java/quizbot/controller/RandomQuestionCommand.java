package quizbot.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.polls.input.InputPollOption;

import reactor.core.publisher.Mono;

import quizbot.QuestionService;
import quizbot.model.Option;
import quizbot.model.Question;
import quizbot.model.QuestionWithOptions;
import quizbot.model.User;

/**
 * This command works on two ways:
 * 0. If user send "/random" without tag string, then try to select an random
 *    question for him/her.
 * 1. If user send "/random <tag>", then try to select an random
 *    quesiton with given tag.
 * 
 * If no question could be selected by given request, reply with a simple 
 * text message with "Sorry, no matching questions found :(".
 * 
 * Otherwise, a telegram poll will be sent and the title contains question id
 * like "1. If I just lie here, would you lie with me and just forget the world?",
 * so we could know which question users are answering.
 */
@Component
public class RandomQuestionCommand implements Command {

    @Autowired
    private QuestionService service;

    public static final String haveUnifinishedQuestion = "Sorry, you have unifinished quiz, please finish it before request an new one.";
    public static final String noQuestionFoundHint = "Sorry, no matching questions found :(";

    @Override
    public Mono<BotApiMethodMessage> reply(Message message, User user) {
        // Check if user contains unfinished quiz
        if (this.service.ifChatIdHaveUnfinishedQuestions(user))
            return Mono.just(SendMessage.builder().chatId(message.getChatId()).text(haveUnifinishedQuestion).build());

        Optional<QuestionWithOptions> question;
        String[] commands = message.getText().split(" ");
        if (commands.length == 1) {
            question = this.service.randomQuestion(user);
        } else {
            question = this.service.randomQuestion(user, commands[1]);
        }
        if (question.isEmpty()) {
            return Mono.just(SendMessage.builder()
                    .chatId(message.getChatId())
                    .text(noQuestionFoundHint)
                    .build());
        } else {
            return Mono.just(this.questionWithOptions2Poll(
                    user, message.getChatId(), question.get()));
        }
    }

    /**
     * Convert QuestionWithOptions object to an Telegram Poll message.
     * @param user is who answering quiz mapped from database
     * @param chatId is chat context id for replying correctly to user
     * @param question is question and options selected from database
     * @return made poll message
     */
    private SendPoll questionWithOptions2Poll(User user, Long chatId, QuestionWithOptions questionWithOptions) {
        Question question = questionWithOptions.getQuestion();
        List<Option> options = questionWithOptions.getOptions();
        Collections.shuffle(options);
        List<InputPollOption> inputOptions = options.stream()
                .map(option -> new InputPollOption(option.getContent()))
                .toList();
        Integer correctOptionId = 0;
        for (; correctOptionId < options.size(); correctOptionId++) {
            if (options.get(correctOptionId).getCorrectness())
                break;
        }
        String questionText = String.format("%d. %s", question.getId(), question.getContent());
        SendPoll poll = SendPoll.builder().chatId(chatId)
                .type("quiz")
                .correctOptionId(correctOptionId)
                .question(questionText)
                .options(inputOptions)
                .protectContent(true)
                .isAnonymous(false)
                .openPeriod(10)
                .build();

        // Update current answering question record in memory
        this.service.updateAnsweringQuestion(
                user,
                question.getId(),
                options.stream().map(option -> option.getId()).toList());

        return poll;
    }
}
