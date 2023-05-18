package it.adbconstructions.adb_api.repository;

import it.adbconstructions.adb_api.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findEmployeeByUsername(String username);

    Employee findEmployeeByEmail(String email);
    @Query("SELECT e.username FROM Employee e WHERE e.available = 'yes'")
    List<String> findUsernamesOfAvailableEmployees();
}
