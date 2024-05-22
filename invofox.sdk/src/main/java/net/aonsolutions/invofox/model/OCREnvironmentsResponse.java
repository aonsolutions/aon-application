package net.aonsolutions.invofox.model;

import java.util.LinkedList;
import java.util.List;

public class OCREnvironmentsResponse extends OCRResponse {

	private static final long serialVersionUID = 6447497857395944437L;
	
	private List<OCREnvironment> environments;

	@Override
	public OCREnvironmentsResponse setHttpCode(Integer httpCode) {
		super.setHttpCode(httpCode);
		return this;
	}

	@Override
	public OCREnvironmentsResponse setError(OCRError error) {
		super.setError(error);
		return this;
	}

	public List<OCREnvironment> getEnvironments() {
		if(environments == null)
			environments = new LinkedList<>();
		return environments;
	}

	public OCREnvironmentsResponse setEnvironments(List<OCREnvironment> environments) {
		this.environments = environments;
		return this;
	}
	
	public OCREnvironmentsResponse addEnvironment(OCREnvironment environment) {
		getEnvironments().add(environment);
		return this;
	}

}
