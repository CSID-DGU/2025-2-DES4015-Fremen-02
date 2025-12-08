package com.phishguard.backend.repository;

import com.phishguard.backend.domain.SmsLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsLogRepository extends JpaRepository<SmsLog, Long> {
    // JpaRepository가 save(), findAll() 등을 다 만들어줌
}
