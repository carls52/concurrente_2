package es.sidelab.webchat;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;

public class TestCrearChat {

	@Test
	public void test() throws InterruptedException, TimeoutException {
		//creamos el ChatManager
		ChatManager chatManager = new ChatManager(1);
		//creamos un chat
		Chat chat = chatManager.newChat("chat_pruebas", 1, TimeUnit.SECONDS);
		//comprobamos que el chat se ha creado y se llama como deberia
		assertEquals(chatManager.getChat("chat_pruebas"),chat);
	}

}
