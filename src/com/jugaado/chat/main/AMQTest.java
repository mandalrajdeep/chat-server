package com.jugaado.chat.main;

import org.jivesoftware.smack.XMPPException;

import com.jugaado.amq.AMQConnection;

public class AMQTest {

    public static void main(String[] args) throws XMPPException {
        AMQConnection amq = new AMQConnection();
        amq.setIncomingAppMessage("haha", "lala");
    }

}
