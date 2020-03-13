package tkoaly.pohinatiimi.laudekoodit;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import io.github.cdimascio.dotenv.Dotenv;
import org.jsoup.Jsoup;
import java.io.IOException;

public class LaudeBot extends TelegramLongPollingBot {

    private static final String heppaUrl = "https://heppa.herokuapp.com/candidates";
    private Dotenv dotenv = Dotenv.load();

    @Override
    public void onUpdateReceived(Update update) {
        
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        
        // Initialize answer message
        SendMessage answer = new SendMessage()
            .setChatId(chatId);
        
        // Choose text for answer
        if (text.equals("Hello")) {
            answer.setText(fetchHeppa());
        } else {
            answer.setText("Happy hacking!");
        }
        
        // Send message
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "official_heppabot";
    }

    @Override
    public String getBotToken() {
        return dotenv.get("1002374578:AAHl6U-dHf8KvMeINgACOQaI2WFfS-9bKQs");
    }

    private String fetchHeppa() {
        String heppaHtml = "";
        
        try {
            heppaHtml = Jsoup.connect(heppaUrl).get().html();
        } catch (Exception e) {
            
        }

        return heppaHtml;
    }
}
