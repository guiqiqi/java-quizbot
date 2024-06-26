package quizbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import quizbot.QuestionService;
import quizbot.form.QuestionFormStatus;
import quizbot.model.User;
import reactor.core.publisher.Mono;

public class AddQuestionCommand implements Command {
    @Autowired
    private QuestionService service;

    public static final String waitingQuestionHint = "Please input question:";
    public static final String waitingTagHint = "Please input tag:";
    public static final String waitingCorrectOptionHint = "Please input correct option:";
    public static final String addingWrongOptionsHint = "Reply to add an wrong option: \nOr finish adding by replying /finish.";
    public static final String serverSideWrongHint = "Invalid status, try again later :(";

    @Override
    public Mono<BotApiMethodMessage> reply(Message message, User user) {
        // If user start a new form, return a hint and create form for user
        if (!this.service.ifUserSubmittingForm(user)) {
            this.service.formStatus(user);
            return Mono.just(this.sendTextMessage(message, waitingQuestionHint));
        }

        this.service.addData2QuestionForm(user, message.getText());

        // If request for forming a new question form, just return a hint
        if (this.service.formStatus(user) == QuestionFormStatus.WaitingTag)
            return Mono.just(this.sendTextMessage(message, waitingTagHint));

        // If current status is WaitingTag, collect question content and send hint
        if (this.service.formStatus(user) == QuestionFormStatus.WaitingCorrectOption)
            return Mono.just(this.sendTextMessage(message, waitingCorrectOptionHint));

        // If current status is AddingWrongOptions, collect correct option and send
        // hint with end submitting button + adding wrong option
        if (this.service.formStatus(user) == QuestionFormStatus.AddingWrongOptions)
            return Mono.just(this.sendTextMessage(message, addingWrongOptionsHint));

        return Mono.just(this.sendTextMessage(message, serverSideWrongHint));
    }

    /**
     * Build an reply based on text of reply and request message.
     * @param request that user sent to server, using for get chat id
     * @param reply content of reply
     * @return built reply SendMessage
     */
    private SendMessage sendTextMessage(Message request, String reply) {
        return SendMessage.builder().chatId(request.getChatId()).text(reply).build();
    }
}
