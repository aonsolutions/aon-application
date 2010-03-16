package com.esferalia.aon.payroll;

import com.esferalia.aon.core.IDocument;
import com.esferalia.aon.core.IRegistry;

public class Registry implements IRegistry {

	private static final long serialVersionUID = -3882685468345124531L;
	
	private IDocument document;
	private String fullName;
	
	
	@Override
	public IDocument getDocument() {
		return document;
	}
	
	@Override
	public void setDocument(IDocument document) {
		this.document = document;
	}

	@Override
	public String getFullName() {
		return fullName;
	}

	@Override
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

}
