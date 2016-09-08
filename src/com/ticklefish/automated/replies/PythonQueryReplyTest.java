package com.ticklefish.automated.replies;

import java.io.FileNotFoundException;

public class PythonQueryReplyTest {
    public static void main(String[] args) {
        PythonQueryReply automatedReplies = null;
        try {
            automatedReplies = new PythonQueryReply();
            System.out.println(automatedReplies.getReply("What is Jugaado"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
