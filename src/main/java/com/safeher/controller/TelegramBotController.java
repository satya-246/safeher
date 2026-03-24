package com.safeher.controller;

import com.safeher.model.TelegramRegistration;
import com.safeher.repository.TelegramRegistrationRepository;
import com.safeher.service.TelegramService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// This controller receives messages from Telegram via webhook
// When a contact sends: /register 9876543210
// Telegram calls: POST /api/telegram/webhook
// This saves their chatId ↔ phone in the database

@RestController
@RequestMapping("/api/telegram")
@CrossOrigin
public class TelegramBotController {

    @Autowired
    private TelegramRegistrationRepository registrationRepository;

    @Autowired
    private TelegramService telegramService;

    // ── Telegram Webhook ──────────────────────────────────────
    // Telegram calls this URL every time someone sends a message to your bot
    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody Map<String, Object> body) {
        try {
            // Extract message from Telegram's JSON structure
            Map<String, Object> message = (Map<String, Object>) body.get("message");
            if (message == null) return;

            // Get the chat ID of the person who sent the message
            Map<String, Object> chat = (Map<String, Object>) message.get("chat");
            String chatId = String.valueOf(chat.get("id"));

            // Get the text they sent
            String text = (String) message.get("text");
            if (text == null) return;

            text = text.trim();
            System.out.println("Telegram message from chatId " + chatId + ": " + text);

            // ── Handle /register command ──────────────────────
            // Contact types: /register 9876543210
            if (text.startsWith("/register")) {
                String[] parts = text.split(" ");

                if (parts.length < 2) {
                    // No phone number given — ask them to include it
                    telegramService.sendMessage(chatId,
                        "Please send your phone number like this:\n/register 9876543210");
                    return;
                }

                // Clean the phone number — remove spaces, +91, dashes
                String phone = parts[1].trim()
                        .replaceAll("[\\s\\-()]+", "")
                        .replaceAll("^\\+91", "")
                        .replaceAll("^91(?=\\d{10})", "");

                // Save or update the mapping in database
                TelegramRegistration existing =
                        registrationRepository.findByPhone(phone).orElse(null);

                if (existing != null) {
                    // Update existing registration
                    existing.setChatId(chatId);
                    registrationRepository.save(existing);
                } else {
                    // New registration
                    registrationRepository.save(
                            new TelegramRegistration(phone, chatId));
                }

                System.out.println("✅ Registered: phone=" + phone + " chatId=" + chatId);

                // Send confirmation message back to the contact
                telegramService.sendMessage(chatId,
                    "✅ You are now registered with SafeHer!\n\n" +
                    "Phone: " + phone + "\n" +
                    "You will receive SOS alerts if someone adds " +
                    phone + " as their emergency contact.\n\n" +
                    "Stay safe! 🛡️");

            // ── Handle /start command ─────────────────────────
            } else if (text.startsWith("/start")) {
                telegramService.sendMessage(chatId,
                    "Welcome to SafeHer Bot! 🛡️\n\n" +
                    "To receive SOS alerts, register your phone number:\n\n" +
                    "/register YOUR_PHONE_NUMBER\n\n" +
                    "Example:\n/register 9876543210");

            // ── Handle /status command ────────────────────────
            } else if (text.startsWith("/status")) {
                // Check if this chatId is registered
                boolean registered = registrationRepository.findAll()
                        .stream().anyMatch(r -> r.getChatId().equals(chatId));
                if (registered) {
                    telegramService.sendMessage(chatId,
                        "✅ You are registered with SafeHer.\nYou will receive SOS alerts.");
                } else {
                    telegramService.sendMessage(chatId,
                        "❌ You are not registered yet.\nSend: /register YOUR_PHONE_NUMBER");
                }
            }

        } catch (Exception e) {
            System.err.println("Webhook error: " + e.getMessage());
        }
    }

    // ── Manual check — see all registrations (for testing) ───
    @GetMapping("/registrations")
    public Object getAllRegistrations() {
        return registrationRepository.findAll();
    }
}
