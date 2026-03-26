package com.safeher.repository;

import com.safeher.model.SOSAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SOSRepository extends JpaRepository<SOSAlert, Long> {

    // Used by SOSController to fetch only THIS user's alerts
    List<SOSAlert> findByUserId(Long userId);
}
