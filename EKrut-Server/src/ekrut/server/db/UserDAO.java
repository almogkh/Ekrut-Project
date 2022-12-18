package ekrut.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.cj.protocol.Resultset;

import ekrut.entity.User;
import ekrut.entity.UserType;

public class UserDAO {
	
	//return null if couldn't locate subscriber id
	public static User fetchUserByUsername(String username) {
		return null;
	}
}
