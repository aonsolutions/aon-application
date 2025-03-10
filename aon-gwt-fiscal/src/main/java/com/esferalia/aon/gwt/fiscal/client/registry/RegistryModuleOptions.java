package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.Optional;

import com.esferalia.aon.gwt.fiscal.client.ModuleOptions;

public class RegistryModuleOptions extends  ModuleOptions<RegistryModuleOptions> {

	private static final long serialVersionUID = -3200864485405861259L;

	private Integer registryId;
	
	public Optional<Integer> getRegistryId() {
		return Optional.ofNullable(registryId);
	}
	public RegistryModuleOptions setRegistryId(Integer registryId) {
		this.registryId = registryId;
		return this;
	}
	
}
