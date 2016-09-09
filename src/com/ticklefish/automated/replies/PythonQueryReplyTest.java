package com.ticklefish.automated.replies;

import java.io.FileNotFoundException;

import com.jugaado.chat.util.MyLogger;

public class PythonQueryReplyTest {

    private static MyLogger logger = MyLogger
        .getLogger(PythonQueryReplyTest.class);

    public static void main(String[] args) {
        PythonQueryReply automatedReplies = null;
        try {
            automatedReplies = new PythonQueryReply();
            logger.log(automatedReplies.getReply("What is Jugaado"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
