package datasets.out;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteConnection {

	private Connection connection = null;
	private String location ="";
	
	public SQLiteConnection(String file) {
		location = file;
	}
	
	public void open() throws SQLException, ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
	    connection = DriverManager.getConnection("jdbc:sqlite:"+location);
	}

	public Statement createStatement() throws SQLException{
		return connection.createStatement();
	}
	
	public void executeUpdate(String sql) throws SQLException{
		Statement s = createStatement();
		s.setQueryTimeout(30);
		s.executeUpdate(sql);
	}
	
	public ResultSet executeQuery(String sql) throws SQLException{
		Statement s = createStatement();
		s.setQueryTimeout(30);
		return s.executeQuery(sql);
	}
	
	public void close() throws SQLException{
		connection.close();
	}
	
	
}
