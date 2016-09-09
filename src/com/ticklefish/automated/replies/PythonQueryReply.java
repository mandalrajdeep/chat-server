package com.ticklefish.automated.replies;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import com.jugaado.chat.util.Config;
import com.jugaado.chat.util.MyLogger;

public class PythonQueryReply extends InMemoryQueryReplies {

    private static MyLogger logger = MyLogger.getLogger(PythonQueryReply.class);

    public PythonQueryReply() throws FileNotFoundException {}

    @Override
    public String getReply(String query) {
        String reply = "Automated Reply is turned off";
        if (Config.AUTO_REPLY_ON) {
            try {
                logger.log("PythonQueryReply:Query received in getReply="
                        + query);
                logger.log("PythonQueryReply:Query received in");
                logger.log(String.format("hola bola"));
                logger.log(String.format(query));
                //
                // Config.AUTOMATED_REPLY_QUERY_PATH
                // Process process = new
                // ProcessBuilder("python","/Users/buta/Desktop/scripts/zommoz_python_AI/20160527/pythonAutomation/exactMatch_small.py","'"
                // + query +"'").start();
                Process process =
                        new ProcessBuilder("python",
                            Config.AUTOMATED_REPLY_QUERY_PATH, "'" + query
                                    + "'").start();
                try {
                    int expectedZero = process.waitFor();
                    if (expectedZero != 0) {
                        logger
                            .log("PythonQueryReply:Python process exited with status:"
                                    + expectedZero);
                    } else {
                        logger.log("PythonQueryReply:Python did not exit:"
                                + expectedZero);

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Scanner scanner =
                        new Scanner(new File(Config.AUTOMATED_RESPONSE_FILE));
                scanner.useDelimiter("\\Z");
                reply = scanner.next();
                logger.log("Fuck Off" + reply);
                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return reply;
    }

}
