package com.safeher.repository;

import com.safeher.model.TelegramRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TelegramRegistrationRepository
        extends JpaRepository<TelegramRegistration, Long> {

    // Find chatId by phone number — used when SOS is triggered
    Optional<TelegramRegistration> findByPhone(String phone);
}
