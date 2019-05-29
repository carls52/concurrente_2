package es.codeurjc.webchat;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Chat {

	private String name;
	volatile private Map<String, User> users = new HashMap<>();

	volatile private ChatManager chatManager;
	
	 private Runnable tarea;

	public Chat(ChatManager chatManager, String name) {
		this.chatManager = chatManager;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public synchronized void addUser(User user) {
		users.put(user.getName(), user);
		for(User u : users.values()){
			if (u != user) {
				u.newUserInChat(this, user);
			}
		}
	}

	public synchronized void removeUser(User user) {
		users.remove(user.getName());
		for(User u : users.values()){
			u.userExitedFromChat(this, user);
		}
	}

	public  Collection<User> getUsers() {
		return Collections.unmodifiableCollection(users.values());
	}

	public User getUser(String name) {
		return users.get(name);
	}

	public synchronized void sendMessage(User user, String message) {
		//se genera un pool de hilos uno para cada usuario
		ExecutorService executor = Executors.newFixedThreadPool(users.size());

		for(User u : users.values()){
			//cada hilo manda el mensaje a cada usuario
			tarea = ()-> u.newMessage(this, user, message);
			executor.execute(tarea);
		}
		executor.shutdown();
	}
	//mapa de executors por cada usuario 
	
	public void close() {
		this.chatManager.closeChat(this);
	}
}
