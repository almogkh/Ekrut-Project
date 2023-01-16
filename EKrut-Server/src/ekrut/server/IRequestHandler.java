package ekrut.server;

import ocsf.server.ConnectionToClient;

public interface IRequestHandler {

	Object handleMessage(Object request, ConnectionToClient client);
}
