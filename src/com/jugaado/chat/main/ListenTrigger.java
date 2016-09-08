package com.jugaado.chat.main;

import java.util.HashSet;
import java.util.Set;

import org.jivesoftware.smack.XMPPException;

import com.jugaado.chat.XmppManager;
import com.jugaado.chat.util.Config;

public class ListenTrigger {
    private static ListenTrigger listener;
    private static XmppManager listenerXmppManager = null;
    private final Set<String> receivedCustomersSet = new HashSet<>();

    public static synchronized ListenTrigger getInstance() {
        if (listener == null) {
            listener = new ListenTrigger();
        }
        return listener;
    }

    static {
        try {
            init();
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    public synchronized void trigger() {
        for (String string : this.receivedCustomersSet) {
            try {
                String userName = string.split("@")[0];
                listenerXmppManager.performLogin(userName, userName);
                wait(50);
            } catch (XMPPException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized static void init() throws XMPPException {
        if (listenerXmppManager == null) {
            listenerXmppManager =
                    new XmppManager(Config.SERVER,
                        Integer.parseInt(Config.PORT));
            try {
                listenerXmppManager.init();
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void add(String e) {
        this.receivedCustomersSet.add(e);
    }
}
