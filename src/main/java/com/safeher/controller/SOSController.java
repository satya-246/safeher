package com.safeher.controller;

import com.safeher.model.SOSAlert;
import com.safeher.model.EmergencyContact;
import com.safeher.repository.SOSRepository;
import com.safeher.repository.EmergencyContactRepository;
import com.safeher.service.SmsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sos")
@CrossOrigin("*")
public class SOSController {

    @Autowired
    private SOSRepository sosRepository;

    @Autowired
    private EmergencyContactRepository contactRepository;

    // SmsService is optional — won't crash if Twilio not configured
    @Autowired(required = false)
    private SmsService smsService;

    // ── Main SOS endpoint ─────────────────────────────────────
    @PostMapping("/alert")
    public SOSAlert sendSOS(@RequestBody SOSAlert alert) {

        System.out.println("🚨 SOS TRIGGERED by user: " + alert.getUserId());

        // 1. Set timestamp
        alert.setTime(LocalDateTime.now());

        // 2. Build Google Maps link
        String mapLink =
            "https://maps.google.com/?q=" +
            alert.getLatitude() + "," + alert.getLongitude();
        alert.setMapLink(mapLink);

        // 3. Save to MySQL database
        SOSAlert saved = sosRepository.save(alert);

        // 4. Build SMS message
        String message =
            "SOS ALERT! Someone needs help!\n" +
            "Location: " + mapLink;

        // 5. Get all contacts for this user
        List<EmergencyContact> contacts =
            contactRepository.findByUserId(alert.getUserId());

        if (contacts.isEmpty()) {
            System.out.println("No contacts found for user: " + alert.getUserId());
        } else {
            // 6. Send SMS to each contact
            for (EmergencyContact contact : contacts) {
                if (smsService != null) {
                    try {
                        // Format phone number
                        String phone = contact.getPhone()
                            .replaceAll("[\\s\\-()]+", "");
                        if (!phone.startsWith("+")) {
                            phone = "+91" + phone
                                .replaceAll("^0", "")
                                .replaceAll("^91(?=\\d{10})", "");
                        }
                        smsService.sendSOS(phone, message);
                        System.out.println("SMS sent to: "
                            + contact.getName() + " (" + phone + ")");
                    } catch (Exception e) {
                        System.err.println("SMS failed for "
                            + contact.getName() + ": " + e.getMessage());
                    }
                } else {
                    System.out.println("SmsService not available — "
                        + "check TWILIO environment variables");
                }
            }
        }

        return saved;
    }

    // ── Get all alerts ────────────────────────────────────────
    @GetMapping("/all")
    public Iterable<SOSAlert> getAllAlerts() {
        return sosRepository.findAll();
    }
}