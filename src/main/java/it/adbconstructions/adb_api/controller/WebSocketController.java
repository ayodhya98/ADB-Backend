package it.adbconstructions.adb_api.controller;

import it.adbconstructions.adb_api.exception.AlreadyActiveRequestFoundException;
import it.adbconstructions.adb_api.exception.ExceptionHandling;
import it.adbconstructions.adb_api.exception.RequestNotFoundException;
import it.adbconstructions.adb_api.exception.RoomNotFoundException;
import it.adbconstructions.adb_api.model.ChatMessage;
import it.adbconstructions.adb_api.model.HttpResponse;
import it.adbconstructions.adb_api.model.Message;
import it.adbconstructions.adb_api.model.Room;
import it.adbconstructions.adb_api.service.ChatRoomService;
import it.adbconstructions.adb_api.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WebSocketController extends ExceptionHandling {
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private MessageService messageService;

    @MessageMapping("/chat/{roomCode}")
    @SendTo("/topic/messages/{roomCode}")
    public ChatMessage send(@DestinationVariable String roomCode, ChatMessage chatMessage) {
        messageService.addMessageToRoom(roomCode,chatMessage.getMessageText(),chatMessage.getAuthor());
        return chatMessage;
    }

    @GetMapping("/messages/{roomCode}")
    public List<Message> getMessages(@PathVariable String roomCode)
    {
        Room room = chatRoomService.findByRoomCode(roomCode);
        List<Message> messages = room.getMessages();

        // Decrypt the message text
        for (Message message : messages) {
            String decryptedText = decrypt(message.getMessageText());
            message.setMessageText(decryptedText); // Set the decrypted text
        }

        return messages;
    }

    @GetMapping("/rooms/{consultantUsername}")
    public ResponseEntity<List<Room>> getConsultantRooms(@PathVariable String consultantUsername)
    {
        List<Room> rooms = chatRoomService.findRoomsByConsultantUsername(consultantUsername);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/chat/rooms/{consumerUsername}")
    public ResponseEntity<Room> getConsumerRoom(@PathVariable String consumerUsername) throws RoomNotFoundException {
        Room room = chatRoomService.findByConsumerUsername(consumerUsername);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @DeleteMapping("/rooms/delete/{username}")
    public ResponseEntity<HttpResponse> deleteRoom(@PathVariable("username") String username) throws RoomNotFoundException {
        chatRoomService.deleteRoom(username);
        return response(HttpStatus.OK, "Room successfully deleted");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }

    // Simple decryption algorithm for a Caesar cipher
    private String decrypt(String text) {
        int shift = 3; // Shift each character by 3 positions to the left
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char shifted = (char) (((c - 'a' - shift + 26) % 26) + 'a');
                result.append(shifted);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}

