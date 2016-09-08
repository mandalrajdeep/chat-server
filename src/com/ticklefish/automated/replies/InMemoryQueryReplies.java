package com.ticklefish.automated.replies;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.jugaado.chat.util.Config;

public class InMemoryQueryReplies {
	
	private Map<String, String> repliesMap;
	
	public InMemoryQueryReplies() throws FileNotFoundException {
		repliesMap = new HashMap<String, String>();
		
		if(Config.AUTO_REPLY_ON == true){
			Scanner scanner = new Scanner(new File(Config.AUTOMATED_RESPONSE_FILE));
			scanner.useDelimiter("\\Z");
			String content = null;
			
			if(scanner.hasNext()){
				content = scanner.next();
				String queryWithReplies[] = content.split("\\|");
				for (String string : queryWithReplies) {
					String token[] = string.split("~");
					if(token.length == 2){
						repliesMap.put(token[0].trim(), token[1].trim());
					}
				} 
			}
			scanner.close();
		}
	}
	
	@Override
	public String toString() {
		return repliesMap.toString();
	}
	
	public int getSize(){
		return repliesMap.size();
	}

	public String getReply(String query) {
		for (String storedQuery : repliesMap.keySet()) {
			if(query.equalsIgnoreCase(storedQuery)){
				return repliesMap.get(storedQuery);
			}
		}
		return null;
	}

}
