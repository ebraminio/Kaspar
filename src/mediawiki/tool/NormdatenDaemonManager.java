package mediawiki.tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import mediawiki.task.NormdatenTask2;
import mediawiki.task.config.DatabaseHandler;

public class NormdatenDaemonManager implements Runnable{

	private Connection connection;
	private NormdatenTask2 nt2;
	private int taskID;
	private int runID;
	
	public NormdatenDaemonManager(Connection c, NormdatenTask2 nt2, int taskid){
		connection = c;
		this.nt2 = nt2;
		taskID = taskid;
	}
	
	private int registerRun() throws SQLException {
		PreparedStatement s = connection.prepareStatement("INSERT INTO runs (task) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
		s.setInt(1, taskID);
		s.executeUpdate();
		ResultSet rs = s.getGeneratedKeys();
		if (rs.next()){
		    runID = rs.getInt(1);
		    s.closeOnCompletion();
		    return runID;
		}
		s.closeOnCompletion();
		throw new RuntimeException("unknown error while registering run");
	}
	
	public void run()  {
		try{
			registerRun();
			
			nt2.registerErrorHandler(new DatabaseHandler(connection, runID));
			nt2.run();
			
			finalizeRun();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void finalizeRun() throws SQLException {
		{
			PreparedStatement s = connection.prepareStatement("UPDATE runs SET `end` = NOW() WHERE ID = ? LIMIT 1");
			s.setInt(1, runID);
			s.executeUpdate();
			s.closeOnCompletion();
		}
		{
			PreparedStatement s = connection.prepareStatement("DELETE FROM problems WHERE AND run != ? AND (SELECT project FROM tasks WHERE tasks.ID = (SELECT task FROM runs WHERE runs.ID = problems.run)) = (SELECT project FROM tasks WHERE tasks.ID = (SELECT task FROM runs WHERE runs.ID = ?))");
			s.setInt(1, runID);
			s.setInt(2, runID);
			s.executeUpdate();
			s.closeOnCompletion();
		}
		{
			Statement s = connection.createStatement();
			s.execute("CALL `delete_old`();");
			s.closeOnCompletion();
		}
	}
	
}
