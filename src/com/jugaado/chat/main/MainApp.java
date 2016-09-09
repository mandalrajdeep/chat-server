package com.jugaado.chat.main;

import java.io.FileNotFoundException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.jivesoftware.smack.XMPPException;

import com.jugaado.amq.AMQConnection;
import com.jugaado.chat.JMessage;
import com.jugaado.chat.TaskManager;
import com.jugaado.chat.XmppManager;
import com.jugaado.chat.util.Config;
import com.jugaado.chat.util.MyLogger;
import com.jugaado.mapping.ChatRouter;

public class MainApp {

    private static MyLogger logger = MyLogger.getLogger(MainApp.class);

    private static XmppManager manager;
    private static AMQConnection amqBroker = new AMQConnection();
    private static ProducerThread producerThread = new ProducerThread();

    public static void main(String[] args) throws XMPPException,
            FileNotFoundException, JMSException {
        setupChatManager();// called once when the program starts
        producerThread.start();
        processIncomingMessages();
        processOutgoingMessages();
    }

    private static void processOutgoingMessages() throws JMSException {
        logger.log("*********I am in Outgoing Messages*********");
        amqBroker.getOutQueueConsumer().setMessageListener(
            new OutQueueListener());
    }

    private static void processIncomingMessages() throws JMSException,
            FileNotFoundException {
        logger.log("*********I am in Incoming Messages*********");
        amqBroker.getInQueueConsumer()
            .setMessageListener(new InQueueListener());
    }

    private static void setupChatManager() throws XMPPException {
        logger.log("*********I am in setupChatManager*********");
        manager =
                XmppManager.getInstance(Config.SERVER,
                    Integer.parseInt(Config.PORT));
        manager.init();
        manager.performLogin(Config.USERNAME, Config.PASSWORD);
        logger.log("*********I am out of setupChatManager*********");
    }

    public static XmppManager getXmppManager() throws XMPPException {
        if (manager != null) {
            return manager;
        }
        setupChatManager();
        return manager;
    }

    static class InQueueListener implements MessageListener {

        @Override
        public void onMessage(Message msg) {
            ActiveMQTextMessage textMessage = (ActiveMQTextMessage) msg;

            if (textMessage != null) {
                String string = null;
                try {
                    string = textMessage.getText();
                    logger.log("MainApp:Text message received:" + string);
                    String userName = string.split("\\|")[0];
                    String body = string.split("\\|")[1];
                    logger.log("MainApp:User name=" + userName);
                    logger.log("MainApp:Body=" + body);
                    JMessage jMessage = new JMessage(body, userName);
                    TaskManager.addMessage(jMessage);
                } catch (JMSException | FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    static class OutQueueListener implements MessageListener {

        @Override
        public void onMessage(Message msg) {
            ActiveMQTextMessage textMessage = (ActiveMQTextMessage) msg;

            if (textMessage != null) {
                String string = null;
                try {
                    string = textMessage.getText();
                    logger.log("MainApp:OutQueueListener message=" + string);
                    String userName = string.split("\\|")[0];
                    String message = string.split("\\|")[1];
                    ChatRouter.sendMessage(userName, message);
                } catch (JMSException | XMPPException e) {
                    e.printStackTrace();
                }

            }

        }
    }

}
