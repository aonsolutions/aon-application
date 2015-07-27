package com.esferalia.aon.file.payroll.contract.pdf;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;



public enum ContractPdfModel implements IResourceable{
	
	INDEFINITE,
	
	TEMPORARY,
	
	LEARNING,
	
	PRACTICE,
	
	PE200,
	
	PE192,
	
	PE191,
	
	INTERNSHIP;
	

	/** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_pdf_model_";
	
	@Override
	public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
	
}