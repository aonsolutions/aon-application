package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.vaadin.polymer.iron.widget.IronCollapse;

public class ConfPanel extends Composite {

    interface Binder extends UiBinder<HTMLPanel , ConfPanel> {
    	
    }
    
    @UiField HTMLPanel panel;
    
    @UiField Button gitHeading;
    @UiField IronCollapse gitCollapse;
    
    private static Binder binder = GWT.create(Binder.class);
	
	Incidence incidence;
	
    public ConfPanel(Incidence incidence) {
    	this.incidence = incidence;
    	initWidget(binder.createAndBindUi(this));
   			
    }
    
    

}
