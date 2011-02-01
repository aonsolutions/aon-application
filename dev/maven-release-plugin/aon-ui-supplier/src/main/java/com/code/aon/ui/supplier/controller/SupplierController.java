package com.code.aon.ui.supplier.controller;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import com.code.aon.ui.registry.controller.RegistryController;

/**
 * Controller used in the supplier maintenance.
 */
public class SupplierController extends RegistryController {

	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.ui.registry.i18n.messages";
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_supplier_report";
	
	public String getReportTitle(){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX);
	}
}