package com.esferalia.aon.payroll;

import java.util.Date;

public class IrpfOutcome {
	
	private IrpfData irpfData;
	private IrpfResult irpfResult;
	private IrpfRegularization irpfRegularization;
	
	
	private String nif;
	private int  birthYear;
	
	
	
	public IrpfResult getIrpfResult() {
		return irpfResult;
	}
	public void setIrpfResult(IrpfResult irpfResult) {
		this.irpfResult = irpfResult;
	}
	public IrpfRegularization getIrpfRegularization() {
		return irpfRegularization;
	}
	public void setIrpfRegularization(IrpfRegularization irpfRegularization) {
		this.irpfRegularization = irpfRegularization;
	}
	
	public IrpfData getIrpfData() {
		return irpfData;
	}
	
	public void setIrpfData(IrpfData irpfData) {
		this.irpfData = irpfData;
	}
	
	
	public String getNif() {
		return nif;
	}
	
	public void setNif(String nif) {
		this.nif = nif;
	}
	
	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}
	
	public int getBirthYear() {
		return birthYear;
	}

}
