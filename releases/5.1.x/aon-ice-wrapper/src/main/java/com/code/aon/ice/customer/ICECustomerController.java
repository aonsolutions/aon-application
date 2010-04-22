package com.code.aon.ice.customer;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.ui.customer.controller.CustomerController;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class ICECustomerController extends CustomerController{

    public void onSelect(RowSelectorEvent event){
    	this.onSelect(new ActionEvent(event.getComponent()));
    	FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "customer_form");
    }
}
