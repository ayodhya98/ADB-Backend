package it.adbconstructions.adb_api.repository;

import it.adbconstructions.adb_api.model.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumerRepository extends JpaRepository<Consumer,Long> {
    Consumer findUserByUsername(String username);

    Consumer findUserByEmail(String email);
}
