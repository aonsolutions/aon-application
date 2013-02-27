package com.code.aon.fiscal.mod130;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.code.aon.common.AonException;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.finance.Finance;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.IFiscalModelKey;
import com.code.aon.fiscal.enumeration.Mod130Key;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.model.IFiscalDeclaration;

public class Mod130 implements IFiscalDeclaration {

	private FiscalModel fiscalModel;
	private Map<Mod130Key,FiscalModelDetail> map;
	private boolean permanentAddressChanges;
	
	public Mod130() {
		
	}
	@Override
	public FiscalModelType getType() {
		return FiscalModelType.M130;
	}
	public boolean isPermanentAddressChanges() {
		return permanentAddressChanges;	
	}
	public void setPermanentAddressChanges(boolean permanentAddressChanges) {
		this.permanentAddressChanges = permanentAddressChanges;
	}

	public void initializeDetails() {
		Administration admin = fiscalModel.getAdministration();
		for (Mod130Key key : Mod130Key.values()) {
			if (key.accept(admin)) {
				ensureDetail(key);
			}
		}
	}

	public Map<Mod130Key, FiscalModelDetail> getMap() {
		if (map == null) {
			map = new TreeMap<Mod130Key, FiscalModelDetail>();	
		}
		return map;
	}

	@Override
	public FiscalModel getHeader() {
		return fiscalModel;
	}
	@Override
	public void setHeader(FiscalModel fiscalModel) {
		this.fiscalModel = fiscalModel;
	}
	public void setFiscalModel(FiscalModel fiscalModel) {
		this.fiscalModel = fiscalModel;
	}

	@Override
	public List<Mod130Key> getKeys() {
		return new LinkedList<Mod130Key>( getMap().keySet() );
	}
	
	@Override
	public Collection<FiscalModelDetail> getDetails() {
		return getMap().values();
	}
	
	@Override
	public FiscalModelDetail getDetail(IFiscalModelKey key) {
		return getMap().get(key);
	}
	
	@Override
	public FiscalModelDetail ensureDetail(IFiscalModelKey key) {
		FiscalModelDetail detail = getDetail(key);
		if (detail == null) {
			detail = new FiscalModelDetail();
			detail.setFiscalModel(getHeader());
			detail.setType(key.getValue());
			addDetail(detail);
		}
		return detail;
	}

	@Override
	public void calculate() throws AonException {
		Mod130CalculatorFactory factory = new Mod130CalculatorFactory();
		int year = getHeader().getYear();
		Administration admin = getHeader().getAdministration(); 
		IMod130Calculator calculator = factory.getCalculator( year , admin );
		calculator.calculate(this);
	}
	
	@Override
	public void addDetail(FiscalModelDetail detail) {
		Mod130Key key = Mod130Key.getKeyWithValue( detail.getType() );
		getMap().put(key, detail);
	}
	
	@Override
	public IFiscalModelKey getKey(String value) {
		return Mod130Key.getKeyWithValue(value);
	}

	@Override
	public Finance getFinance() {
		return fiscalModel!=null?fiscalModel.getFinance():null;
	}
	
	@Override
	public double getResult() {
		Mod130CalculatorFactory factory = new Mod130CalculatorFactory();
		int year = getHeader().getYear();
		Administration admin = getHeader().getAdministration(); 
		IMod130Calculator calculator = factory.getCalculator( year , admin );
		return calculator.getResult(this);
	}

	@Override
	public boolean isDeclarationNegativeAvailable() {
		return (getHeader().getPeriod() == Period.T4);
	}

	@Override
	public boolean isToDeductDeclarationAvailable() {
		return (getHeader().getPeriod() == Period.T1
				|| getHeader().getPeriod() == Period.T2
				|| getHeader().getPeriod() == Period.T3);
	}

	@Override
	public boolean isWithoutActivityDeclarationAvailable() {
		return false;
	}
	
	@Override
	public boolean isNegative() {
		return (isDeclarationNegativeAvailable() && getResult() < 0);
	}

	@Override
	public boolean isToDeduct() {
		return (isToDeductDeclarationAvailable()  && getResult() < 0);
	}
	
}
