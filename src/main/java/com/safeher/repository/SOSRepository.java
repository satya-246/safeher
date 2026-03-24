package com.safeher.repository;

import com.safeher.model.SOSAlert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SOSRepository extends JpaRepository<SOSAlert, Long> { }
