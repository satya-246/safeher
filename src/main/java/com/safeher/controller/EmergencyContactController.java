package com.safeher.controller;

import com.safeher.model.EmergencyContact;
import com.safeher.repository.EmergencyContactRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin("*")
public class EmergencyContactController {

    @Autowired
    private EmergencyContactRepository repository;

    // ── Add contact ───────────────────────────────────────────
    // The contact's userId must match the requestingUserId header.
    // A user cannot add a contact under someone else's account.
    @PostMapping("/add")
    public ResponseEntity<?> addContact(
            @RequestBody EmergencyContact contact,
            @RequestHeader(value = "requestingUserId", required = false) Long requestingUserId) {

        if (requestingUserId == null || !requestingUserId.equals(contact.getUserId())) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Access denied. You can only add contacts to your own account."));
        }

        return ResponseEntity.ok(repository.save(contact));
    }

    // ── Get contacts for the requesting user ONLY ─────────────
    // The userId in the path MUST match the requestingUserId header.
    // This prevents user A from fetching user B's contacts.
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getContactsByUser(
            @PathVariable Long userId,
            @RequestHeader(value = "requestingUserId", required = false) Long requestingUserId) {

        if (requestingUserId == null || !requestingUserId.equals(userId)) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Access denied. You can only view your own contacts."));
        }

        List<EmergencyContact> contacts = repository.findByUserId(userId);
        return ResponseEntity.ok(contacts);
    }

    // ── Delete contact ────────────────────────────────────────
    // Verifies the contact belongs to the requesting user before deleting.
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteContact(
            @PathVariable Long id,
            @RequestHeader(value = "requestingUserId", required = false) Long requestingUserId) {

        if (requestingUserId == null) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Access denied. requestingUserId header is required."));
        }

        // Fetch the contact first to verify ownership
        return repository.findById(id).map(contact -> {
            if (!contact.getUserId().equals(requestingUserId)) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "Access denied. You can only delete your own contacts."));
            }
            repository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Contact deleted successfully."));
        }).orElse(ResponseEntity.status(404)
                .body(Map.of("error", "Contact not found.")));
    }

    // ── NOTE: /all endpoint REMOVED from here ─────────────────
    // Admin can view all contacts via GET /api/admin/contacts (with adminKey header).
    // Regular users have NO endpoint to list all contacts.
}