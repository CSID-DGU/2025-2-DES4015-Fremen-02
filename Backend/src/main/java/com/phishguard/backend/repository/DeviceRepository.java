package com.phishguard.backend.repository;

import com.phishguard.backend.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    // select * from device where uuid = ?
    Optional<Device> findByUuid(String uuid);
}
