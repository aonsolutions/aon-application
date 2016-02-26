package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public interface IFiscalModel extends Serializable {

	Integer getId();
	int getDomain();
	String getDomainName();
	FiscalModelType getModel();
	
	IFiscalModelKey getDeclarationTypeKey();
	double getResult();
	
	int getYear();
	Period getPeriod();
	Administration getAdministration();
	FiscalStatus getStatus();
	
	boolean isReplacement();
	boolean isComplementary();
	
	String getDocument();
	String getName();
	String getSurname();
	String getFullName();
}
