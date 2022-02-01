package net.aonsolutions.aon.tbai.lroe;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.json.JsonUtils;

public class LROEChapter {

	List<LROERequest> requests;
	
	public List<LROERequest> getRequests() {
		if(requests == null) {
			requests = new LinkedList<>();
		}
		return requests;
	}
	
	public LROEChapter setRequests(List<LROERequest> requests) {
		this.requests = requests;
		return this;
	}
	
	public LROEChapter addRequest(LROERequest request) {
		getRequests().add(request);
		return this;
	}
	
	public boolean isAccepted() {
		boolean alta = getRequests().stream().filter(f ->
			f.getInfo().isAlta() && f.getResponse().isOk())
			.count() > 0;
		boolean anulacion =getRequests().stream().filter(f -> 
			f.getInfo().isAnulacion() && f.getResponse().isOk())
			.count() > 0;
		return alta && !anulacion;
	}
	
	public boolean isTbaiError() {
		boolean alta = getRequests().stream().filter(f ->
			f.getInfo().isAlta() && f.getResponse().isOk())
			.count() > 0;
		boolean anulacion =getRequests().stream().filter(f -> 
			f.getInfo().isAnulacion() && f.getResponse().isOk())
			.count() > 0;
		boolean err = getRequests().stream().filter(f ->
			f.getInfo().isAlta() 
			&& JsonUtils.optString(f.getResponse().getJson(), "errorMessage").contains("B4_2000001")).count() > 0;
			
		return !alta && !anulacion && err;
	}
}
