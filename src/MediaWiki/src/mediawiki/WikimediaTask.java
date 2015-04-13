package mediawiki;

import java.util.ArrayList;
import java.util.Date;

import mediawiki.event.CompletedListener;
import mediawiki.event.ProgressChangeEvent;
import mediawiki.event.ProgressChangeListener;

public abstract class WikimediaTask implements Runnable {

	private int togo = 0;
	private int done = 0;
	private ArrayList<ProgressChangeListener<WikimediaTask,Double>> listeners = new ArrayList<>();
	private ArrayList<CompletedListener> listeners2 =new ArrayList<>();
	
	private long t1 = 0;
	private double p1 = 0;
	
	private boolean stopped = false;
	
	private WikimediaConnection connection;
	
	public WikimediaTask(WikimediaConnection con){
		connection = con;
	}
	
	public WikimediaConnection getConnection() {
		return connection;
	}

	
	public int getTogo() {
		return togo;
	}

	public int getDone() {
		return done;
	}

	protected void setTogo(int togo) {
		this.togo = togo;
		fireProgressChangeEvent(new ProgressChangeEvent<WikimediaTask, Double>(this, getProgress()));
	}

	protected void setDone(int done) {
		this.done = done;
		fireProgressChangeEvent(new ProgressChangeEvent<WikimediaTask, Double>(this, getProgress()));
	}
	
	public double getProgress(){
		if(togo == 0)
			return Double.NaN;
		return ((double) done)/((double) togo);
	}

	protected void increaseDone(){
		setDone(getDone()+1);
	}

	public void addProgressChangeListener(ProgressChangeListener<WikimediaTask,Double> listener){
		listeners.add(listener);
	}
	
	protected void fireProgressChangeEvent(ProgressChangeEvent<WikimediaTask,Double> e){
		for(ProgressChangeListener l : listeners)
			l.progressChanged(e);
	}
	
	public void addCompletedListener(CompletedListener listener){
		listeners2.add(listener);
	}
	
	protected void fireCompletedEvent(){
		for(CompletedListener l : listeners2)
			l.completed();
	}

	public boolean isStopped() {
		return stopped;
	}

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
		if(stopped == false)
			fireCompletedEvent();
	}

	public void recordETA(){
		if(getTogo() == 0)
			throw new IllegalStateException("Set Togo before recording ETA");
		t1 = new Date().getTime();
		p1 = getProgress();
	}
	
	public Date getETA(){
		if(getTogo() == 0)
			return null;
		if(t1 == 0)
			return null;
		long dT = new Date().getTime()-t1;
		double dP = getProgress()-p1;
		if(dP == Double.NaN || dP == 0)
			return null;
		long verbl = (long) ((1-getProgress())/(dP/dT));
		return new Date(new Date().getTime()+verbl);
	}
}
