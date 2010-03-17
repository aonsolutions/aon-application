package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.esferalia.aon.core.IDocument;

@Embeddable
public class Document implements IDocument {

	private static final long serialVersionUID = 8951268005779548912L;
	
	String value;
	
	@Override
	@Column(name="numdoc", nullable=false, length=10)
	public String getValue() {
		return value;
	}
	
	@Override
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public boolean validate() {
		return true;
	}

}
