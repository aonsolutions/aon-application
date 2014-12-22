package com.code.aon.ui.registry.controller;

import com.code.aon.AonVersion;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.ui.form.LinesController;

public class RegistryTaxController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private TaxType taxType;

	public TaxType getTaxType() {
		return taxType;
	}

	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
	}

	public boolean isVat() {
		return getTaxType() == TaxType.VAT;
	}

	public boolean isRetention() {
		return getTaxType() == TaxType.RETENTION;
	}

}
