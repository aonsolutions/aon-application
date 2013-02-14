package com.code.aon.fiscal.enumeration;

import com.code.aon.config.enumeration.Administration;

public interface IFiscalModelKey {
	
	boolean accept(Administration administration);
	String getValue();
	boolean isDifEnabled();
	int getLevel();
	boolean isTitle();
	boolean isTotal();
	boolean isDescriptionEnabled();
	
}
