package com.jugaado.mapping;

import org.jivesoftware.smack.XMPPException;

import com.jugaado.chat.XmppManager;
import com.jugaado.chat.main.MainApp;
import com.jugaado.chat.util.Config;

public class ChatRouter {

    private static XmppManager manager;

    public static String getExecutive(String user) throws XMPPException {
        if (manager == null) {
            manager = MainApp.getXmppManager();
        }
        return manager.getExecutive(user);
    }

    public static void sendMessage(String user, String message)
            throws XMPPException {
        if (manager == null) {
            manager = MainApp.getXmppManager();
        }
        manager.sendMessage(message, user);
    }

    public static void sendMessageFrom(String from, String to, String message)
            throws XMPPException {
        XmppManager chatManager =
                new XmppManager(Config.SERVER, Integer.parseInt(Config.PORT));
        chatManager.init();
        from = from.split("@")[0];
        chatManager.performLogin(from, from);
        chatManager.sendMessage(message, to);
    }

}
