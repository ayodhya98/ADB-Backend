package it.adbconstructions.adb_api.repository;

import it.adbconstructions.adb_api.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

    Project findByProjectCode(String projectCode);

    List<Project> findByCategory(String category);
}
