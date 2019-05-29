package es.sidelab.webchat;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;


public class TestTimeOut {

	 private Runnable tarea;
	 private CountDownLatch l = new CountDownLatch(2);
	 boolean b = true;
	
	@Test
	public void testTodoBien() throws InterruptedException, TimeoutException {
			
		ExecutorService executor = Executors.newFixedThreadPool(4);
		ChatManager chatManager = new ChatManager(1);
		Chat chat = chatManager.newChat("chat_pruebas", 10, TimeUnit.SECONDS);
		
		Collection <Chat> coleccion = chatManager.getChats();
		for(Chat c : coleccion)
		{
			System.out.println(c.getName());
		}
		tarea = ()-> {
			try {
				crearUser(chatManager);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};

		executor.execute(tarea);

		//Thread.sleep(6000);
		tarea = ()-> {
			try {
				crearUser2(chatManager);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				b = false;
			}
		};

		chat.close();
		System.out.println("chat cerrado");
		executor.execute(tarea);

		coleccion = chatManager.getChats();
		l.await();
		executor.shutdown();
		assertTrue(b);
	}
	//test si salta excpcion debe devolver rojo
	
	public void crearUser(ChatManager chatManager) throws InterruptedException, TimeoutException
	{
		System.out.println("1.CHAT2");
		Chat chat2 = chatManager.newChat("chat_pruebas2", 1, TimeUnit.SECONDS);
		Thread.sleep(15000);
		chat2.close();		
		System.out.println("2.CHAT2");
		l.countDown();
		l.countDown();
	}
	public void crearUser2(ChatManager chatManager) throws InterruptedException, TimeoutException
	{
		System.out.println("1.CHAT3");
		Chat chat3 = chatManager.newChat("chat_pruebas3", 1, TimeUnit.SECONDS);
		Thread.sleep(2000);
		chat3.close();
		System.out.println("2.CHAT3");
		l.countDown();
		//l.countDown();
	}

}
