package com.esferalia.aon.gwt.fiscal.client.config;

import com.esferalia.aon.gwt.fiscal.client.ModuleOptions;

public class FiscalConfigModuleOptions extends  ModuleOptions<FiscalConfigModuleOptions> {

	private static final long serialVersionUID = -1477889480738439699L;
	
	private FiscalConfig fiscal;

	public FiscalConfig getFiscal() {
		return fiscal;
	}
	public FiscalConfigModuleOptions setFiscal(FiscalConfig fiscal) {
		this.fiscal = fiscal;
		return this;
	}
}
