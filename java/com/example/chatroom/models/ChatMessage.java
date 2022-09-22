package com.example.chatroom.models;

import java.util.Date;
import java.util.List;

public class ChatMessage {
    public String senderId, receiverId, message, dateTime,eventName;
    public String senderName;
    public List<String> receivers;
    public Date dateObject;
    public String conversionId, conversionName,conversionImage;
}
