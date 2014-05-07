package com.esferalia.aon.file.payroll.contract.pdf;

public interface IContractFieldName {
	
	public boolean isOverridable();
	
	public boolean isCheck();
	
	public boolean isCommonValue();
	
	public String getValue();
	
	public IContractFieldName[] getCompositeValues();
	
}