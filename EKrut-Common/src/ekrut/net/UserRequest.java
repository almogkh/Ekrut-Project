package ekrut.net;

import java.io.Serializable;

public class UserRequest implements Serializable{
	private static final long serialVersionUID = 5395643634235579687L;
	private UserRequestType action;
	private String username;
	private String password;
}
