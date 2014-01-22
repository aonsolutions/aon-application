package com.code.aon.fiscal.mod111;

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
import com.code.aon.fiscal.enumeration.Mod111Key;
import com.code.aon.fiscal.model.IFiscalDeclaration;

public class Mod111 implements IFiscalDeclaration {

	private FiscalModel fiscalModel;
	private Map<Mod111Key,FiscalModelDetail> map;
	
	
	public Mod111() {
		
	}

	@Override
	public FiscalModelType getType() {
		return FiscalModelType.M111;
	}

	public void initializeDetails() {
		clearMap();
		Administration admin = fiscalModel.getAdministration();
		for (Mod111Key key : Mod111Key.values()) {
			if (key.accept(admin)) {
				ensureDetail(key);
			}
		}
	}
    @Override
    public void clearMap() {
    	map = null;
    }
    
	public Map<Mod111Key, FiscalModelDetail> getMap() {
		if (map == null) {
			map = new TreeMap<Mod111Key, FiscalModelDetail>();	
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
	public List<Mod111Key> getKeys() {
		return new LinkedList<Mod111Key>( getMap().keySet() );
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
		Mod111CalculatorFactory factory = new Mod111CalculatorFactory();
		int year = getHeader().getYear();
		Administration admin = getHeader().getAdministration(); 
		IMod111Calculator calculator = factory.getCalculator( year , admin );
		calculator.calculate(this);
	}
	
	@Override
	public void addDetail(FiscalModelDetail detail) {
		Mod111Key key = Mod111Key.getKeyWithValue( detail.getType() );
		getMap().put(key, detail);
	}

	@Override
	public IFiscalModelKey getKey(String value) {
		return Mod111Key.getKeyWithValue(value);
	}

	@Override
	public Finance getFinance() {
		return fiscalModel!=null?fiscalModel.getFinance():null;
	}
	
	@Override
	public double getResult() {
		Mod111CalculatorFactory factory = new Mod111CalculatorFactory();
		int year = getHeader().getYear();
		Administration admin = getHeader().getAdministration(); 
		IMod111Calculator calculator = factory.getCalculator( year , admin );
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
