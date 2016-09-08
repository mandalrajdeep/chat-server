package com.jugaado.chat.main;

public class ProducerThread extends Thread {
    private static ListenTrigger listenTrigger;
    static {
        listenTrigger = ListenTrigger.getInstance();
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            listenTrigger.trigger();
        }
    }
}
