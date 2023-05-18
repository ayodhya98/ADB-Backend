package it.adbconstructions.adb_api.repository;

import it.adbconstructions.adb_api.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request,Long> {

    @Query("SELECT r FROM Request r JOIN r.requestedUser u WHERE u.username = :username AND r.isFinished = false")
    List<Request> findByConsumerUsername(@Param("username") String username);

    Request findByRequestCode(String requestCode);

    @Query("SELECT r FROM Request r WHERE r.requestedUser.username = :username")
    List<Request> findByRequestedUserUsername(@Param("username") String username);
}
