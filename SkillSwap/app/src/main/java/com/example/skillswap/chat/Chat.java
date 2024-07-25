package com.example.skillswap.chat;

public class Chat {
    private String CreatedByUser;
    private String text;

    // Default constructor required for calls to DataSnapshot.getValue(Chat.class)
    public Chat() {
    }

    public Chat(String CreatedByUser, String text) {
        this.CreatedByUser = CreatedByUser;
        this.text = text;
    }

    public String getCreatedByUser() {
        return CreatedByUser;
    }

    public void setCreatedByUser(String CreatedByUser) {
        this.CreatedByUser = CreatedByUser;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
