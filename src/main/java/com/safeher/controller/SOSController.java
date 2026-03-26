package com.safeher.controller;

import com.safeher.model.SOSAlert;
import com.safeher.model.EmergencyContact;
import com.safeher.repository.SOSRepository;
import com.safeher.repository.EmergencyContactRepository;
import com.safeher.service.SmsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    // Security: SOS is only sent to contacts registered under alert.getUserId().
    // The requestingUserId header must match the userId in the alert body.
    // This ensures a user cannot trigger SOS on behalf of another user
    // and cannot cause SMS to be sent to another user's contacts.
    @PostMapping("/alert")
    public ResponseEntity<?> sendSOS(
            @RequestBody SOSAlert alert,
            @RequestHeader(value = "requestingUserId", required = false) Long requestingUserId) {

        // 🔒 Validate that the sender matches the alert's userId
        if (requestingUserId == null || !requestingUserId.equals(alert.getUserId())) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Access denied. You can only send SOS for your own account."));
        }

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

        // 5. Get contacts ONLY for THIS user — never for any other userId
        List<EmergencyContact> contacts =
            contactRepository.findByUserId(alert.getUserId());

        if (contacts.isEmpty()) {
            System.out.println("⚠️ No contacts found for user: " + alert.getUserId());
        } else {
            // 6. Send SMS to each of the user's own contacts ONLY
            for (EmergencyContact contact : contacts) {
                if (smsService != null) {
                    try {
                        // Format phone number to E.164 (Indian default: +91)
                        String phone = contact.getPhone()
                            .replaceAll("[\\s\\-()]+", "");
                        if (!phone.startsWith("+")) {
                            phone = "+91" + phone
                                .replaceAll("^0", "")
                                .replaceAll("^91(?=\\d{10})", "");
                        }
                        smsService.sendSOS(phone, message);
                        System.out.println("✅ SMS sent to: "
                            + contact.getName() + " (" + phone + ")");
                    } catch (Exception e) {
                        System.err.println("❌ SMS failed for "
                            + contact.getName() + ": " + e.getMessage());
                    }
                } else {
                    System.out.println("⚠️ SmsService not available — "
                        + "check TWILIO environment variables");
                }
            }
        }

        return ResponseEntity.ok(saved);
    }

    // ── Get alerts for requesting user only ───────────────────
    // A user can only see their own SOS history.
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getUserAlerts(
            @PathVariable Long userId,
            @RequestHeader(value = "requestingUserId", required = false) Long requestingUserId) {

        if (requestingUserId == null || !requestingUserId.equals(userId)) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Access denied. You can only view your own SOS history."));
        }

        return ResponseEntity.ok(sosRepository.findByUserId(userId));
    }

    // ── /all endpoint REMOVED from here ──────────────────────
    // Admin can view all SOS alerts via GET /api/admin/alerts (with adminKey header).
}