package com.esferalia.aon.gwt.template.client.scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;


public class SouthPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, SouthPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);
    protected static final String nothing = "NO HAY DATOS RELACIONADOS";  
    
    @UiField VerticalPanel vertical;
    @UiField Label title;
    
    public SouthPanel() {   
        initWidget(binder.createAndBindUi(this));
    }
}
