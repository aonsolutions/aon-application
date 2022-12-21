package solutions.aon.seg.social.object;

import java.util.HashMap;
import java.util.Map;

public class ITPartPage {
	private Map<Integer, ITPart> data;
	private boolean next;
	
	public ITPartPage(){
		data = new HashMap<>();
	}
	
	public Map<Integer, ITPart> getData() {
		return data;
	}
	
	public ITPartPage setData(Map<Integer, ITPart> data) {
		this.data = data;
		return this;
	}
	
	public boolean getNext() {
		return next;
	}
	
	public ITPartPage setNext(boolean next) {
		this.next = next;
		return this;
	}
}
