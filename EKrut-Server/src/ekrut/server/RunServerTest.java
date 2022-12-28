package ekrut.server;

import java.io.IOException;

public class RunServerTest {

	public static void main(String[] args) {
		EKrutServer ekServer = new EKrutServer(5555, "root", "UntilWhenNov12");
		try {
			ekServer.listen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
