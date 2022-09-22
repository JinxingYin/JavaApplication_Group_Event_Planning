package com.example.chatroom.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    public String name,image,email,token,id;
    public Integer option;
    public boolean addToGroup;
    public List<String> groups = new ArrayList<>();
    public User(){

    }
    public User(String name, Integer option) {
        this.name = name;
        this.option = option;
    }


}
