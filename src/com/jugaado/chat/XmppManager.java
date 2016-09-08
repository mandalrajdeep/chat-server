package com.jugaado.chat;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

import com.jugaado.amq.AMQConnection;

public class XmppManager {

    private static XmppManager instance = null;

    private static final int packetReplyTimeout = 2000; // millis

    private String server;
    private int port;

    private ConnectionConfiguration config;
    XMPPConnection connection;

    private ChatManager chatManager;
    private MessageListener messageListener;

    AccountManager accountManager;

    private Set<String> inProcessCustomers = new HashSet<>();
    private Map<String, String> customerExecutiveMap =
            new HashMap<String, String>();
    PriorityQueue<CustomerExecutive> executivesAvailibityQueue =
            new PriorityQueue<>();
    List<CustomerExecutive> list = new LinkedList<CustomerExecutive>();

    private Map<String, CustomerExecutive> usernameToExecutiveMap =
            new HashMap<String, CustomerExecutive>();

    private AMQConnection amqBroker = new AMQConnection();

    public XmppManager(String server, int port) {
        this.server = server;
        this.port = port;
    }

    public static synchronized XmppManager getInstance(String server, int port) {
        if (instance == null) {
            instance = new XmppManager(server, port);
        }
        return instance;
    }

    public void createAccount(String username, String password) {
        if (this.accountManager == null) {
            this.accountManager = new AccountManager(this.connection);
        }
        try {
            this.accountManager.createAccount(username, password);
            System.out.println("XmppManager:Account created with username :"
                    + username);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    public void init() throws XMPPException {

        System.out.println(String.format(
            "XmppManager:Initializing connection to server %1$s port %2$d",
            this.server, this.port));

        SmackConfiguration.setPacketReplyTimeout(packetReplyTimeout);

        this.config = new ConnectionConfiguration(this.server, this.port);
        this.config.setSASLAuthenticationEnabled(false);
        this.config.setSecurityMode(SecurityMode.disabled);
        this.config.setSendPresence(true);

        this.connection = new XMPPConnection(this.config);
        this.connection.connect();

        System.out.println("XmppManager:Connected: "
                + this.connection.isConnected());

        this.chatManager = this.connection.getChatManager();
        this.messageListener = new MyMessageListener();

        ChatManagerListener chatManagerListener = new ChatManagerListener() {

            @Override
            public void chatCreated(Chat chat, boolean arg1) {
                chat.addMessageListener(XmppManager.this.messageListener);
            }

        };
        this.chatManager.addChatListener(chatManagerListener);

    }

    public void performLogin(String username, String password)
            throws XMPPException {
        if (this.connection != null && this.connection.isConnected()) {
            // connection.login(username, password);
            try {
                // performLogout();
                this.connection.login(username, password);
            } catch (XMPPException e) {
                System.out
                    .println("XmppManager:Login Failed. Creating account...");
                createAccount(username, password);
                this.connection.login(username, password);
                System.out
                    .println("XmppManager:Login Succeeded after creation of account!");
            }
            Roster roster = this.connection.getRoster();
            Collection<RosterEntry> collection = roster.getEntries();

            for (RosterEntry rosterEntry : collection) {

                CustomerExecutive ce =
                        new CustomerExecutive(rosterEntry.getUser());
                this.usernameToExecutiveMap.put(rosterEntry.getUser(), ce);
                this.executivesAvailibityQueue.add(ce);
                this.list.add(ce);
                System.out.println("gghggh");
            }
        }

    }

    public void performLogout() {
        if (this.connection != null && this.connection.isConnected()) {
            this.connection.disconnect();
            this.connection = new XMPPConnection(this.config);
            try {
                this.connection.connect();
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
    }

    public void setStatus(boolean available, String status) {

        Presence.Type type = available ? Type.available : Type.unavailable;
        Presence presence = new Presence(type);

        presence.setStatus(status);
        this.connection.sendPacket(presence);

    }

    public void destroy() {
        if (this.connection != null && this.connection.isConnected()) {
            this.connection.disconnect();
        }
    }

    public void sendMessage(String message, String buddyJID)
            throws XMPPException {
        System.out.println(String
            .format("XmppManager:Sending mesage '%1$s' to user %2$s", message,
                buddyJID));
        Chat chat = this.chatManager.createChat(buddyJID, this.messageListener);
        chat.sendMessage(message);
    }

    public void sendMessage(Message message) throws XMPPException {
        System.out.println(String.format(
            "XmppManager:Sending mesage '%1$s' to user %2$s",
            message.getBody(), message.getTo()));
        Chat chat =
                this.chatManager.createChat(message.getTo(),
                    this.messageListener);
        chat.sendMessage(message.getBody());
    }

    public void createEntry(String user, String name) throws Exception {
        System.out.println(String.format(
            "XmppManager:Creating entry for buddy '%1$s' with name %2$s", user,
            name));
        Roster roster = this.connection.getRoster();
        roster.createEntry(user, name, null);
    }

    class MyMessageListener implements MessageListener {

        @Override
        public void processMessage(Chat chat, Message message) {
            System.out
                .println("XmppManager:##################################### I am here bro");
            String from = message.getFrom();
            String body = message.getBody();
            String to = message.getTo();

            if (body.contains("get set go meritnation:")) // && from ==
                                                          // ("guest"))//s
            {
                String browserid = body.split(":")[1];
                System.out.println("yo meritnation - " + browserid);
                createAccount(browserid, browserid);
                String browseriduserName = browserid + "_r2d2";
                createAccount(browseriduserName, browseriduserName);

            }
            // else{//soma{

            System.out.println("XmppManager:Body=" + body);
            System.out.println("XmppManager:From=" + from);
            System.out.println("XmppManager:To=" + to);
            new Date();

            if (!checkIfFromExecutive(from) && to.startsWith("master@")) {
                // TODO: Remove this check when we
                // have 2 separate chat servers (App and Backend)
                String userName = from.split("@")[0];
                String suffix = from.split("@")[1];
                suffix = suffix.split("/")[0];
                userName = userName + "_r2d2";
                from = userName + "@" + suffix;
                try {
                    XmppManager.this.amqBroker
                        .setIncomingAppMessage(from, body);
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            } else if (checkIfFromExecutive(from) && to.contains("_r2d2@")) {
                String correctedTo =
                        to.split("_r2d2")[0] + to.split("_r2d2")[1];
                XmppManager.this.amqBroker.setOutgoingAppMesssage(correctedTo,
                    body);
            }
            // }//end else soma
        }

        private boolean checkIfFromExecutive(String from) {
            String slaves[] =
                    {"slave01@", "slave02@", "slave03@", "slave04@",
                            "slave05@", "slave06@", "slave07@", "slave08@",
                            "slave09@", "slave10@",};
            for (String slave : slaves) {
                if (from.startsWith(slave)) {
                    return true;
                }
            }
            return false;
        }

    }

    public String getExecutive(String sentUser) {
        String user = sentUser;
        if (user.contains("{")) {
            user = user.substring(1);
        }
        System.out.println("XmppManager:User to be mapped:" + user);
        Roster roster = this.connection.getRoster();
        roster.setSubscriptionMode(SubscriptionMode.accept_all);
        Collection<RosterEntry> collection = roster.getEntries();
        Presence presence;

        System.out.println("XmppManager:inProcessCustomers"
                + this.inProcessCustomers);
        if (this.inProcessCustomers.contains(user)) {

            String executive = this.customerExecutiveMap.get(user);
            RosterEntry rEntry = roster.getEntry(executive);
            presence = roster.getPresence(rEntry.getUser());
            String status = presence.getType().name();

            if (status.equals("available")) {
                System.out.println("XmppManager:Already mapped getExecutive="
                        + this.customerExecutiveMap.get(user));
                return this.customerExecutiveMap.get(user);
            }

        }

        for (RosterEntry rosterEntry : collection) {
            presence = roster.getPresence(rosterEntry.getUser());
            String status = presence.getType().name();
            this.usernameToExecutiveMap.get(rosterEntry.getUser()).setStatus(
                status);
        }

        Collections.sort(this.list);
        System.out.println("XmppManager:Roster" + this.list);
        CustomerExecutive executive = this.list.get(0);
        executive.setCustomersCount(executive.getCustomersCount() + 1);
        this.customerExecutiveMap.put(user, executive.getUsername());
        this.inProcessCustomers.add(user);

        System.out.println("XmppManager:getExecutive returns:"
                + executive.getUsername());
        return executive.getUsername();
    }

}