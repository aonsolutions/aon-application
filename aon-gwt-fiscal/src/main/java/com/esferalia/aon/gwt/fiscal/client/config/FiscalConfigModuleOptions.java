package com.esferalia.aon.gwt.fiscal.client.config;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.ModuleOptions;

public class FiscalConfigModuleOptions extends  ModuleOptions<FiscalConfigModuleOptions> {

	private static final long serialVersionUID = -1477889480738439699L;
	
	private AonData aonData;
	private FiscalConfig fiscal;

	public AonData getAonData() {
		return aonData;
	}
	public FiscalConfigModuleOptions setAonData(AonData aonData) {
		this.aonData = aonData;
		return this;
	}
	
	public FiscalConfig getFiscal() {
		return fiscal;
	}
	public FiscalConfigModuleOptions setFiscal(FiscalConfig fiscal) {
		this.fiscal = fiscal;
		return this;
	}
}
