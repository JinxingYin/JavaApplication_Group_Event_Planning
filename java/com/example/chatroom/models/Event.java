package com.example.chatroom.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Event implements Serializable {
    public String date,time,description;
    public List<String> members = new ArrayList<>();
    public String name,message;
}
