package net.aonsolutions.invofox.model;

import java.util.Optional;

public class OCRInfoResponse extends OCRResponse {

	private static final long serialVersionUID = 6447497857395944437L;
	
	private OCRPage[] pages;

	@Override
	public OCRInfoResponse setHttpCode(Integer httpCode) {
		super.setHttpCode(httpCode);
		return this;
	}

	@Override
	public OCRInfoResponse setError(OCRError error) {
		super.setError(error);
		return this;
	}

	@Override
	public OCRInfoResponse setSkip(Integer skip) {
		super.setSkip(skip);
		return this;
	}

	@Override
	public OCRInfoResponse setLimit(Integer limit) {
		super.setLimit(limit);
		return this;
	}

	@Override
	public OCRInfoResponse setCount(Integer count) {
		super.setCount(count);
		return this;
	}

	public Optional<OCRPage[]> getPages() {
		return Optional.ofNullable(pages);
	}

	public OCRInfoResponse setPages(OCRPage[] pages) {
		this.pages = pages;
		return this;
	}

}
