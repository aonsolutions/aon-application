package com.code.aon.fiscal.mod123;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.finance.Finance;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.IFiscalModelKey;
import com.code.aon.fiscal.enumeration.Mod123Key;
import com.code.aon.fiscal.model.IFiscalDeclaration;

public class Mod123 implements IFiscalDeclaration, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private FiscalModel fiscalModel;
	private Map<Mod123Key,FiscalModelDetail> map;
	
	public Mod123() {
		
	}
	
	@Override
	public FiscalModelType getType() {
		return FiscalModelType.M123;
	}

	public void initializeDetails() {
		clearMap();
		Administration admin = fiscalModel.getAdministration();
		for (Mod123Key key : Mod123Key.values()) {
			if (key.accept(admin,fiscalModel.getPeriod(),fiscalModel.getYear())) {
				ensureDetail(key);
			}
		}
	}
	
    @Override
    public void clearMap() {
    	map = null;
    }

	public Map<Mod123Key, FiscalModelDetail> getMap() {
		if (map == null) {
			map = new TreeMap<Mod123Key, FiscalModelDetail>();	
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
	public List<Mod123Key> getKeys() {
		return new LinkedList<Mod123Key>( getMap().keySet() );
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
		Mod123CalculatorFactory factory = new Mod123CalculatorFactory();
		int year = getHeader().getYear();
		Administration admin = getHeader().getAdministration(); 
		IMod123Calculator calculator = factory.getCalculator( year , admin );
		calculator.calculate(this);
	}
	
	@Override
	public void addDetail(FiscalModelDetail detail) {
		Mod123Key key = Mod123Key.getKeyWithValue( detail.getType() );
		getMap().put(key, detail);
	}
	
	@Override
	public IFiscalModelKey getKey(String value) {
		return Mod123Key.getKeyWithValue(value);
	}
	
	@Override
	public Finance getFinance() {
		return fiscalModel!=null?fiscalModel.getFinance():null;
	}
	
	@Override
	public double getResult() {
		Mod123CalculatorFactory factory = new Mod123CalculatorFactory();
		int year = getHeader().getYear();
		Administration admin = getHeader().getAdministration(); 
		IMod123Calculator calculator = factory.getCalculator( year , admin );
		return calculator.getResult(this);
	}
	
	@Override
	public boolean isDeclarationNegativeAvailable() {
		return true;
	}

	@Override
	public boolean isToDeductDeclarationAvailable() {
		return false;
	}

	@Override
	public boolean isWithoutActivityDeclarationAvailable() {
		return false;
	}

	@Override
	public boolean isNegative() {
		return (getResult() <= 0);
	}

	@Override
	public boolean isToDeduct() {
		return false;
	}

	@Override
	public boolean isCompensateDeclarationAvailable() {
		return false;
	}

	@Override
	public boolean isCompensate() {
		return false;
	}
	@Override
	public boolean isPayback() {
		return false;
	}
	@Override
	public boolean isPaybackDeclarationAvailable() {
		return false;
	}
}
