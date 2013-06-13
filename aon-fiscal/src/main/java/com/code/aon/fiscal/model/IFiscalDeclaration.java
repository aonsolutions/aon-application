package com.code.aon.fiscal.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.code.aon.common.AonException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.IFiscalModelKey;

public interface IFiscalDeclaration {
	FiscalModelType getType();
	FiscalModel getHeader();
	void setHeader(FiscalModel fiscalModel);
	
	Map<? extends IFiscalModelKey, FiscalModelDetail> getMap();
	void clearMap();
	Collection<FiscalModelDetail> getDetails();
	List<? extends IFiscalModelKey> getKeys();
	IFiscalModelKey getKey(String value);
	
	void initializeDetails() throws ManagerBeanException;
	
	void addDetail(FiscalModelDetail detail);
	FiscalModelDetail getDetail(IFiscalModelKey key);
	FiscalModelDetail ensureDetail(IFiscalModelKey key);
	void calculate() throws AonException;
	
	double getResult();
	Finance getFinance();
	
	boolean isDeclarationNegativeAvailable();
	boolean isToDeductDeclarationAvailable();
	boolean isWithoutActivityDeclarationAvailable();
	public boolean isNegative();
	public boolean isToDeduct();
	
	
}
