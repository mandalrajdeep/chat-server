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
    private Map<String, String> customerExecutiveMap = new HashMap<String, String>();
    PriorityQueue<CustomerExecutive> executivesAvailibityQueue = new PriorityQueue<>();
    List<CustomerExecutive> list = new LinkedList<CustomerExecutive>();

	private Map<String,CustomerExecutive> usernameToExecutiveMap = new HashMap<String, CustomerExecutive>();
	
	private AMQConnection amqBroker = new AMQConnection();
    
    public XmppManager(String server, int port) {
        this.server = server;
        this.port = port;
    }
    
    public static synchronized XmppManager getInstance(String server, int port) {
    	if(instance == null) {
    		instance = new XmppManager(server, port);
    	}
    	return instance;
    }
    


	public void createAccount(String username, String password){
    	if(accountManager == null){
    		accountManager = new AccountManager(connection);
    	}
    	try {
			accountManager.createAccount(username, password);
			System.out.println("Account created with username :"+username);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
    }
    
    public void init() throws XMPPException {
        
        System.out.println(String.format("Initializing connection to server %1$s port %2$d", server, port));

        SmackConfiguration.setPacketReplyTimeout(packetReplyTimeout);
        
        config = new ConnectionConfiguration(server, port);
        config.setSASLAuthenticationEnabled(false);
        config.setSecurityMode(SecurityMode.disabled);
        config.setSendPresence(true);
        
        connection = new XMPPConnection(config);
        connection.connect();
        
        System.out.println("Connected: " + connection.isConnected());
        
        chatManager = connection.getChatManager();
        messageListener = new MyMessageListener();
        
        ChatManagerListener chatManagerListener = new ChatManagerListener() {

			@Override
			public void chatCreated(Chat chat, boolean arg1) {
				chat.addMessageListener(messageListener);
			}
    		
    	};
    	chatManager.addChatListener(chatManagerListener);
        
    }
    
    public void performLogin(String username, String password) throws XMPPException {
        if (connection!=null && connection.isConnected()) {
			//connection.login(username, password);
            try {
            	//performLogout();
				connection.login(username, password);
			} catch (XMPPException e) {
				System.out.println("Login Failed. Creating account...");
				createAccount(username, password);
				connection.login(username, password);
				System.out.println("Login Succeeded after creation of account!");
			}
            Roster roster = connection.getRoster();
            Collection<RosterEntry> collection = roster.getEntries();
            
            for (RosterEntry rosterEntry : collection) {
            	
            	CustomerExecutive ce = new CustomerExecutive(rosterEntry.getUser());
            	usernameToExecutiveMap.put(rosterEntry.getUser(), ce);
            	executivesAvailibityQueue.add(ce);
            	list.add(ce);
            }
        }
        
    }
    
    public void performLogout() {
        if (connection!=null && connection.isConnected()) {
            connection.disconnect();
            connection = new XMPPConnection(config);
            try {
				connection.connect();
			} catch (XMPPException e) {
				e.printStackTrace();
			}
        }
    }

    public void setStatus(boolean available, String status) {
        
        Presence.Type type = available? Type.available: Type.unavailable;
        Presence presence = new Presence(type);
        
        presence.setStatus(status);
        connection.sendPacket(presence);
        
    }
    
    public void destroy() {
        if (connection!=null && connection.isConnected()) {
            connection.disconnect();
        }
    }
    
    public void sendMessage(String message, String buddyJID) throws XMPPException {
        System.out.println(String.format("Sending mesage '%1$s' to user %2$s", message, buddyJID));
        Chat chat = chatManager.createChat(buddyJID, messageListener);
        chat.sendMessage(message);
    }
    
    public void sendMessage(Message message) throws XMPPException {
        System.out.println(String.format("Sending mesage '%1$s' to user %2$s", 
        		message.getBody(), message.getTo()));
        Chat chat = chatManager.createChat(message.getTo(), messageListener);
        chat.sendMessage(message.getBody());
    }
    
    public void createEntry(String user, String name) throws Exception {
        System.out.println(String.format("Creating entry for buddy '%1$s' with name %2$s", user, name));
        Roster roster = connection.getRoster();
        roster.createEntry(user, name, null);
    }
    
    class MyMessageListener implements MessageListener {

        @Override
        public void processMessage(Chat chat, Message message) {
        	System.out.println("#####################################");
            String from = message.getFrom();
            String body = message.getBody();
            String to = message.getTo();
            
            System.out.println(body);
            System.out.println("From=" + from);
            System.out.println("To=" + to);
            Date date = new Date();
            System.out.println(date);
            
            if(!checkIfFromExecutive(from) && to.startsWith("master@")){ 
            	//TODO: Remove this check when we 
            	// have 2 separate chat servers (App and Backend)
            	String userName = from.split("@")[0];
        		String suffix = from.split("@")[1];
        		suffix = suffix.split("/")[0];
        		userName = userName + "_r2d2";
        		from = userName + "@"+ suffix;
            	try {
					amqBroker.setIncomingAppMessage(from, body);
				} catch (XMPPException e) {
					e.printStackTrace();
				}
            } else if(checkIfFromExecutive(from) && to.contains("_r2d2@")) {
            	String correctedTo = to.split("_r2d2")[0] + to.split("_r2d2")[1];
            	amqBroker.setOutgoingAppMesssage(correctedTo, body);
            }
        }

		private boolean checkIfFromExecutive(String from) {
			String slaves[] = {"slave01@", "slave02@", "slave03@", "slave04@", 
					"slave05@", "slave06@", "slave07@", "slave08@", "slave09@", 
					"slave10@", };
			for (String slave : slaves) {
				if(from.startsWith(slave)){
					return true;
				}
			}
			return false;
		}
        
    }

	public String getExecutive(String sentUser) {
		String user = sentUser;
		if(user.contains("{")){
			user = user.substring(1);
		}
		System.out.println("User to be mapped:"+user);
		Roster roster = connection.getRoster();
		roster.setSubscriptionMode(SubscriptionMode.accept_all);
		Collection<RosterEntry> collection = roster.getEntries();
		Presence presence;
		
		System.out.println("inProcessCustomers"+inProcessCustomers);
		if(inProcessCustomers.contains(user)){
			
			String executive = customerExecutiveMap.get(user);
			RosterEntry rEntry = roster.getEntry(executive);
			presence = roster.getPresence(rEntry.getUser());
			String status = presence.getType().name();
			
			//System.out.println(status);
			if(status.equals("available")){
				System.out.println("Already mapped getExecutive="+customerExecutiveMap.get(user));
				return customerExecutiveMap.get(user);
			}
			
		}
		
		for (RosterEntry rosterEntry : collection) {
			presence = roster.getPresence(rosterEntry.getUser());
			String status = presence.getType().name();
			usernameToExecutiveMap.get(rosterEntry.getUser()).setStatus(status);
		}
		
		Collections.sort(list);
		System.out.println(list);
		CustomerExecutive executive = list.get(0);
		//System.out.println(executive);
		executive.setCustomersCount(executive.getCustomersCount()+1);
		customerExecutiveMap.put(user, executive.getUsername());
		inProcessCustomers.add(user);
		
		System.out.println("getExecutive returns:"+executive.getUsername());
		return executive.getUsername();
	}
    
}