package ekrut.server.managers;

import ekrut.entity.User;
import ekrut.server.EKrutServer;
import ekrut.server.IRequestHandler;
import ekrut.server.db.DBController;
import ekrut.server.gui.ServerUI;
import ocsf.server.ConnectionToClient;

public abstract class AbstractServerManager<T, R> implements IRequestHandler {

	private R unknownErrorResponse;
	private DBController con;
	protected EKrutServer server;
	
	protected AbstractServerManager(Class<T> klass, R unknownErrorResponse) {
		this.server = ServerUI.getServer();
		server.registerHandler(klass, this);
		this.unknownErrorResponse = unknownErrorResponse;
		this.con = server.getDBController();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object handleMessage(Object request, ConnectionToClient client) {
		try {
			return handleRequest((T) request, server.getSession().getUser(client));
		} catch (Exception e) {
			con.abortTransaction();
			return unknownErrorResponse;
		}
	}
	
	protected abstract R handleRequest(T request, User user);
}
