package it.adbconstructions.adb_api.service;

import it.adbconstructions.adb_api.model.Message;
import it.adbconstructions.adb_api.model.Room;
import it.adbconstructions.adb_api.repository.MessageRepository;
import it.adbconstructions.adb_api.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class MessageService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MessageRepository messageRepository;

    public void addMessageToRoom(String roomCode, String messageText, String author) {
        Room room = roomRepository.findByRoomCode(roomCode);
        // Encrypt the message text
        String encryptedText = encrypt(messageText);
        Message message = new Message();
        message.setMessageText(encryptedText);
        message.setAuthor(author);
        message.setRoom(room);
        Message savedMsg = messageRepository.save(message);

        room.getMessages().add(savedMsg);
        roomRepository.save(room);
    }

    // Simple encryption algorithm using a Caesar cipher
    private String encrypt(String text) {
        int shift = 3; // Shift each character by 3 positions to the right
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char shifted = (char) (((c - 'a' + shift) % 26) + 'a');
                result.append(shifted);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}

