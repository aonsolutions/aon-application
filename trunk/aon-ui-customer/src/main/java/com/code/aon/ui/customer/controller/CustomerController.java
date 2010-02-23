package com.code.aon.ui.customer.controller;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.customer.Customer;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.stat.controller.RegistryStatEngineController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the customer maintenance.
 */
public class CustomerController extends RegistryController {
    /** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.ui.registry.i18n.messages";
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_customer_report";
	
	public String getReportTitle(){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX);
	}

	public void onCustomerData(ActionEvent e){
		RegistryStatEngineController controller =(RegistryStatEngineController)AonUtil.getRegisteredBean("registryStat");
		controller.setRegistry(((Customer)this.getTo()).getRegistry());
		controller.getRegistryData();
	}
		
}