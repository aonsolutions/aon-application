package com.code.aon.ui.purchase.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.purchase.enumeration.PurchaseStatus;

/**
 * Controller used to get Collections related with clasess in <code>com.code.aon.purchase</code>
 * 
 * @author Consulting & Development. Joseba Urkiri - 21-dic-2005
 */
public class PurchaseCollectionsController {
	
    /**
     * Gets the purchase statuses.
     * 
     * @return the purchase statuses
     */
    public List<SelectItem> getPurchaseStatuses() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    LinkedList<SelectItem> types = new LinkedList<SelectItem>();
        for( PurchaseStatus type : PurchaseStatus.values() ) {
            String name = type.getName(locale); 
            SelectItem item = new SelectItem(type, name);
            types.add( item );
        }
        return types;
    }
	
    /**
     * Gets the purchase detail statuses.
     * 
     * @return the purchase detail statuses
     */
    public List<SelectItem> getPurchaseDetailStatuses() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    LinkedList<SelectItem> types = new LinkedList<SelectItem>();
        for( PurchaseDetailStatus type : PurchaseDetailStatus.values() ) {
            String name = type.getName(locale); 
            SelectItem item = new SelectItem(type, name);
            types.add( item );
        }
        return types;
    }
}