package es.sidelab.webchat;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;

public class TestAltaUsuario {

	@Test
	public void test() throws InterruptedException, TimeoutException {
		//creamos el ChatManager
		ChatManager chatManager = new ChatManager(1);
		//creamos un chat
		Chat chat = chatManager.newChat("chat_pruebas", 1, TimeUnit.SECONDS);
		//creamos un usuario lo añadimos al chatManager y al chat
		TestUser user = new TestUser("USER0");
		chatManager.newUser(user);
		chat.addUser(user);
		//comprobamos que el usuario coincide con lo esperado
		assertEquals(chat.getUser("USER0"),user);
	}

}
