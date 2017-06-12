package com.code.aon.fiscal.mod303;

import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Period;

public interface IMod303Declaration {
	
	boolean isGeneralRegime();
	int getDomain();
	int getYear();
	Period getPeriod(); 
	Administration getAdministration();
	IBankAccountContainer getBankAccountContainer();
	boolean isReplacement();
	String getReplacedNumber();
	boolean isComplementary();
	boolean isTaxRefundRegistry();
	double getProrata();

}
