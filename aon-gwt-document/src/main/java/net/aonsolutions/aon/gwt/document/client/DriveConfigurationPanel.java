package net.aonsolutions.aon.gwt.document.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

public class DriveConfigurationPanel extends Composite {

    interface Binder extends UiBinder<HTMLPanel , DriveConfigurationPanel> {
    	
    }
    
    @UiField HTMLPanel panel;
    
        
    private static Binder binder = GWT.create(Binder.class);
	
    public DriveConfigurationPanel() {
    	initWidget(binder.createAndBindUi(this));
    	
    }
 

}
