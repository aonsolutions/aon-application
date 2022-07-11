package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.CategoryDraft;

public class CategoryDraftObject extends AgreementDraftObject {

	public CategoryDraftObject(Integer domain, String domainName, CategoryDraft categoryDraft,
			DomainEmployeesServiceAsync employeesServiceAsync) {
		super(domain, domainName, categoryDraft, employeesServiceAsync);
	}
	
	protected CategoryDraft getCategoryDraft() {
		return (CategoryDraft)super.getAgreementDraft();
	}
	
}
