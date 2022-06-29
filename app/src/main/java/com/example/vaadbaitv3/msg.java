package com.example.vaadbaitv3;

import java.io.Serializable;

public class msg implements Serializable {

    private String name_sender;
    private String type_chat;
    private String text;



    @Override
    public String toString() {
        return "msg{" +
                "name_sender='" + name_sender + '\'' +
                ", type_chat='" + type_chat + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public String getName_sender() {
        return name_sender;
    }

    public void setName_sender(String name_sender) {
        this.name_sender = name_sender;
    }

    public String getType_chat() {
        return type_chat;
    }

    public void setType_chat(String type_chat) {
        this.type_chat = type_chat;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }



    public msg(String name_sender, String type_chat, String text) {
        this.name_sender = name_sender;
        this.type_chat = type_chat;
        this.text = text;

    }

    public msg() {
    }




}