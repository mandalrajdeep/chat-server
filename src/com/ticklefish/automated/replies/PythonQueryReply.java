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
				System.out.println("Query is : "+ query);
				Process process = new ProcessBuilder("python","-W","ignore","/root/jugaado/jugaadoHostMigration/reader/pythonAutomation/exactMatch.py","'" + query +"'").start(); 
				try {
					int expectedZero = process.waitFor();
					System.out.println("Python process exited with status:" + expectedZero);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				Scanner scanner = new Scanner(new File("/root/jugaado/jugaadoHostMigration/reader/automatedOutput.txt"));
				scanner.useDelimiter("\\Z");
				reply = scanner.next();
				scanner.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return reply;
	}

}
