package com.code.aon.fiscal.activity;

import com.code.aon.common.AonException;
import com.code.aon.fiscal.FiscalActivityInfo;
import com.code.aon.fiscal.enumeration.FiscalActivityInfoKey;

public interface IModuleCalculator {

	public void changeVatModule(FiscalActivityInfo info) throws AonException;
	public void changeIrpfModule(FiscalActivityInfo info) throws AonException;
	public void changeDetailModule(FiscalActivityInfo info) throws AonException;
	public void calculate() throws AonException;
	public void calculateIrpf();
	public void calculateVat();
	public FiscalActivityInfoKey[] getActivityKeys(int year, String epigraph);
	public FiscalActivityInfoKey[] getDetailedKeys(FiscalActivityInfoKey key);

}
