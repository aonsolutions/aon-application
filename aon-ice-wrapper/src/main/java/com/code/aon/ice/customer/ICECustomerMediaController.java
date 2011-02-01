package com.code.aon.ice.customer;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.customer.controller.CustomerMediaController;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class ICECustomerMediaController extends CustomerMediaController {

    public void onSelect(RowSelectorEvent event){
    	this.onSelect(new ActionEvent(event.getComponent()));
    }
}