package com.safeher.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Service
public class TelegramService {

    @Value("${telegram.bot.token}")
    private String BOT_TOKEN;

    @Value("${telegram.chat.id}")
    private String DEFAULT_CHAT_ID;

    // Send to a specific chatId
    public void sendMessage(String chatId, String message) {
        try {
            String urlString =
                "https://api.telegram.org/bot" + BOT_TOKEN +
                "/sendMessage?chat_id=" + chatId +
                "&text=" + URLEncoder.encode(message, "UTF-8");

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            System.out.println("Telegram → chatId:" + chatId + " response:" + code);

        } catch (Exception e) {
            System.err.println("Telegram error for chatId " + chatId + ": " + e.getMessage());
        }
    }

    // Send to default chatId (fallback)
    public void sendMessage(String message) {
        sendMessage(DEFAULT_CHAT_ID, message);
    }
}