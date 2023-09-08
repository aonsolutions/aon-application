package net.aonsolutions.invofox.model;

import java.util.List;
import java.util.Optional;

public class OCRCompaniesResponse extends OCRResponse {

	private static final long serialVersionUID = -796537650796711873L;
	
	private List<OCRCompany> companies;

	@Override
	public OCRCompaniesResponse setHttpCode(Integer httpCode) {
		super.setHttpCode(httpCode);
		return this;
	}

	@Override
	public OCRCompaniesResponse setError(OCRError error) {
		super.setError(error);
		return this;
	}

	@Override
	public OCRCompaniesResponse setSkip(Integer skip) {
		super.setSkip(skip);
		return this;
	}

	@Override
	public OCRCompaniesResponse setLimit(Integer limit) {
		super.setLimit(limit);
		return this;
	}

	@Override
	public OCRCompaniesResponse setCount(Integer count) {
		super.setCount(count);
		return this;
	}

	public Optional<List<OCRCompany>> getCompanies() {
		return Optional.ofNullable(companies);
	}

	public OCRCompaniesResponse setCompanies(List<OCRCompany> companies) {
		this.companies = companies;
		return this;
	}

}
