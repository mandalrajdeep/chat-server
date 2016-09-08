package com.ticklefish.automated.replies;

import java.io.FileNotFoundException;

public class InMemoryQueryRepliesTest {

    public static void main(String[] args) {
        InMemoryQueryReplies automatedReplies = null;

        try {
            automatedReplies = new InMemoryQueryReplies();
            System.out.println(automatedReplies.toString());
            System.out.println(automatedReplies.getSize());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
