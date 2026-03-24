package com.safeher.controller;

import com.safeher.model.SOSAlert;
import com.safeher.model.EmergencyContact;
import com.safeher.model.TelegramRegistration;
import com.safeher.repository.SOSRepository;
import com.safeher.repository.EmergencyContactRepository;
import com.safeher.repository.TelegramRegistrationRepository;
import com.safeher.service.TelegramService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sos")
@CrossOrigin
public class SOSController {

    @Autowired private SOSRepository                  sosRepository;
    @Autowired private TelegramService                telegramService;
    @Autowired private EmergencyContactRepository     contactRepository;
    @Autowired private TelegramRegistrationRepository registrationRepository;

    @PostMapping("/alert")
    public SOSAlert sendSOS(@RequestBody SOSAlert alert) {

        System.out.println("🚨 SOS TRIGGERED by user: " + alert.getUserId());

        alert.setTime(LocalDateTime.now());

        String mapLink =
            "https://maps.google.com/?q=" +
            alert.getLatitude() + "," + alert.getLongitude();

        alert.setMapLink(mapLink);
        SOSAlert saved = sosRepository.save(alert);

        // SOS message text
        String message =
            "🚨 SAFEHER SOS ALERT 🚨\n\n" +
            "Someone needs immediate help!\n\n" +
            "📍 Live Location:\n" + mapLink + "\n\n" +
            "Please respond immediately!";

        // Get all emergency contacts for this user
        List<EmergencyContact> contacts =
            contactRepository.findByUserId(alert.getUserId());

        if (contacts.isEmpty()) {
            // No contacts — send to default
            System.out.println("No contacts found — sending to default chat");
            telegramService.sendMessage(message);
            return saved;
        }

        int sentCount = 0;

        for (EmergencyContact contact : contacts) {

            // Clean the phone number to match how it was registered
            String cleanPhone = contact.getPhone()
                    .replaceAll("[\\s\\-()]+", "")
                    .replaceAll("^\\+91", "")
                    .replaceAll("^91(?=\\d{10})", "");

            // Look up their Telegram Chat ID by phone number
            Optional<TelegramRegistration> reg =
                    registrationRepository.findByPhone(cleanPhone);

            if (reg.isPresent()) {
                // Found their chatId — send alert directly to them
                String chatId = reg.get().getChatId();
                telegramService.sendMessage(chatId, message);
                System.out.println("✅ Alert sent to " + contact.getName()
                        + " (phone:" + cleanPhone + " chatId:" + chatId + ")");
                sentCount++;
            } else {
                // Contact has not registered with the bot yet
                System.out.println("⚠️ " + contact.getName()
                        + " (" + cleanPhone + ") has not registered with SafeHer bot");
            }
        }

        // If none of the contacts registered yet, fall back to default
        if (sentCount == 0) {
            System.out.println("No contacts registered — using default chat");
            telegramService.sendMessage(message);
        }

        System.out.println("📱 Sent to " + sentCount + "/" + contacts.size() + " contacts");
        return saved;
    }

    @GetMapping("/all")
    public Iterable<SOSAlert> getAllAlerts() {
        return sosRepository.findAll();
    }
}