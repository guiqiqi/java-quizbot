package quizbot.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import quizbot.QuestionService;
import quizbot.form.QuestionFormStatus;
import quizbot.model.User;
import reactor.core.publisher.Mono;

@Controller
public class UpdateHandler implements LongPollingSingleThreadUpdateConsumer {
    private TelegramClient client;
    private QuestionService service;
    private Map<Class<? extends Command>, Command> commands;

    public UpdateHandler(
            @Autowired TelegramClient client,
            @Autowired QuestionService service,
            @Autowired EchoCommand echoCommand,
            @Autowired RandomQuestionCommand randomQuestionCommand,
            @Autowired ClearScoreCommand clearScoreCommand,
            @Autowired QueryScoreCommand queryScoreCommand,
            @Autowired HelpCommand helpCommand,
            @Autowired AddQuestionCommand addQuestionCommand,
            @Autowired FinishAddingQuestionCommand finishAddingQuestionCommand) {
        this.client = client;
        this.service = service;
        this.commands = new HashMap<>();

        // TODO: add more commands support
        this.commands.put(EchoCommand.class, echoCommand);
        this.commands.put(RandomQuestionCommand.class, randomQuestionCommand);
        this.commands.put(ClearScoreCommand.class, clearScoreCommand);
        this.commands.put(QueryScoreCommand.class, queryScoreCommand);
        this.commands.put(HelpCommand.class, helpCommand);
        this.commands.put(AddQuestionCommand.class, addQuestionCommand);
        this.commands.put(FinishAddingQuestionCommand.class, finishAddingQuestionCommand);
    }

    /**
     * Handler and dispatcher of new comming message,
     * all reply will be generated and pushed into replies queue when possible,
     * then the sender will be able to collect and send them to telegram server.
     * @param message obtained from updated
     * @param user is user object mapped to database who send this message to us
     */
    private void handler(Message message, User user) {
        // Command by default using echo
        Command command = this.commands.get(EchoCommand.class);

        // TODO: add more commands support
        if (message.getText().startsWith("/random"))
            command = this.commands.get(RandomQuestionCommand.class);
        else if (message.getText().equals("/clear"))
            command = this.commands.get(ClearScoreCommand.class);
        else if (message.getText().startsWith("/score"))
            command = this.commands.get(QueryScoreCommand.class);
        else if (message.getText().equals("/help"))
            command = this.commands.get(HelpCommand.class);

        // Check if user is adding data to question form
        else if (this.service.ifUserSubmittingForm(user) &&
                this.service.formStatus(user) == QuestionFormStatus.AddingWrongOptions &&
                message.getText().equals("/finish"))
            command = this.commands.get(FinishAddingQuestionCommand.class);
        else if (this.service.ifUserSubmittingForm(user) || message.getText().equals("/add"))
            command = this.commands.get(AddQuestionCommand.class);

        Mono<BotApiMethodMessage> reply = command.reply(message, user);
        reply.subscribe(this::sendReply);
    }

    /**
     * Consume update from telegram bot API and generate reply.
     */
    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            User user = this.localizeUser(message.getFrom());
            this.handler(message, user);
        }
    }

    /**
     * Send reply back to telegram server.
     * @param reply generated from command handler
     */
    private void sendReply(BotApiMethodMessage reply) {
        try {
            client.execute(reply);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert telegram user into local database user.
     * @param user informations gathered from telegram API
     * @return user mapped to local database
     */
    private User localizeUser(org.telegram.telegrambots.meta.api.objects.User user) {
        return service.ensureUser(user.getId().toString(), user.getUserName());
    }
}