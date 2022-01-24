package net.aonsolutions.aon.tbai;

import java.util.LinkedList;
import java.util.List;

public class TBAIInformation {

	List<TBAIRequest> requests;
	
	public List<TBAIRequest> getRequests() {
		if(requests == null) {
			requests = new LinkedList<>();
		}
		return requests;
	}
	
	public TBAIInformation setRequests(List<TBAIRequest> requests) {
		this.requests = requests;
		return this;
	}
	
	public TBAIInformation addRequest(TBAIRequest request) {
		getRequests().add(request);
		return this;
	}
	
	public boolean isAccepted() {
		return getRequests().stream().filter(f -> f.getResponse().isOk())
			.count() > 0;
	}
}
