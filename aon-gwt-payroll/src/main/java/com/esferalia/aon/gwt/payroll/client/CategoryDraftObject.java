package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.CategoryDraft;

public class CategoryDraftObject extends AgreementDraftObject {

	public CategoryDraftObject(Integer domain, CategoryDraft categoryDraft,
			EmployeesServiceAsync employeesServiceAsync) {
		super(domain, categoryDraft, employeesServiceAsync);
	}
	
	protected CategoryDraft getCategoryDraft() {
		return (CategoryDraft)super.getAgreementDraft();
	}
	
}
