package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import java.util.Optional;

import com.esferalia.aon.gwt.fiscal.client.ModuleOptions;

public class InvoiceFeeModuleOptions extends  ModuleOptions<InvoiceFeeModuleOptions> {

	private static final long serialVersionUID = 8565229219550096670L;

	private Integer registryId;
	
	public Optional<Integer> getRegistryId() {
		return Optional.ofNullable(registryId);
	}
	public InvoiceFeeModuleOptions setRegistryId(Integer registryId) {
		this.registryId = registryId;
		return this;
	}
	

}
