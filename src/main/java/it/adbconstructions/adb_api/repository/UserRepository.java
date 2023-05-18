package it.adbconstructions.adb_api.repository;


import it.adbconstructions.adb_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUsername(String username);

    User findUserByEmail(String email);
    @Query("SELECT u.username FROM User u WHERE u.role = 'ROLE_CONSULTANT'")
    List<String> findConsultantUsernames();
}
