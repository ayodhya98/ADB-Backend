package it.adbconstructions.adb_api.service;

import it.adbconstructions.adb_api.exception.RoomNotFoundException;
import it.adbconstructions.adb_api.model.Message;
import it.adbconstructions.adb_api.model.Request;
import it.adbconstructions.adb_api.model.Room;
import it.adbconstructions.adb_api.repository.RequestRepository;
import it.adbconstructions.adb_api.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import static it.adbconstructions.adb_api.common.constant.ErrorUtil.*;

@Service
public class ChatRoomService {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RequestRepository requestRepository;

    public Room createRoom(String consumerUsername, String consultantUsername) {
        Room room = new Room();
        room.setRoomCode(generateCode(consumerUsername,consultantUsername));
        room.setConsumerUsername(consumerUsername);
        room.setConsultantUsername(consultantUsername);
        return roomRepository.save(room);
    }

    public Room findByRoomCode(String roomCode) {
        return roomRepository.findByRoomCode(roomCode);
    }

    public List<Message> findMessagesByRoom(Room room) {
        return room.getMessages();
    }

    public void addMessageToRoom(Room room, String messageText) {
        Message message = new Message();
        message.setMessageText(messageText);
        message.setRoom(room);
        room.getMessages().add(message);
        roomRepository.save(room);
    }

    public List<Room> findRoomsByConsultantUsername(String consultantUsername) {
        return roomRepository.findByConsultantUsername(consultantUsername);
    }
    public String generateCode(String str1, String str2) {
        Random rand = new Random();
        int randNum = rand.nextInt(10000); // Generate a random 4-digit number
        String numStr = String.format("%04d", randNum); // Convert the number to a 4-digit string with leading zeros

        String str1Prefix = str1.substring(0, Math.min(str1.length(), 2)); // Get the first two characters of the first string
        String str2Prefix = str2.substring(0, Math.min(str2.length(), 2)); // Get the first two characters of the second string

        return numStr.substring(0, 2) + str1Prefix + numStr.substring(2, 4) + str2Prefix;
    }


    public void deleteRoom(String consumerUsername) throws RoomNotFoundException {
        Room room = roomRepository.findByConsumerUsername(consumerUsername);
        if (room != null)
        {
            List<Request> activeRequests = requestRepository.findByConsumerUsername(consumerUsername);
            for(Request r: activeRequests)
            {
                r.setFinished(true);
            }
            requestRepository.saveAll(activeRequests);
            roomRepository.delete(room);
        }
        else
        {
            throw new RoomNotFoundException(ROOM_NOT_FOUND_BY_USERNAME);
        }
    }

    public Room findByConsumerUsername(String consumerUsername) throws RoomNotFoundException {
        Room room = roomRepository.findByConsumerUsername(consumerUsername);
        if (room != null)
        {
            return room;
        }
        else
        {
            throw new RoomNotFoundException(ROOM_NOT_FOUND_BY_USERNAME);
        }
    }
}

