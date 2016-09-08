package com.ticklefish.automated.replies;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import com.jugaado.chat.util.Config;

public class PythonQueryReply extends InMemoryQueryReplies{

	public PythonQueryReply() throws FileNotFoundException {
	}
	
	@Override
	public String getReply(String query)  {
		String reply = "Automated Reply is turned off";
		if(Config.AUTO_REPLY_ON) {
			try {
				System.out.println("PythonQueryReply:Query received in getReply="+ query);
				System.out.println("PythonQueryReply:Query received in");
				System.out.println(String.format("hola bola"));
				System.out.println(String.format(query));
				//
				//Config.AUTOMATED_REPLY_QUERY_PATH
				//Process process = new ProcessBuilder("python","/Users/buta/Desktop/scripts/zommoz_python_AI/20160527/pythonAutomation/exactMatch_small.py","'" + query +"'").start();
				Process process = new ProcessBuilder("python",Config.AUTOMATED_REPLY_QUERY_PATH,"'" + query +"'").start();
				try {
					int expectedZero = process.waitFor();
					if(expectedZero != 0){
						System.out.println("PythonQueryReply:Python process exited with status:" + expectedZero);
					}
					else
					{
						System.out.println("PythonQueryReply:Python did not exit:" + expectedZero);
						
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				Scanner scanner = new Scanner(new File(Config.AUTOMATED_RESPONSE_FILE));
				scanner.useDelimiter("\\Z");
				reply = scanner.next();
				System.out.println("Fuck Off" + reply);
				scanner.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return reply;
	}

}
