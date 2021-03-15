package com.esferalia.aon.gwt.fiscal.client.registry;

import com.esferalia.aon.occam.api.model.RegistryParams;

public class CustomerModuleSearchPanel extends RegistryModuleSearchPanel {

	public CustomerModuleSearchPanel(final RegistryModuleOptions opt) {
		super(opt);
	}
	
	public RegistryParams getParams( final RegistryModuleOptions opt) {
		RegistryParams params = super.getParams(opt);
		return params;
	}
	
	
}
