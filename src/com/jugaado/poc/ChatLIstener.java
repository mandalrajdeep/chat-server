package com.jugaado.poc;

import org.jivesoftware.smack.XMPPException;

import com.jugaado.chat.XmppManager;
import com.jugaado.chat.util.Config;

public class ChatLIstener {
    private static XmppManager manager;

    public static void main(String[] args) throws XMPPException,
            InterruptedException {
        setupChatManager();
        manager.sendMessage("Peacock", "asdf@openfire");
        Thread.sleep(50000);
    }

    private static void setupChatManager() throws XMPPException {
        manager =
                XmppManager.getInstance(Config.SERVER,
                    Integer.parseInt(Config.PORT));
        manager.init();
        manager.performLogin(Config.USERNAME, Config.PASSWORD);
    }
}
