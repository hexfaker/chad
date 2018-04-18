package io.github.hexfaker.chad;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties()
public class ChadApplication implements CommandLineRunner {


	@Autowired
	Configuration config;

	TelegramBot chadFiitov;

	@Override
	public void run(String... args) throws Exception {
		chadFiitov = new TelegramBot(config.token);

    chadFiitov.setUpdatesListener(new UpdatesListener() {
      @Override
      public int process(List<Update> updates) {
        for (Update update : updates){
          sendMsg(update);
        }
        // process updates

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
      }
    });
    System.out.print("os");
	}
  public void sendMsg(Update update){
    Message message = update.message();
    if(message.text().equals("/start")) {
      SendMessage request = new SendMessage(message.chat().id(),"Дарова " +message.chat().firstName())
          .parseMode(ParseMode.HTML)
          .disableWebPagePreview(true)
          .disableNotification(true);
      SendResponse sendResponse = chadFiitov.execute(request);
      boolean ok = sendResponse.isOk();
      Message message2 = sendResponse.message();
    }
    else{
      SendMessage request = new SendMessage(message.chat().id(), message.text())
          .parseMode(ParseMode.HTML)
          .disableWebPagePreview(true)
          .disableNotification(true);
      SendResponse sendResponse = chadFiitov.execute(request);
      boolean ok = sendResponse.isOk();
      Message message2 = sendResponse.message();
    }

  }


	public static void main(String[] args) {
		SpringApplication.run(ChadApplication.class, args);
	}
}
