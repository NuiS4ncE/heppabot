package tkoaly.pohinatiimi.laudekoodit;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import io.github.cdimascio.dotenv.Dotenv;
import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;

public class LaudeBot extends TelegramLongPollingBot {

    private static final String heppaUrl = "https://heppa.herokuapp.com/candidates";
    private Dotenv dotenv = Dotenv.load();
    private int num, moviesize;
    

    @Override
    public void onUpdateReceived(Update update) {

        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        // Initialize answer message
        SendMessage answer = new SendMessage()
                .setChatId(chatId);
        
        Document doc = Jsoup.parse(fetchHeppa());
        Element table = doc.select("table").get(0);
        Elements movies = table.select("tr");
        moviesize = movies.size() - 1;
        // Choose text for answer
        int inputnum = Integer.parseInt(text.substring(4));
        if (inputnum > moviesize) {
            answer.setText("These are not the answers you're looking for. Amount of movies is " + moviesize);
        }
        else if (text.substring(0, 4).equals("/top")) {

            num = inputnum;
            answer.setText(buildMessage());
        } else {
            answer.setText("Whatchu talking about, foo!");
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
        return dotenv.get("BOT_TOKEN");
    }

    private String fetchHeppa() {
        String heppaHtml = "";

        try {
            heppaHtml = Jsoup.connect(heppaUrl).get().html();
        } catch (Exception e) {

        }

        return heppaHtml;
    }

    /* private String findMovie(){
        try{
        String url = "https://heppa.herokuapp.com/candidates";
        Document document = Jsoup.connect(url).get();
        Elements links = document.select("a[href]");
        String movie = "";
        for (Element link : links){
            if(link.text() == "Edit" || link.text() == "Login" || link.text() == "Add a candidate") continue;
            movie += link.text() + " " + "\n";
            
            System.out.println("Movie: " + movie);
        } 
                return movie;
        } catch (Exception e){
            System.out.println(e.toString());
            return e.toString().substring(0,100);
        }
    } */
    private ArrayList<String> parseTopMovies(String heppaHtml) {
        Document doc = Jsoup.parse(heppaHtml);
        Element table = doc.select("table").get(0);
        Elements movies = table.select("tr");
        
        ArrayList<String> topMovies = new ArrayList<>();

        for (int i = 1; i < Math.min(num + 1, movies.size()); i++) {
            Elements fields = movies.get(i).select("td");

            String movieEntry = "";

            movieEntry += fields.get(0).text();
            movieEntry += " - ";
            movieEntry += fields.get(3).text();
            movieEntry += " ääntä";

            topMovies.add(movieEntry);
        }

        return topMovies;
    }

    private String buildMessage() {
        String msg = "";
        ArrayList<String> movies = parseTopMovies(fetchHeppa());

        for (int i = 0; i < movies.size(); i++) {
            msg += movies.get(i);
            msg += "\n";
        }

        return msg;
    }

}
