package com.code.aon.ui.commercial.controller;

import java.util.ResourceBundle;

import com.code.aon.ui.commercial.ICommercialMessages;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the target maintenance.
 */
public class TargetController extends RegistryController {

	private ResourceBundle bundle;

	public TargetController() {
		setBundleName(ICommercialMessages.BUNDLE_KEY);
	}
	
	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundleName(String bundleName) {
		this.bundle = AonUtil.getResourceBundle(bundleName);
	}	
		
}