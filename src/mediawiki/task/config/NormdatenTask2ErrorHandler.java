package mediawiki.task.config;

import java.sql.SQLException;

import mediawiki.task.NormdatenTask2.NormdatenTask2Exception;

public interface NormdatenTask2ErrorHandler {

	public void handle(NormdatenTask2Exception e) throws Exception;
	
	public boolean accept(NormdatenTask2Exception e);
}
