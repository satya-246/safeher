package com.safeher.model;

import jakarta.persistence.*;

// This table stores: phone number → Telegram Chat ID
// A contact registers themselves by messaging your bot: /register 9876543210
@Entity
@Table(name = "telegram_registrations")
public class TelegramRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String phone;      // contact's phone number e.g. "9876543210"

    private String chatId;     // their Telegram Chat ID e.g. "123456789"

    public TelegramRegistration() {}

    public TelegramRegistration(String phone, String chatId) {
        this.phone  = phone;
        this.chatId = chatId;
    }

    public Long getId()     { return id; }
    public String getPhone()  { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
}
