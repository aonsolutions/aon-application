package com.code.aon.ui.account.bridge.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.account.bridge.enumeration.ProductAccountType;
import com.code.aon.account.bridge.enumeration.TaxAccountType;

public class AccountBridgeCollectionsController {
	
	private List<SelectItem> taxAccountTypes;
	private List<SelectItem> productAccountTypes;
	
	public List<SelectItem> getTaxAccountTypes() {
		if ( taxAccountTypes == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			taxAccountTypes = new LinkedList<SelectItem>();
			for( TaxAccountType type : TaxAccountType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				taxAccountTypes.add(item);
			}
		}
		return taxAccountTypes;
	}

	public List<SelectItem> getProductAccountTypes() {
		if ( productAccountTypes == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			productAccountTypes = new LinkedList<SelectItem>();
			for( ProductAccountType type : ProductAccountType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				productAccountTypes.add(item);
			}
		}
		return productAccountTypes;
	}
}
