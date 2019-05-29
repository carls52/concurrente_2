package es.codeurjc.webchat;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ChatManager {

	volatile private Map<String, Chat> chats = new HashMap<>();
	volatile private Map<String, User> users = new HashMap<>();
	//concurrentHashMap es la mejor opcion
	private int maxChats;
	volatile private Semaphore semaphore; 
	boolean entrada = false;
	private Object xLock = new Object();

	public ChatManager(int maxChats) {
		this.maxChats = maxChats;
		this.semaphore = new Semaphore(this.maxChats);
	}

	public synchronized void  newUser(User user) {
		
		if(users.containsKey(user.getName())){
			throw new IllegalArgumentException("There is already a user with name \'"
					+ user.getName() + "\'");
		} else {
			users.put(user.getName(), user);
		}
	}

	public Chat newChat(String name, long timeout, TimeUnit unit) throws InterruptedException,
			TimeoutException {
		synchronized(xLock)
		{
			if(!semaphore.tryAcquire(timeout,unit))
			{
				throw new TimeoutException("There is no enought capacity to create a new chat");
			}
		/*if (chats.size() == maxChats) {
			//se esperan 60 segundos , en caso contrario salta una excepcion
			entrada = timeOut(60);
			if(entrada==false)
			{
			throw new TimeoutException("There is no enought capacity to create a new chat");
			}
			//condicion de carrera 
			//el numero de permisos debe ser el numero de chats asi te ahorras las condiciones de carrera
		}*/
		
		if(chats.containsKey(name)){
			return chats.get(name);
		} else {
			Chat newChat = new Chat(this, name);
			chats.put(name, newChat);
			
			for(User user : users.values()){
				user.newChat(newChat);
			}

			return newChat;
		}
		}
		
	}

	public boolean timeOut(int time) throws InterruptedException
	{
		System.out.println("chat esperando..");
		//se espera el tiempo puesto a que el semaforo este libre
		if(semaphore.tryAcquire(time, TimeUnit.SECONDS))
			return true;
		else
			return false;
	}
	
	public synchronized void closeChat(Chat chat) {
		//liberamos el semaforo dando a entender que hay hueco para mas chats
		if (chats.size() == maxChats) {
		semaphore.release();
		}
		Chat removedChat = chats.remove(chat.getName());
		if (removedChat == null) {
			throw new IllegalArgumentException("Trying to remove an unknown chat with name \'" + chat.getName() + "\'");
		}

		for(User user : users.values()){
			user.chatClosed(removedChat);
		}

	}

	public synchronized Collection<Chat> getChats() {
		return Collections.unmodifiableCollection(chats.values());
	}

	public Chat getChat(String chatName) {
		return chats.get(chatName);
	}

	public synchronized Collection<User> getUsers() {
		return Collections.unmodifiableCollection(users.values());
	}

	public User getUser(String userName) {
		return users.get(userName);
	}

	public void close() {}
}
