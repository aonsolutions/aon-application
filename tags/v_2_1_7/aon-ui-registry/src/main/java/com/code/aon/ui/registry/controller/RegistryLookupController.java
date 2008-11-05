package com.code.aon.ui.registry.controller;

import java.util.Map;

import com.code.aon.common.ITransferObject;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.LookupController;

public class RegistryLookupController extends LookupController {
	
	/** The Constant REGISTRY_FULL_NAME used to retrieve the complete name of the registry using the lookup. */
	private static final String REGISTRY_FULL_NAME = "Registry_full_name";

	@Override
	protected void customizeLookupMap(ITransferObject to, Map<String, Object> map) {
		Registry registry = (Registry)to;
		map.put(REGISTRY_FULL_NAME, ((registry.getName() == null) ? "" : registry.getName()) + " " + ((registry.getSurname() == null) ? "" : registry.getSurname()) );
	}
}