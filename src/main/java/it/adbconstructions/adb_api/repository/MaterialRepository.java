package it.adbconstructions.adb_api.repository;

import it.adbconstructions.adb_api.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material,Long> {
    Material findByCode(String code);
    Material findByName(String name);
}
