package com.jugaado.chat;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jivesoftware.smack.packet.Message;

public class MessageBroker {
    private static Queue<JMessage> incomingQueue =
            new ConcurrentLinkedQueue<JMessage>();
    private static Queue<JMessage> processedQueue =
            new ConcurrentLinkedQueue<JMessage>();
    private static Queue<Message> outgoingQueue =
            new ConcurrentLinkedQueue<Message>();

    public static boolean offer(JMessage message) {
        return incomingQueue.offer(message);
    }

    public static JMessage poll() {
        JMessage pollMessage = incomingQueue.poll();
        processedQueue.offer(pollMessage);
        return pollMessage;
    }

    public static JMessage peek() {
        return incomingQueue.peek();
    }

    public static Queue<Message> getOutgoingQueue() {
        return outgoingQueue;
    }

    public static boolean isQueueEmpty() {
        return incomingQueue.isEmpty();
    }

    public static Message getMessageTemplate() {
        String defaultMessage = "Welcome to Jugaado";
        String from = "master@openfire";
        Message message = new Message();
        message.setBody(defaultMessage);
        message.setFrom(from);
        return message;
    }
}
