package net.aonsolutions.invofox.model;

import java.util.Optional;

public class OCRDocumentResponse extends OCRResponse {

	private static final long serialVersionUID = 6447497857395944437L;
	
	private OCRDocument document;

	@Override
	public OCRDocumentResponse setHttpCode(Integer httpCode) {
		super.setHttpCode(httpCode);
		return this;
	}

	@Override
	public OCRDocumentResponse setError(OCRError error) {
		super.setError(error);
		return this;
	}

	@Override
	public OCRDocumentResponse setSkip(Integer skip) {
		super.setSkip(skip);
		return this;
	}

	@Override
	public OCRDocumentResponse setLimit(Integer limit) {
		super.setLimit(limit);
		return this;
	}

	@Override
	public OCRDocumentResponse setCount(Integer count) {
		super.setCount(count);
		return this;
	}

	public Optional<OCRDocument> getDocument() {
		return Optional.ofNullable(document);
	}

	public OCRDocumentResponse setDocument(OCRDocument document) {
		this.document = document;
		return this;
	}

}
