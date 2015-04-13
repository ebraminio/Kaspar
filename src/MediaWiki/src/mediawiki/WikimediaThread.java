package mediawiki;

public class WikimediaThread extends Thread {
	
	private WikimediaTask task;
	
	public WikimediaThread(WikimediaTask t){
		super(t, t.getClass().getSimpleName());
		setTask(t);
	}

	public WikimediaTask getTask() {
		return task;
	}

	public void setTask(WikimediaTask task) {
		this.task = task;
	}
}
