package mediawiki.event;

public class ProgressChangeEvent<R,V> {

	private R referer;
	private V newvalue;
	
	public ProgressChangeEvent(R ref, V newvalue){
		referer = ref;
		this.newvalue = newvalue; 
	}

	public R getReferer() {
		return referer;
	}

	public V getNewvalue() {
		return newvalue;
	}

	public void setReferer(R referer) {
		this.referer = referer;
	}

	public void setNewvalue(V newvalue) {
		this.newvalue = newvalue;
	}
}
