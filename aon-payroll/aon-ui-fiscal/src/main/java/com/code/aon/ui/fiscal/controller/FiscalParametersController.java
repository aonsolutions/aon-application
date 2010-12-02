package com.code.aon.ui.fiscal.controller;

import com.code.aon.fiscal.enumeration.Administration;


public class FiscalParametersController {
	
	public static final String FISCAL_PARAMS_BEAN_NAME = "fiscalParams";
	
	private Integer defaultYear = 2010;
	private boolean taxRefundRegistry = true;
	private Administration defaultAdministration = Administration.BIZKAIA;
	
	public Integer getDefaultYear() {
		return defaultYear;
	}
	public void setDefaultYear(Integer defaultYear) {
		this.defaultYear = defaultYear;
	}
	public boolean isTaxRefundRegistry() {
		return taxRefundRegistry;
	}
	public void setTaxRefundRegistry(boolean taxRefundRegistry) {
		this.taxRefundRegistry = taxRefundRegistry;
	}
	public Administration getDefaultAdministration() {
		return defaultAdministration;
	}
	public void setDefaultAdministration(Administration defaultAdministration) {
		this.defaultAdministration = defaultAdministration;
	}
	
}
