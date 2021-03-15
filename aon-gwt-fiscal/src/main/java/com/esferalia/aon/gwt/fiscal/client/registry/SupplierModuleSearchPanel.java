package com.esferalia.aon.gwt.fiscal.client.registry;

import com.esferalia.aon.occam.api.model.RegistryParams;

public class SupplierModuleSearchPanel extends RegistryModuleSearchPanel {

	public SupplierModuleSearchPanel(final RegistryModuleOptions opt) {
		super(opt);
	}
	
	public RegistryParams getParams( final RegistryModuleOptions opt) {
		RegistryParams params = super.getParams(opt);
		return params;
	}

}
