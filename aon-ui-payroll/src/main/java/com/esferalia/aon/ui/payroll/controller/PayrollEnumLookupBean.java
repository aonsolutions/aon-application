package com.esferalia.aon.ui.payroll.controller;

import java.util.Collections;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.sepe.SSCodeTablesWriter.ISSEnum;


public class PayrollEnumLookupBean {
	
	private String code;
	private ISSEnum payrollEnum;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ISSEnum getPayrollEnum() {
		return payrollEnum;
	}

	public void setPayrollEnum(ISSEnum payrollEnum) {
		this.payrollEnum = payrollEnum;
	}

	public void onCancel(ActionEvent event) {
		
	}

	public void onSearch(ActionEvent event) {
		
	}
	
	public void onSelect(ActionEvent event) {
		
	}
	
	public void onClear( ActionEvent event ) {
		
	}
	
	public List<ITransferObject> autocomplete( Object value, UIComponent component ) {
    	return Collections.emptyList();
    }	
	
	
}