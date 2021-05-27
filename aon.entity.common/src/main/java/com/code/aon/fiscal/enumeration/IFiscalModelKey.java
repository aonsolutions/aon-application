package com.code.aon.fiscal.enumeration;

import java.util.Locale;

import com.code.aon.config.enumeration.Administration;

public interface IFiscalModelKey {
	
	boolean accept(Administration administration, Period period, int year);
	String getValue();
	boolean isDifEnabled();
	int getLevel();
	boolean isTitle();
	boolean isTotal();
	boolean isDescriptionEnabled();
	String getName(Locale locale);
	
}
