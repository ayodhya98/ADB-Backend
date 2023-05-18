package it.adbconstructions.adb_api.repository;

import it.adbconstructions.adb_api.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Room findByRoomCode(String roomCode);

    @Query("SELECT r FROM Room r WHERE r.consultantUsername = :username")
    List<Room> findByConsultantUsername(@Param("username") String username);

    @Query("SELECT r FROM Room r WHERE r.consumerUsername = :username")
    Room findByConsumerUsername(@Param("username") String username);
}

