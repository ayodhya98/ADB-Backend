package it.adbconstructions.adb_api.repository;

import it.adbconstructions.adb_api.model.ResetCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetCodeRepository extends JpaRepository<ResetCode,Long> {
    ResetCode findByCode(String code);
}
