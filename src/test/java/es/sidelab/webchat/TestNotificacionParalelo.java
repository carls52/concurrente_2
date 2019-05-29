package es.sidelab.webchat;

import java.util.concurrent.CountDownLatch;

//import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;
import es.codeurjc.webchat.User;

public class TestNotificacionParalelo {
	private CountDownLatch latch; 
	@Test
	public void test() throws InterruptedException, TimeoutException {
		//creamos el ChatManager
		ChatManager chatManager = new ChatManager(1);
		
		//creamos un chat
		Chat chat = chatManager.newChat("chat_pruebas", 1, TimeUnit.SECONDS);
		
		//inicializamos la variable user
		TestUser user = null;
		
		latch = new CountDownLatch(4);
		for (int i = 0; i<4;i++)
		{
			//generamos users con el NewMessage modificado para que tarde 1 segundo
				user = new TestUser("USER"+i) {
					@Override
					public void newMessage(Chat chat, User user,String message) {
						try {
							Thread.sleep(1000);
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("New message '" + message + "' from user " + user.getName()
						+ " in chat " + chat.getName());
						// bajamos uno la cuenta atras
						latch.countDown();
					}
				};

				chatManager.newUser(user);
				chat.addUser(user);
		}
		// enviamos el mensaje que se enviará concurrentemente
		chat.sendMessage(user,"hola");
		
		//esperamos a que la cuenta atras llegue a cero
		latch.await();

	}
	//test tiene que fallar si se pasa del tiempo

}
