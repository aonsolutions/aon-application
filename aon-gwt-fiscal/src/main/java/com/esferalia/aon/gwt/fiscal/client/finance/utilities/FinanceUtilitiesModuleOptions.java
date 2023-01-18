package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.ModuleOptions;

public class FinanceUtilitiesModuleOptions extends  ModuleOptions<FinanceUtilitiesModuleOptions> {

	private static final long serialVersionUID = 8565229219550096670L;

	private ModuleCallback externalCallback;
	
	public ModuleCallback getExternalCallback() {
		return externalCallback;
	}

	public boolean hasExternalCallback() {
		return getExternalCallback() != null;
	}

	public FinanceUtilitiesModuleOptions setExternalCallback(ModuleCallback externalCallback) {
		this.externalCallback = externalCallback;
		return this;
	}

}
