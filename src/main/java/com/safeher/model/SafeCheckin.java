package com.safeher.model;
 
import jakarta.persistence.*;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "safe_checkins")
public class SafeCheckin {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    private Long userId;
    private double latitude;
    private double longitude;
    private String note;                 // e.g. "Reached home safely"
    private LocalDateTime checkedInAt;
 
    public SafeCheckin() {}
 
    public Long getId() { return id; }
 
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
 
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
 
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
 
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
 
    public LocalDateTime getCheckedInAt() { return checkedInAt; }
    public void setCheckedInAt(LocalDateTime checkedInAt) { this.checkedInAt = checkedInAt; }
 
    @PrePersist
    protected void onCreate() {
        checkedInAt = LocalDateTime.now();
    }
}
