package com.safeher.controller;

import com.safeher.model.EmergencyContact;
import com.safeher.repository.EmergencyContactRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin("*")
public class EmergencyContactController {

    @Autowired
    private EmergencyContactRepository repository;

    // ── Add contact ───────────────────────────────────────────
    @PostMapping("/add")
    public EmergencyContact addContact(@RequestBody EmergencyContact contact) {
        return repository.save(contact);
    }

    // ── Get contacts for ONE specific user only ───────────────
    // This is what the user's contacts.html calls
    // /api/contact/user/1 returns ONLY user 1's contacts
    @GetMapping("/user/{userId}")
    public List<EmergencyContact> getContactsByUser(@PathVariable Long userId) {
        return repository.findByUserId(userId);
    }

    // ── Get ALL contacts (admin only) ─────────────────────────
    @GetMapping("/all")
    public List<EmergencyContact> getAllContacts() {
        return repository.findAll();
    }

    // ── Delete contact ────────────────────────────────────────
    @DeleteMapping("/delete/{id}")
    public void deleteContact(@PathVariable Long id) {
        repository.deleteById(id);
    }
}