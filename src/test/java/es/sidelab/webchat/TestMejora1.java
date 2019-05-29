package es.sidelab.webchat;

//import static org.junit.Assert.*;

import java.util.Collection;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;
import es.codeurjc.webchat.User;

public class TestMejora1 {
	public static final int N_TAREAS = 4;
	private Object xLock = new Object();
	// Crear el chat Manager
	ChatManager chatManager = new ChatManager(50);
	
	public String ejecutaTarea(int numTask, String[] nombre) throws InterruptedException, TimeoutException {
		try
		{
		//creamos el usuario
		TestUser user = new TestUser(nombre[numTask]);
		//registramos al ususario
	    chatManager.newUser(user);
		for(int i=0;i<5;i++)
		{
			//creamos un chat	
			Chat conversacion;
			conversacion = chatManager.newChat("CHAT_" + i, 50, TimeUnit.SECONDS);
			//añadimos un usuario
			conversacion.addUser(user);
						
			synchronized(xLock) {

				//mostramos los usuarios
				System.out.println(mostrarUsuarios(conversacion.getUsers(), user, conversacion));
			}
		}

		return ("USUARIO" + numTask + " FIN");
		
		}
		catch (InterruptedException e ) {
			System.out.println("PROBLEMA DE INTERRUPCION");
			return "ERROR";		
		}
		catch (TimeoutException es ) {
			System.out.println("PROBLEMA DE TIMEOUT");
			return "ERROR";	
		}
	}
	//Funcion para devolver la lista de usuario de un chat
	 public synchronized String mostrarUsuarios(Collection<User> listaUsuarios, TestUser user, Chat conversacion)
			 throws InterruptedException, TimeoutException
	 {
		 String resultado = "Usuarios en "+ conversacion.getName()+" : ";
	        for(User p : listaUsuarios) {
	        	resultado = resultado + p.getName()+ " ";
	        }	        		
		 return resultado;
	 }
	@Test
	public void exec() throws InterruptedException, ExecutionException {

		String[] nombres =  {"USER0","USER1","USER2","USER3"};
		ExecutorService executor = Executors.newFixedThreadPool(4);

		try {	
			CompletionService<String> completionService = new ExecutorCompletionService<>(executor);
			for (int i = 0; i < N_TAREAS; i++) {
				//se crean hilos para las tareas
				int numTask = i;
				completionService.submit(() -> ejecutaTarea(numTask,nombres));
			}
			for (int i = 0; i < N_TAREAS; i++) {
				//se espera a que terminen y se recoge su respuesta
				Future<String> completedTask = completionService.take();
				System.out.println("Task: " + completedTask.get());
			}
		} finally {
			executor.shutdown();
		}
	}
}

