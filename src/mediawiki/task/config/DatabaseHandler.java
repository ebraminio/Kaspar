package mediawiki.task.config;

import java.sql.Connection;
import java.sql.PreparedStatement;

import mediawiki.task.NormdatenTask2.NormdatenTask2Exception;
import mediawiki.task.NormdatenTask2.NormdatenTask2ExceptionLevel;

public class DatabaseHandler implements NormdatenTask2ErrorHandler {

	private Connection connection;
	private int runID;
	
	
	public DatabaseHandler(Connection c, int runID){
		setConnection(c);
		setRunID(runID);
	}
	
	
	@Override
	public void handle(NormdatenTask2Exception e) throws Exception {
		if(e.getLevel() != NormdatenTask2ExceptionLevel.FINAL) {
			PreparedStatement s = connection.prepareStatement("INSERT INTO problems (article, typ, message, run) VALUES (?,?,?,?)");
			s.setString(1, e.getArticle().getTitle());
			s.setString(2, e.getType());
			s.setString(3, e.getSimpleMessage());
			s.setInt(4, getRunID());
			
			s.executeUpdate();
			s.closeOnCompletion();
		}
		
		PreparedStatement s = connection.prepareStatement("DELETE FROM problems WHERE article = ? AND `timestamp` < NOW() AND run != ? AND (SELECT project FROM tasks WHERE tasks.ID = (SELECT task FROM runs WHERE runs.ID = problems.run)) = (SELECT project FROM tasks WHERE tasks.ID = (SELECT task FROM runs WHERE runs.ID = ?))");
		s.setString(1, e.getArticle().getTitle());
		s.setInt(2, getRunID());
		s.setInt(3, getRunID());
		s.executeUpdate();
		s.closeOnCompletion();
	}
	
	@Override
	public boolean accept(NormdatenTask2Exception e) {
		switch(e.getLevel()){
		case INFO:
		
			return false;
		case FINAL:
		case EXTERNAL:
		case PROBLEM:
		case INTERNAL:
			return true;
		}
		return false;
	}


	public Connection getConnection() {
		return connection;
	}


	public void setConnection(Connection connection) {
		this.connection = connection;
	}


	public int getRunID() {
		return runID;
	}


	public void setRunID(int runID) {
		this.runID = runID;
	}

}
