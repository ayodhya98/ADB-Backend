package it.adbconstructions.adb_api.repository;

import it.adbconstructions.adb_api.model.Message;
import it.adbconstructions.adb_api.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRoom(Room room);
}

