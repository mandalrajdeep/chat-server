package com.jugaado.chat.util;

import static com.jugaado.chat.util.ConfigProperties.getProperty;

public class Config {
	public static final String OWNER = getProperty("owner");
	public static final String SERVER = getProperty("server");
	public static final String PORT = getProperty("port");
	public static final String USERNAME = getProperty("username");
	public static final String PASSWORD = getProperty("password");
	public static final String MESSAGE_TIMEOUT = getProperty("messageTimeout");
	public static final String MESSAGE_THREADS = getProperty("messageThreads");
	public static final boolean AUTO_REPLY_ON = Boolean.valueOf(getProperty("autoReplyOn"));
	public static final String AMQ_SERVER = getProperty("amqServer");
	public static final String AMQ_USER = getProperty("amqUser");
	public static final String AMQ_PASSWORD = getProperty("amqPassword");
	public static final String AMQ_INCOMING_QUEUE = getProperty("amqIncomingQ");
	public static final String AMQ_OUTGOING_QUEUE = getProperty("amqOutgoingQ");
	public static final String AUTOMATED_REPLY_QUERY_PATH = getProperty("automatedReplyCallPath");
	public static final String AUTOMATED_RESPONSE_FILE = getProperty("automatedResponseFile");

}

