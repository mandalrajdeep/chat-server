package com.jugaado.chat;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
	private static Map<String, Task> userTaskMap = new HashMap<String, Task>();
	
	public static void addTask(String user, Task task) {
		userTaskMap.put(user, task);
	}

	public static void addMessage(JMessage message) throws FileNotFoundException {
		String user = message.getFrom();
		if(userTaskMap.containsKey(user) && !userTaskMap.get(user).isEmpty()) {
			Task task = userTaskMap.get(user);
			task.addMessage(message);
		} else {
			Task task = new Task(user);
			userTaskMap.put(user, task);
			task.addMessage(message);
			QueryManager.submit(task);
		}
	}
}
