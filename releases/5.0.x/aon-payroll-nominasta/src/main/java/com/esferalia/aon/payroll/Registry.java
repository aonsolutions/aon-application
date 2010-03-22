package com.esferalia.aon.payroll;

import com.esferalia.aon.core.IDocument;
import com.esferalia.aon.core.IRegistry;

public class Registry<D extends IDocument> implements IRegistry<D> {

	private D document;
	private String fullName;
	
	
	@Override
	public D getDocument() {
		return document;
	}
	
	@Override
	public void setDocument(D document) {
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
