package com.safeher.repository;
 
import com.safeher.model.SafeCheckin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
 
public interface SafeCheckinRepository extends JpaRepository<SafeCheckin, Long> {
    List<SafeCheckin> findByUserIdOrderByCheckedInAtDesc(Long userId);
}
