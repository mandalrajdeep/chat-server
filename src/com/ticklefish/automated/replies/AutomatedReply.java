package com.ticklefish.automated.replies;

import java.io.FileNotFoundException;

import org.jivesoftware.smack.XMPPException;

import com.jugaado.mapping.ChatRouter;

public class AutomatedReply {
    private InMemoryQueryReplies inMemoryQueryReplies;

    public AutomatedReply() throws FileNotFoundException {
        this.inMemoryQueryReplies = new PythonQueryReply();
    }

    public boolean intercept(String user, String query) throws XMPPException {
        String reply = this.inMemoryQueryReplies.getReply(query);
        String executiveNotification = "Sending autoreply:" + reply;
        String executive = ChatRouter.getExecutive(user);
        ChatRouter.sendMessageFrom(user, executive, executiveNotification);
        ChatRouter.sendMessageFrom(executive, user, reply);
        return true;
    }

}
