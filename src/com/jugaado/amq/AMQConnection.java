package com.jugaado.amq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.jivesoftware.smack.XMPPException;

import com.jugaado.chat.XmppManager;
import com.jugaado.chat.util.Config;

public class AMQConnection {
	private static final String brokerURL = Config.AMQ_SERVER;
	private ConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private MessageProducer producer;
	private MessageConsumer consumer;
	private MessageProducer queueInProducer;
	private MessageProducer queueOutProducer;
	private MessageConsumer queueInConsumer;
	private MessageConsumer queueOutConsumer;
	private Topic incomingTopic;
	private Topic outgoingTopic;
	private Queue incomingQueue;
	private Queue outgoingQueue;
	
	public AMQConnection getWhatsappInstance(){
		
		AMQConnection instance = null;
		return instance;
	}
	
	// We are using only the queues here
	private static final String incomingTopicURL = "jugaado";
	private static final String outgoinTopicURL = "outgoing_jugaado";
	private static final String incomingQueueURL = Config.AMQ_INCOMING_QUEUE;
	private static final String outgoingQueueURL = Config.AMQ_OUTGOING_QUEUE;
	
	public MessageProducer getProducer() {
		if(producer!=null){
			return producer;
		}
		try {
			//consumer = getSession().createConsumer(getIncomingTopic());
			producer = getSession().createProducer(getOutgoingTopic());
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return producer;
	}

	public MessageConsumer getConsumer() {
		if(consumer!=null){
			return consumer;
		}
		try {
			consumer = getSession().createConsumer(getIncomingTopic());
			//producer = getSession().createProducer(getOutgoingTopic());
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return consumer;
	}
	
	public MessageConsumer getInQueueConsumer() {
		if(queueInConsumer!=null){
			return queueInConsumer;
		}
		try {
			queueInConsumer = getSession().createConsumer(getIncomingQueue());
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return queueInConsumer;
	}
	
	public MessageConsumer getOutQueueConsumer() {
		if(queueOutConsumer!=null){
			return queueOutConsumer;
		}
		try {
			queueOutConsumer = getSession().createConsumer(getOutgoingQueue());
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return queueOutConsumer;
	}
	
	public MessageProducer getInQueueProducer() {
		if(queueInProducer!=null){
			return queueInProducer;
		}
		try {
			queueInProducer = getSession().createProducer(getIncomingQueue());
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return queueInProducer;
	}
	
	public MessageProducer getOutQueueProducer() {
		if(queueOutProducer!=null){
			return queueOutProducer;
		}
		try {
			queueOutProducer = getSession().createProducer(getOutgoingQueue());
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return queueOutProducer;
	}


	
	public AMQConnection(){
		startConnection();
	}
	
	public void startConnection(){
		try {
			getConnection().start();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public Connection getConnection(){
		if(connection!=null){
			return connection;
		}
		String brokerUserName = Config.AMQ_USER;
		String brokerPassword = Config.AMQ_PASSWORD;
		try {
			connection = getConnectionFactory().createConnection(brokerUserName, brokerPassword);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	public ConnectionFactory getConnectionFactory() {
		if(connectionFactory==null){
			connectionFactory = new ActiveMQConnectionFactory(brokerURL);
		}
		return connectionFactory;
	}
	
	public Session getSession(){
		if(session!=null){
			return session;
		}
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return session;
	}
	
	public Queue getIncomingQueue(){
		if(incomingQueue!=null){
			return incomingQueue;
		}
		try {
			incomingQueue = getSession().createQueue(incomingQueueURL);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return incomingQueue;
	}
	
	public Queue getOutgoingQueue(){
		if(outgoingQueue!=null){
			return outgoingQueue;
		}
		try {
			outgoingQueue = getSession().createQueue(outgoingQueueURL);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return outgoingQueue;
	}
	
	public Topic getIncomingTopic(){
		if(incomingTopic!=null){
			return incomingTopic;
		}
		try {
			incomingTopic = getSession().createTopic(incomingTopicURL);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return incomingTopic;
	}
	
	public Topic getOutgoingTopic(){
		if(outgoingTopic!=null){
			return outgoingTopic;
		}
		try {
			outgoingTopic = getSession().createTopic(outgoinTopicURL);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return outgoingTopic;
	}
	
	public void setIncomingAppMessage(String from, String body) throws XMPPException {
		String incomingMessage = prepareAMQText(from, body);
		
		System.out.println("AMQConnection:Incoming message to AMQ:"+incomingMessage);
		
		ActiveMQTextMessage amqMessage = new ActiveMQTextMessage();
		try {
			amqMessage.setText(incomingMessage);
			getInQueueProducer().send(amqMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		XmppManager chatManager = new XmppManager(Config.SERVER, Integer.parseInt(Config.PORT));
		chatManager.init();
		from = from.split("@")[0];
		try {
			chatManager.performLogin(from, from);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	private String prepareAMQText(String from, String body) {
		System.out.println("AMQConnection:FROM="+from);
		String incomingMessage = from +"|" + body;
		System.out.println("AMQConnection:INCOMING_MESSAGE="+incomingMessage);
		return incomingMessage;
	}

	public void setOutgoingAppMesssage(String correctedTo, String body) {
		String outgoingMessage = prepareAMQText(correctedTo, body);
		
		System.out.println("AMQConnection:Outgoing message to AMQ:"+outgoingMessage);
		
		ActiveMQTextMessage amqMessage = new ActiveMQTextMessage();
		try {
			amqMessage.setText(outgoingMessage);
			getOutQueueProducer().send(amqMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
