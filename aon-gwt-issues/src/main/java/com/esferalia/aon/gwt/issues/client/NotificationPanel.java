package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperToggleButton;
import com.vaadin.polymer.vaadin.widget.VaadinComboBox;

public class NotificationPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, NotificationPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);
    
    @UiField VaadinComboBox emailComboBox;
    @UiField VaadinComboBox signComboBox;
    @UiField VaadinComboBox modeComboBox;
    @UiField PaperToggleButton logoToggle;
    @UiField PaperToggleButton commentHistoryToggle;
    @UiField PaperToggleButton statusHistoryToggle;
    @UiField PaperInput bccInput;
    
    public NotificationPanel(Incidence incidence) {
        initWidget(binder.createAndBindUi(this));
     
        initEmailComboBox();
        initSignComboBox();
        initModeComboBox();
        
        bccInput.setLabel("Incluir en BCC");
        
    }
    
    private void initEmailComboBox() {
    	//get emails incidence ....
    	emailComboBox.setLabel("Cuenta de Correo");
    	emailComboBox.setItems("['aaaaa','bbbbb','ccccc']");
	}
    
    private void initSignComboBox() {
    	//get emails incidence ....
    	signComboBox.setLabel("Firma de Correo");
    	signComboBox.setItems("['aaaaa','bbbbb','ccccc']");
	}
    
    private void initModeComboBox() {
    	//get emails incidence ....
    	modeComboBox.setLabel("Modo");
    	modeComboBox.setItems("[aaaaa,bbbbb,ccccc]");
	}
}
