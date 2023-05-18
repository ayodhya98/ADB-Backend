package it.adbconstructions.adb_api.model;

public class ChatMessage {
    private String messageText;
    private String author;

    public ChatMessage() {
    }

    public ChatMessage(String messageText, String author) {
        this.messageText = messageText;
        this.author = author;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

