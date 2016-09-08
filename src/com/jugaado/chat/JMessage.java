package com.jugaado.chat;

import java.util.Date;

import org.jivesoftware.smack.packet.Message;

public class JMessage {

    private Message message;
    private Date date;
    private String from;
    private String body;

    public JMessage(Message message, Date date) {
        this.message = message;
        this.body = message.getBody();
        this.from = message.getFrom();
        this.date = date;
    }

    public JMessage(String body, String from) {
        this.body = body;
        this.from = from;
    }

    public Message getMessage() {
        return this.message;
    }

    public String getMessageBody() {
        return this.body;
    }

    public void setBody(Message body) {
        this.message = body;
        this.body = body.getBody();
    }

    public void setMessageBody(String body) {
        this.body = body;
    }

    public String getFrom() {
        return this.from;
    }

    public Date getDate() {
        return this.date;
    }

}
