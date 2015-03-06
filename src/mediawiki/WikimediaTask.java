package mediawiki;

public abstract class WikimediaTask implements Runnable {

	private WikimediaConnection connection;
	
	public WikimediaTask(WikimediaConnection con){
		connection = con;
	}
	
	public WikimediaConnection getConnection() {
		return connection;
	}
	
}
