package com.safeher.controller;
 
import com.safeher.model.SafeCheckin;
import com.safeher.repository.SafeCheckinRepository;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/checkin")
@CrossOrigin
public class SafeCheckinController {
 
    @Autowired
    private SafeCheckinRepository checkinRepository;
 
    // POST /api/checkin/safe
    // Called when user presses "I'm Safe" button
    // Body: { "userId": 1, "latitude": 21.07, "longitude": 86.50, "note": "Reached home" }
    @PostMapping("/safe")
    public SafeCheckin checkin(@RequestBody SafeCheckin checkin) {
        System.out.println("✅ SAFE CHECK-IN from User: " + checkin.getUserId());
        return checkinRepository.save(checkin);
    }
 
    // GET /api/checkin/history/{userId}
    // Get all check-ins for a user — used in dashboard
    @GetMapping("/history/{userId}")
    public List<SafeCheckin> getHistory(@PathVariable Long userId) {
        return checkinRepository.findByUserIdOrderByCheckedInAtDesc(userId);
    }
 
    // GET /api/checkin/all — for admin
    @GetMapping("/all")
    public List<SafeCheckin> getAll() {
        return checkinRepository.findAll();
    }
}
