package io.github.hexfaker.chad;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties
@Slf4j
@RequiredArgsConstructor
public class ChadApplication implements CommandLineRunner {


  private final Configuration config;
  private final Akinator answers;

  TelegramBot chadFiitov;

  private final static String INSTRUCTION = "\n" +
    "Кидай мне слова из вопросов. Чем более характерные, тем лучше. " +
    "А я буду искать вопросы, их содержащие и слать тебе ответы." +
    "\n" +
    "Я понятные слова говорю? )))))))))";


  @Override
  public void run(String... args) throws Exception {
    chadFiitov = new TelegramBot(config.token);

    chadFiitov.setUpdatesListener(updates -> {
      for (Update update : updates) {
        handleUpdate(update);
      }
      return UpdatesListener.CONFIRMED_UPDATES_ALL;
    });
    log.info("Successfully subscribed to telegram updates");
  }

  private SendMessage makeReply(Message message) {
    SendMessage result = null;


    if (message.text() != null) {
      result = new SendMessage(
        message.chat().id(),
        answers.getAnswer(message.text())
      ).replyToMessageId(message.messageId());
    }

    if (message.entities() != null && message.entities().length > 0) {
      List<MessageEntity> messageEntities = Arrays.asList(message.captionEntities());
      result = new SendMessage(message.chat().id(), "Дарова " + message.chat().firstName() + INSTRUCTION);
    }
    return result;
  }

  public void handleUpdate(Update update) {
    try {
      SendMessage replyMessage = makeReply(update.message());

      if (replyMessage == null)
        replyMessage = new SendMessage(update.message().chat().id(), "Неизвестная команда");

      SendResponse rsp = chadFiitov.execute(replyMessage);

      if (!rsp.isOk()) {
        log.error("Send failed {}", rsp);
      }
    } catch (Exception e) {
      log.error("Send failed {}", e);
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(ChadApplication.class, args);
  }
}

