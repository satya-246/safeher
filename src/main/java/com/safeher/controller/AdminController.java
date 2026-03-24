package com.safeher.controller;

import com.safeher.model.User;
import com.safeher.model.SOSAlert;
import com.safeher.model.EmergencyContact;
import com.safeher.repository.UserRepository;
import com.safeher.repository.SOSRepository;
import com.safeher.repository.EmergencyContactRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    // Simple admin password — in a real project this would be in application.properties
    private static final String ADMIN_KEY = "safeher@admin2026";

    @Autowired private UserRepository userRepository;
    @Autowired private SOSRepository sosRepository;
    @Autowired private EmergencyContactRepository contactRepository;

    // Helper: check admin key from request header
    private boolean isAuthorized(String key) {
        return ADMIN_KEY.equals(key);
    }

    // View all users — requires adminKey header
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestHeader(value="adminKey", required=false) String key) {
        if (!isAuthorized(key))
            return ResponseEntity.status(403).body(Map.of("error", "Access denied. Invalid admin key."));
        return ResponseEntity.ok(userRepository.findAll());
    }

    // View all SOS alerts — requires adminKey header
    @GetMapping("/alerts")
    public ResponseEntity<?> getAlerts(@RequestHeader(value="adminKey", required=false) String key) {
        if (!isAuthorized(key))
            return ResponseEntity.status(403).body(Map.of("error", "Access denied. Invalid admin key."));
        return ResponseEntity.ok(sosRepository.findAll());
    }

    // View all contacts — requires adminKey header
    @GetMapping("/contacts")
    public ResponseEntity<?> getContacts(@RequestHeader(value="adminKey", required=false) String key) {
        if (!isAuthorized(key))
            return ResponseEntity.status(403).body(Map.of("error", "Access denied. Invalid admin key."));
        return ResponseEntity.ok(contactRepository.findAll());
    }
}
