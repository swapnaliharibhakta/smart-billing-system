package com.company.invoice.repository;

import com.company.invoice.entity.CompanySettings;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SettingsRepository extends JpaRepository<CompanySettings, Long> {

    @Query("select s from CompanySettings s order by s.id asc limit 1")
    Optional<CompanySettings> findFirst();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from CompanySettings s order by s.id asc limit 1")
    Optional<CompanySettings> findFirstForUpdate();
}
