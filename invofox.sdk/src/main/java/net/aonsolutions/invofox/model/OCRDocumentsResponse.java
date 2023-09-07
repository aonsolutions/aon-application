package net.aonsolutions.invofox.model;

import java.util.List;
import java.util.Optional;

public class OCRDocumentsResponse extends OCRResponse {

	private static final long serialVersionUID = 2055965872506639907L;
	
	private List<OCRDocument> documents;

	@Override
	public OCRDocumentsResponse setHttpCode(Integer httpCode) {
		super.setHttpCode(httpCode);
		return this;
	}

	@Override
	public OCRDocumentsResponse setError(OCRError error) {
		super.setError(error);
		return this;
	}

	@Override
	public OCRDocumentsResponse setSkip(Integer skip) {
		super.setSkip(skip);
		return this;
	}

	@Override
	public OCRDocumentsResponse setLimit(Integer limit) {
		super.setLimit(limit);
		return this;
	}

	@Override
	public OCRDocumentsResponse setCount(Integer count) {
		super.setCount(count);
		return this;
	}

	public Optional<List<OCRDocument>> getDocuments() {
		return Optional.ofNullable(documents);
	}

	public OCRDocumentsResponse setDocuments(List<OCRDocument> documents) {
		this.documents = documents;
		return this;
	}

}
