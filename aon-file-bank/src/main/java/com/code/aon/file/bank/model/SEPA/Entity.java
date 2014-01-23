package com.code.aon.file.bank.model.SEPA;


public interface Entity {

	String getName();
	
	Address getSEPAAddress();
	
	boolean isOrganisation();
	
	String getDocument();
	
	String getDocumentType();
	
}
