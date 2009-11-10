package com.code.aon.ui.customer.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.customer.enumeration.CustomerStatus;

public class CustomerCollectionsController {

	private List<SelectItem> customerStatuses;
	
	/**
	 * Gets the customer statuses.
	 * 
	 * @return the customer statuses
	 */
	public List<SelectItem> getCustomerStatuses() {
		if ( customerStatuses == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			customerStatuses = new LinkedList<SelectItem>();
			for( CustomerStatus status : CustomerStatus.values() ) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				customerStatuses.add(item);
			}			
		}
		return customerStatuses;
	}

}