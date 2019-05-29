package es.sidelab.webchat;

import static org.junit.Assert.*;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;
import es.codeurjc.webchat.User;

public class TestEnvioMensaje {
	private Exchanger<String> exchanger = new Exchanger<>();

	@Test
	public void test() throws InterruptedException, TimeoutException {
		//creamos el ChatManager
		ChatManager chatManager = new ChatManager(1);
		
		//creamos un chat
		Chat chat = chatManager.newChat("chat_pruebas", 1, TimeUnit.SECONDS);
		
		//creamos los usuarios que enviaran y recibiran el mensaje
		TestUser userSend = new TestUser("USER0");
		TestUser userRecv = new TestUser("USER") {
			@Override
			//el usuario que recibe el mensaje lo reenvia al hilo principal por un exchanger
			public void newMessage(Chat chat, User user,String message) {
				System.out.println("New message '" + message + "' from user " + user.getName()
				+ " in chat " + chat.getName());
				try {
					exchanger.exchange(message);
				} catch (InterruptedException e) {
					System.out.println("ERROR");
				}
			}
		};
		//añadimos a los usuarios al chatManager y al chat que les toca
		chatManager.newUser(userSend);
		chatManager.newUser(userRecv);
		chat.addUser(userSend);
		chat.addUser(userRecv);
		
		//mandamos el mensaje y lo recogemos con el exchanger
		chat.sendMessage(userSend,"Mensaje de prueba");
		String message = exchanger.exchange(null); 
		
		//comparamos si el mensaje recibido por el usuario es el mismo que deberia ser
		assertEquals(message,"Mensaje de prueba");

	}

}
