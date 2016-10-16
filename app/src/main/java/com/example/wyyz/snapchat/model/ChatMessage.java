package com.example.wyyz.snapchat.model;


public class ChatMessage implements Comparable{
    private long currentTime;
    private String sender;
    private String message;
    private FileModel file;
    private int messageStatus;

    public ChatMessage() {
        // empty default constructor, necessary for Firebase
    }

    public ChatMessage(long currentTime, String sender, String message, int messageStatus, FileModel file) {
        this.currentTime = currentTime;
        this.sender = sender;
        this.message = message;
        this.messageStatus = messageStatus;
        this.file = file;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public FileModel getFile() {
        return file;
    }

    public void setFile(FileModel file) {
        this.file = file;
    }
    @Override
    public boolean equals(Object o) {
        ChatMessage object = (ChatMessage) o;
        if (object.getCurrentTime() == this.getCurrentTime()) {
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(Object another) {
        ChatMessage chatMessage = (ChatMessage) another;

        /**
         * ascending order
         */
        return (int) (this.getCurrentTime() - chatMessage.getCurrentTime());

        /**
         * descending order
         */
        /*return (int) (chatMessage.getCurrentTime() - this.getCurrentTime());*/
    }
}