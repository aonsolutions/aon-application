package com.code.aon.ui.supplier.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.supplier.enumeration.SupplierStatus;

public class SupplierCollectionsController {

	private List<SelectItem> supplierStatuses;
	
    /**
     * Gets the supplier statuses.
     * 
     * @return the supplier statuses
     */
    public List<SelectItem> getSupplierStatuses() {
    	if ( this.supplierStatuses == null ) {
	        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	        supplierStatuses = new LinkedList<SelectItem>();
	        for( SupplierStatus type : SupplierStatus.values() ) {
	            String name = type.getName(locale); 
	            SelectItem item = new SelectItem(type, name);
	            supplierStatuses.add( item );
	        }
    	}
        return supplierStatuses;
    }
    
}