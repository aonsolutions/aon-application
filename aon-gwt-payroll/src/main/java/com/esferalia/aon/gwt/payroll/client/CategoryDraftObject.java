package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.CategoryDraft;

public class CategoryDraftObject extends AgreementDraftObject {

	public CategoryDraftObject(CategoryDraft categoryDraft,
			EmployeesServiceAsync employeesServiceAsync) {
		super(categoryDraft, employeesServiceAsync);
	}
	
	protected CategoryDraft getCategoryDraft() {
		return (CategoryDraft)super.getAgreementDraft();
	}
	
}
