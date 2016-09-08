package com.jugaado.chat;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.XMPPException;

import com.jugaado.chat.main.ListenTrigger;
import com.jugaado.chat.util.Config;
import com.jugaado.mapping.ChatRouter;
import com.ticklefish.automated.replies.AutomatedReply;

public class Task implements Runnable {
    private static final long TIMEOUT = Long.parseLong(Config.MESSAGE_TIMEOUT);
    private List<JMessage> jmessageList = new ArrayList<>();
    boolean isTimeOut;
    private String user;

    private AutomatedReply autoReply;

    public Task(String user) throws FileNotFoundException {
        this.user = user;
        this.isTimeOut = false;
        this.autoReply = new AutomatedReply();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(TIMEOUT);
            dispatchMessages();
        } catch (InterruptedException | XMPPException e) {
            e.printStackTrace();
        }
    }

    private synchronized void dispatchMessages() throws XMPPException {
        String consolidatedMessage = consolidateMessages();
        this.jmessageList = new ArrayList<JMessage>();
        System.out.println("Task:consolidatedMessage=" + consolidatedMessage);
        // Echo message
        // ChatRouter.sendMessage(user, consolidatedMessage);
        // Get executive and send
        String executive = ChatRouter.getExecutive(this.user);
        // ChatRouter.sendMessage(executive, consolidatedMessage);
        ChatRouter.sendMessageFrom(this.user, executive, consolidatedMessage);
        ListenTrigger.getInstance().add(this.user);

        this.autoReply.intercept(this.user, consolidatedMessage);
    }

    private String consolidateMessages() {
        StringBuilder sb = new StringBuilder();
        for (JMessage jMessage : this.jmessageList) {
            sb.append(jMessage.getMessageBody()).append(" ");
        }
        return sb.toString();
    }

    public synchronized void addMessage(JMessage message) {
        this.jmessageList.add(message);
    }

    public synchronized boolean isEmpty() {
        return this.jmessageList.isEmpty();
    }
}
