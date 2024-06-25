package quizbot;

import java.util.Properties;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;

import quizbot.controller.UpdateHandler;

public class Application {
    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("production");
        context.register(ApplicationConfig.class);
        context.refresh();
        try (TelegramBotsLongPollingApplication application = new TelegramBotsLongPollingApplication()) {
            LongPollingUpdateConsumer consumer = context.getBean(UpdateHandler.class);
            Properties properties = (Properties) context.getBean("properties");
            application.registerBot(properties.getProperty("telegram.bot.token"), consumer);
            Thread.currentThread().join();
        } catch (InterruptedException error) {
            context.close();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
}
