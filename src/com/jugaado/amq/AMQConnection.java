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
import com.jugaado.chat.util.MyLogger;

public class AMQConnection {
    private static MyLogger logger = MyLogger.getLogger(AMQConnection.class);

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

    public AMQConnection getWhatsappInstance() {

        AMQConnection instance = null;
        return instance;
    }

    // We are using only the queues here
    private static final String incomingTopicURL = "jugaado";
    private static final String outgoinTopicURL = "outgoing_jugaado";
    private static final String incomingQueueURL = Config.AMQ_INCOMING_QUEUE;
    private static final String outgoingQueueURL = Config.AMQ_OUTGOING_QUEUE;

    public MessageProducer getProducer() {
        if (this.producer != null) {
            return this.producer;
        }
        try {
            // consumer = getSession().createConsumer(getIncomingTopic());
            this.producer = getSession().createProducer(getOutgoingTopic());
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return this.producer;
    }

    public MessageConsumer getConsumer() {
        if (this.consumer != null) {
            return this.consumer;
        }
        try {
            this.consumer = getSession().createConsumer(getIncomingTopic());
            // producer = getSession().createProducer(getOutgoingTopic());
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return this.consumer;
    }

    public MessageConsumer getInQueueConsumer() {
        if (this.queueInConsumer != null) {
            return this.queueInConsumer;
        }
        try {
            this.queueInConsumer =
                    getSession().createConsumer(getIncomingQueue());
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return this.queueInConsumer;
    }

    public MessageConsumer getOutQueueConsumer() {
        if (this.queueOutConsumer != null) {
            return this.queueOutConsumer;
        }
        try {
            this.queueOutConsumer =
                    getSession().createConsumer(getOutgoingQueue());
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return this.queueOutConsumer;
    }

    public MessageProducer getInQueueProducer() {
        if (this.queueInProducer != null) {
            return this.queueInProducer;
        }
        try {
            this.queueInProducer =
                    getSession().createProducer(getIncomingQueue());
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return this.queueInProducer;
    }

    public MessageProducer getOutQueueProducer() {
        if (this.queueOutProducer != null) {
            return this.queueOutProducer;
        }
        try {
            this.queueOutProducer =
                    getSession().createProducer(getOutgoingQueue());
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return this.queueOutProducer;
    }

    public AMQConnection() {
        startConnection();
    }

    public void startConnection() {
        try {
            getConnection().start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        if (this.connection != null) {
            return this.connection;
        }
        String brokerUserName = Config.AMQ_USER;
        String brokerPassword = Config.AMQ_PASSWORD;
        try {
            this.connection =
                    getConnectionFactory().createConnection(brokerUserName,
                        brokerPassword);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return this.connection;
    }

    public ConnectionFactory getConnectionFactory() {
        if (this.connectionFactory == null) {
            this.connectionFactory = new ActiveMQConnectionFactory(brokerURL);
        }
        return this.connectionFactory;
    }

    public Session getSession() {
        if (this.session != null) {
            return this.session;
        }
        try {
            this.session =
                    this.connection.createSession(false,
                        Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return this.session;
    }

    public Queue getIncomingQueue() {
        if (this.incomingQueue != null) {
            return this.incomingQueue;
        }
        try {
            this.incomingQueue = getSession().createQueue(incomingQueueURL);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return this.incomingQueue;
    }

    public Queue getOutgoingQueue() {
        if (this.outgoingQueue != null) {
            return this.outgoingQueue;
        }
        try {
            this.outgoingQueue = getSession().createQueue(outgoingQueueURL);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return this.outgoingQueue;
    }

    public Topic getIncomingTopic() {
        if (this.incomingTopic != null) {
            return this.incomingTopic;
        }
        try {
            this.incomingTopic = getSession().createTopic(incomingTopicURL);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return this.incomingTopic;
    }

    public Topic getOutgoingTopic() {
        if (this.outgoingTopic != null) {
            return this.outgoingTopic;
        }
        try {
            this.outgoingTopic = getSession().createTopic(outgoinTopicURL);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return this.outgoingTopic;
    }

    public void setIncomingAppMessage(String from, String body)
            throws XMPPException {
        String incomingMessage = prepareAMQText(from, body);

        logger.log("AMQConnection:Incoming message to AMQ:" + incomingMessage);

        ActiveMQTextMessage amqMessage = new ActiveMQTextMessage();
        try {
            amqMessage.setText(incomingMessage);
            getInQueueProducer().send(amqMessage);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        XmppManager chatManager =
                new XmppManager(Config.SERVER, Integer.parseInt(Config.PORT));
        chatManager.init();
        from = from.split("@")[0];
        try {
            chatManager.performLogin(from, from);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    private String prepareAMQText(String from, String body) {
        logger.log("AMQConnection:FROM=" + from);
        String incomingMessage = from + "|" + body;
        logger.log("AMQConnection:INCOMING_MESSAGE=" + incomingMessage);
        return incomingMessage;
    }

    public void setOutgoingAppMesssage(String correctedTo, String body) {
        String outgoingMessage = prepareAMQText(correctedTo, body);

        logger.log("AMQConnection:Outgoing message to AMQ:" + outgoingMessage);

        ActiveMQTextMessage amqMessage = new ActiveMQTextMessage();
        try {
            amqMessage.setText(outgoingMessage);
            getOutQueueProducer().send(amqMessage);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
