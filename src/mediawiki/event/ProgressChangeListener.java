package mediawiki.event;

public interface ProgressChangeListener<T,V> {

	public void progressChanged(ProgressChangeEvent<T,V> r);
}
