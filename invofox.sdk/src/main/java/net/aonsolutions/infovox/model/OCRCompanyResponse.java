package net.aonsolutions.infovox.model;

import java.util.Optional;

public class OCRCompanyResponse extends OCRResponse {

	private OCRCompany company;

	@Override
	public OCRCompanyResponse setHttpCode(Integer httpCode) {
		super.setHttpCode(httpCode);
		return this;
	}

	@Override
	public OCRCompanyResponse setError(OCRError error) {
		super.setError(error);
		return this;
	}

	@Override
	public OCRCompanyResponse setSkip(Integer skip) {
		super.setSkip(skip);
		return this;
	}

	@Override
	public OCRCompanyResponse setLimit(Integer limit) {
		super.setLimit(limit);
		return this;
	}

	@Override
	public OCRCompanyResponse setCount(Integer count) {
		super.setCount(count);
		return this;
	}

	public Optional<OCRCompany> getCompany() {
		return Optional.ofNullable(company);
	}

	public OCRCompanyResponse setCompanies(OCRCompany company) {
		this.company = company;
		return this;
	}

}
