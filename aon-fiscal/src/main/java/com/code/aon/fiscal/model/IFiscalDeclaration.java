package com.code.aon.fiscal.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.code.aon.common.AonException;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.IFiscalModelKey;

public interface IFiscalDeclaration {
	
	FiscalModel getHeader();
	Map<? extends IFiscalModelKey, FiscalModelDetail> getMap();
	Collection<FiscalModelDetail> getDetails();
	List<? extends IFiscalModelKey> getKeys();
	IFiscalModelKey getKey(String value);
	
	void initializeDetails();
	
	void addDetail(FiscalModelDetail detail);
	FiscalModelDetail getDetail(IFiscalModelKey key);
	FiscalModelDetail ensureDetail(IFiscalModelKey key);
	void calculate() throws AonException;
	
}
