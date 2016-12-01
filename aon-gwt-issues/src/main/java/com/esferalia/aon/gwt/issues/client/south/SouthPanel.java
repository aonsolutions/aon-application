package com.esferalia.aon.gwt.issues.client.south;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


public class SouthPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, SouthPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);
    protected static final String nothing = "NO HAY DATOS RELACIONADOS";  
    
    @UiField VerticalPanel vertical;
    
    public SouthPanel() {   
        initWidget(binder.createAndBindUi(this));
    }
}
