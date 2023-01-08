package ekrut.entity;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConnectedClient {
	private SimpleStringProperty ip;
	private SimpleStringProperty username;
	private SimpleStringProperty role;


	public ConnectedClient(final String ip, final String username, final UserType role) {
		this.ip = new SimpleStringProperty(ip);
		this.username = new SimpleStringProperty(username);
		this.role = new SimpleStringProperty(role.toString());
	}

	public String getIp() {
		return ip.get();
	}

	public void setIp(String ip) {
		this.ip.set(ip);
	}

	public StringProperty ipProperty() {
		return ip;
	}
	public String getUsername() {
		return username.get();
	}

	public void setUsername(String username) {
		this.username.set(username);
	}
	
	public StringProperty usernameProperty() {
		return username;
	}

	public String getRole() {
		return role.get();
	}

	public void setRole(String role) {
		this.role.set(role);
	}
	public StringProperty roleProperty() {
		return role;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof ConnectedClient) {
			ConnectedClient other = (ConnectedClient)o;
			return this.getUsername().equals(other.getUsername());
		}
		return false;
	}
}
