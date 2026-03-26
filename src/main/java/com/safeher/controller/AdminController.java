package com.safeher.controller;

import com.safeher.repository.UserRepository;
import com.safeher.repository.SOSRepository;
import com.safeher.repository.EmergencyContactRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    // Admin password — move this to application.properties / environment variable in production
    private static final String ADMIN_KEY = "safeher@admin2026";

    @Autowired private UserRepository userRepository;
    @Autowired private SOSRepository sosRepository;
    @Autowired private EmergencyContactRepository contactRepository;

    private boolean isAuthorized(String key) {
        return ADMIN_KEY.equals(key);
    }

    // ── View ALL users ────────────────────────────────────────
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestHeader(value = "adminKey", required = false) String key) {
        if (!isAuthorized(key))
            return ResponseEntity.status(403).body(Map.of("error", "Access denied. Invalid admin key."));
        return ResponseEntity.ok(userRepository.findAll());
    }

    // ── View ALL SOS alerts (across all users) ────────────────
    @GetMapping("/alerts")
    public ResponseEntity<?> getAlerts(
            @RequestHeader(value = "adminKey", required = false) String key) {
        if (!isAuthorized(key))
            return ResponseEntity.status(403).body(Map.of("error", "Access denied. Invalid admin key."));
        return ResponseEntity.ok(sosRepository.findAll());
    }

    // ── View ALL emergency contacts (across all users) ─────────
    @GetMapping("/contacts")
    public ResponseEntity<?> getContacts(
            @RequestHeader(value = "adminKey", required = false) String key) {
        if (!isAuthorized(key))
            return ResponseEntity.status(403).body(Map.of("error", "Access denied. Invalid admin key."));
        return ResponseEntity.ok(contactRepository.findAll());
    }

    // ── Admin delete any contact by ID ────────────────────────
    // Separate from /api/contact/delete/{id} which requires the contact owner's userId.
    // This route is protected by adminKey only — regular users cannot reach it.
    @DeleteMapping("/contact/delete/{id}")
    public ResponseEntity<?> deleteContact(
            @PathVariable Long id,
            @RequestHeader(value = "adminKey", required = false) String key) {
        if (!isAuthorized(key))
            return ResponseEntity.status(403).body(Map.of("error", "Access denied. Invalid admin key."));
        if (!contactRepository.existsById(id))
            return ResponseEntity.status(404).body(Map.of("error", "Contact not found."));
        contactRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Contact deleted by admin."));
    }
}