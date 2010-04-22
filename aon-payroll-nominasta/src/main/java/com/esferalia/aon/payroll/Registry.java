package com.esferalia.aon.payroll;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Transient;

import com.esferalia.aon.core.IDocument;
import com.esferalia.aon.core.IRegistry;

@Embeddable
public class Registry implements IRegistry {

	private static final long serialVersionUID = -3882685468345124531L;
	
	private Document document;
	private String fullName;
	
	
	@Override
	@Embedded
	public Document getDocument() {
		return document;
	}
	
	@Override
	public void setDocument(IDocument document) {
		this.document = (Document) document;
	}

	@Override
	@Transient
	public String getFullName() {
		return fullName;
	}

	@Override
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

}
