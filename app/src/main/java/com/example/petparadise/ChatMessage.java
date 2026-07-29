package com.example.petparadise;

public class ChatMessage {
    private int id;
    private String senderEmail;
    private String receiverEmail;
    private String message;
    private long timestamp;

    public ChatMessage(int id, String senderEmail, String receiverEmail, String message, long timestamp) {
        this.id = id;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public String getSenderEmail() { return senderEmail; }
    public String getReceiverEmail() { return receiverEmail; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
}
