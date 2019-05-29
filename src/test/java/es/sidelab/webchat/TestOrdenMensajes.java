package es.sidelab.webchat;

import static org.junit.Assert.*;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;
import es.codeurjc.webchat.User;

public class TestOrdenMensajes {
	private Exchanger<String> exchanger = new Exchanger<>();
	@Test
	public void test() throws InterruptedException, TimeoutException {	
		//array para guardar lo que devuelve el usuario
		String[] resultado = new String[5];		
		
		//creamos el ChatManager
		ChatManager chatManager = new ChatManager(1);
		
		//creamos un chat
		Chat chat = chatManager.newChat("chat_pruebas", 1, TimeUnit.SECONDS);
		
		//creamos los usuarios
		TestUser user = new TestUser("USER0");
		//el segundo usuario espera medio segundo y envia el mensaje por el exchanger
		TestUser user0 = new TestUser("USER") {
			@Override
			public void newMessage(Chat chat, User user,String message) {
				System.out.println("New message '" + message + "' from user " + user.getName()
				+ " in chat " + chat.getName());
				try {
					Thread.sleep(500);
					exchanger.exchange(message);
				} catch (InterruptedException e) {
					System.out.println("ERROR");
				}
			}
		};
		
		//añadimos a los usuarios al chatManager y al chat que les toca
		chatManager.newUser(user);		
		chatManager.newUser(user0);
		chat.addUser(user0);
		chat.addUser(user);
		
		//enviamos cada mensaje y se guarda en el array la respuesta del usuario
		chat.sendMessage(user,"1");
		chat.sendMessage(user,"2");
		chat.sendMessage(user,"3");
		chat.sendMessage(user,"4");
		chat.sendMessage(user,"5");
		resultado[0] = exchanger.exchange(null);
		System.out.println(resultado[0]);
		//forzar secuencial mal
		//mandar todos  recogerlos despues
		resultado[1] = exchanger.exchange(null);
		System.out.println(resultado[1]);
		resultado[2] = exchanger.exchange(null);
		resultado[3] = exchanger.exchange(null);
		resultado[4] = exchanger.exchange(null);
		
		assertTrue(esOrden(resultado));
	}
	//funcion que compara el orden que deberia ser con el orden de 
	//los mensajes devueltos por el usuario
	public boolean esOrden(String[] resultado) {
		return ((resultado[0]=="1")&&(resultado[1]=="2")&&
				(resultado[2]=="3")&&(resultado[3]=="4")&&(resultado[4]=="5"));
	}
}
