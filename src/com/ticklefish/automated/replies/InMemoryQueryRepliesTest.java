package com.ticklefish.automated.replies;

import java.io.FileNotFoundException;

import com.jugaado.chat.util.MyLogger;

public class InMemoryQueryRepliesTest {

    private static MyLogger logger = MyLogger
        .getLogger(InMemoryQueryRepliesTest.class);

    public static void main(String[] args) {
        InMemoryQueryReplies automatedReplies = null;

        try {
            automatedReplies = new InMemoryQueryReplies();
            logger.log(automatedReplies.toString());
            logger.log("" + automatedReplies.getSize());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
