package quizbot;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;

import quizbot.controller.RandomQuestionCommand;
import quizbot.controller.UpdateHandler;
import quizbot.model.QuestionWithOptions;

@Service
@Profile("production")
public class ScheduledTask {
    @Autowired
    private RandomQuestionCommand randomQuestionCommand;

    @Autowired
    private QuestionService service;

    @Autowired
    private UpdateHandler updateHandler;

    /**
     * Send a random quiz every day at 10 a.m.
     * @param randomQuestionCommand autowired from RandomQuestionCommand for converting question to poll
     * @param service autowired from QuestionService for selecting random question for user
     * @param updateHandler autowire from UpdateHandler for sending poll as reply
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void sendingRandomQuiz() {
        this.service.listAllUsers().forEach(user -> {
            Long chatId = Long.parseLong(user.getTelegram());
            Optional<QuestionWithOptions> question = service.randomQuestion(user);
            if (question.isPresent()) {
                SendPoll poll = this.randomQuestionCommand.questionWithOptions2Poll(
                        user, chatId, question.get());
                this.updateHandler.sendReply(poll);
            }
        });
    }
}
