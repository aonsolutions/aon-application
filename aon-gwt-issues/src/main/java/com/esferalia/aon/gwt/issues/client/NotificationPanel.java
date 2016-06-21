package com.esferalia.aon.gwt.issues.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

public class NotificationPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, NotificationPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);
    
    public NotificationPanel() {
        initWidget(binder.createAndBindUi(this));
    }
    
    
}
