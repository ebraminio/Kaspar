package mediawiki;

public class MediaWikiThread extends Thread {
	
	private MediaWikiTask task;
	
	public MediaWikiThread(MediaWikiTask t){
		super(t, t.getClass().getSimpleName());
		setTask(t);
	}

	public MediaWikiTask getTask() {
		return task;
	}

	public void setTask(MediaWikiTask task) {
		this.task = task;
	}
}
