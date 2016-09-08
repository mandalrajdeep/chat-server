package com.jugaado.chat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Handles tasks and queries related to a user
 *
 * @author ignatius
 */
public class QueryManager {

    private static ExecutorService fixedPool = Executors.newFixedThreadPool(10);

    public static Future<?> submit(Task task) {
        return fixedPool.submit(task);
    }

}
