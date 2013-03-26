package com.code.aon.fiscal.activity;

import com.code.aon.fiscal.FiscalActivityInfo;
import com.code.aon.fiscal.enumeration.FiscalActivityInfoKey;

public interface IModuleCalculator {

	public void changeVatModule(FiscalActivityInfo info);
	public void changeIrpfModule(FiscalActivityInfo info);
	public void changeDetailModule(FiscalActivityInfo info);
	public void calculate();
	public void calculateIrpf();
	public void calculateVat();
	public FiscalActivityInfoKey[] getActivityKeys(int year, String epigraph);
	public FiscalActivityInfoKey[] getDetailedKeys(FiscalActivityInfoKey key);

}
