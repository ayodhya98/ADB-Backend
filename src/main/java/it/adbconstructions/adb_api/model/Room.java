package it.adbconstructions.adb_api.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Room implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String roomCode;

    private String consumerUsername;
    private String consultantUsername;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;

    // getters and setters

    public Room(Long id, String roomCode, String consumerUsername, String consultantUsername, List<Message> messages) {
        this.id = id;
        this.roomCode = roomCode;
        this.consumerUsername = consumerUsername;
        this.consultantUsername = consultantUsername;
        this.messages = messages;
    }

    public Room() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getConsumerUsername() {
        return consumerUsername;
    }

    public void setConsumerUsername(String consumerUsername) {
        this.consumerUsername = consumerUsername;
    }

    public String getConsultantUsername() {
        return consultantUsername;
    }

    public void setConsultantUsername(String consultantUsername) {
        this.consultantUsername = consultantUsername;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}



