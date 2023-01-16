package ekrut.client.managers;

import ekrut.client.EKrutClient;
import ekrut.client.IResponseHandler;

/**
 * Base class for all client manager classes.
 * All manager classes on the client side should extend this class.
 * 
 * @author Almog Khaikin
 *
 * @param <T> the request type this manager sends
 * @param <R> the response type this manager receives
 */
public abstract class AbstractClientManager<T, R> implements IResponseHandler {

	private R response;
	private Object lock = new Object();
	protected EKrutClient client;
	
	/**
	 * Initializes the client field and registers the manager for receiving responses.
	 * @param client the EKrutClient instance
	 * @param klass  a Class object representing the response type the manager uses
	 */
	protected AbstractClientManager(EKrutClient client, Class<R> klass) {
		this.client = client;
		client.registerHandler(klass, this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void handleResponse(Object response) {
		synchronized (lock) {
			this.response = (R) response;
			lock.notify();
		}
	}
	
	/**
	 * Synchronously sends a request to the server and awaits a response from it
	 * 
	 * @param request the request to send
	 * @return        the server's response
	 */
	protected R sendRequest(T request) {
		response = null;
		client.sendRequestToServer(request);
		synchronized (lock) {
			while (response == null) {
				try {
					lock.wait();
				} catch (InterruptedException e) {}
			}
		}
		
		return response;
	}
}
