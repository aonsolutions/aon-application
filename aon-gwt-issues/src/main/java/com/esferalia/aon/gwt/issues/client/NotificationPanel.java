package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperToggleButton;
import com.vaadin.polymer.paper.widget.event.ChangeEvent;
import com.vaadin.polymer.paper.widget.event.ChangeEventHandler;
import com.vaadin.polymer.vaadin.widget.VaadinComboBox;
import com.vaadin.polymer.vaadin.widget.event.ValueChangedEvent;
import com.vaadin.polymer.vaadin.widget.event.ValueChangedEventHandler;

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
    @UiField PaperToggleButton notifyOpenToggle;
    @UiField PaperToggleButton notifyCloseToggle;
    @UiField PaperToggleButton notifyReopenToggle;
    @UiField PaperToggleButton notifyCommentToggle;
    @UiField PaperInput bccInput;
    
    Incidence incidence;
    public NotificationPanel(Incidence incidence) {
        initWidget(binder.createAndBindUi(this));
        this.incidence = incidence;

		initEmailComboBox();
		initSignComboBox();
		initModeComboBox();
		initToggles();
		initBccInput();  				
		
             
    }
    
    private void initEmailComboBox() {
    	//get emails incidence ....
    	emailComboBox.setLabel("Cuenta de Correo");
    	emailComboBox.setItems("[]");
    	//emailComboBox.setValue(notify.getMail());
    	emailComboBox.addValueChangedHandler(new ValueChangedEventHandler() {
			
			@Override
			public void onValueChanged(ValueChangedEvent event) {
				String r= "{\"email\":\""+emailComboBox.getValue() +"\"}";
				incidence.updateNotificationInfo(r);
			}
		});
	}
    
    private void initSignComboBox() {
    	//get emails incidence ....
    	signComboBox.setLabel("Firma de Correo");
    	signComboBox.setItems("[]");
    	//signComboBox.setValue(notify.getSign());
    	signComboBox.addValueChangedHandler(new ValueChangedEventHandler() {
			
			@Override
			public void onValueChanged(ValueChangedEvent event) {
				String r= "{\"sign\":\""+signComboBox.getValue() +"\"}";
				incidence.updateNotificationInfo(r);
			}
		});
	}
    
    private void initModeComboBox() {
    	//get emails incidence ....
    	modeComboBox.setLabel("Modo");
    	modeComboBox.setItems("[aaaaa,bbbbb,ccccc]");
    	modeComboBox.addValueChangedHandler(new ValueChangedEventHandler() {
			
			@Override
			public void onValueChanged(ValueChangedEvent event) {
				String r= "{\"mode\":\""+modeComboBox.getValue() +"\"}";
				incidence.updateNotificationInfo(r);
			}
		});
    }
    
    private void initToggles(){
    	logoToggle.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String r= "{\"logo\":\""+logoToggle.getValue() +"\"}";
				incidence.updateNotificationInfo(r);
			}
		});
    
    	commentHistoryToggle.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String r= "{\"commentHistory\":\""+commentHistoryToggle.getValue() +"\"}";
				incidence.updateNotificationInfo(r);
			}
		});
    	
        statusHistoryToggle.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String r= "{\"statusHistory\":\""+statusHistoryToggle.getValue() +"\"}";
				incidence.updateNotificationInfo(r);
			}
		});
        
        notifyOpenToggle.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String r= "{\"notifyOpen\":\""+notifyOpenToggle.getValue() +"\"}";
				incidence.updateNotificationInfo(r);
			}
		});
        
        notifyCloseToggle.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String r= "{\"notifyClose\":\""+notifyCloseToggle.getValue() +"\"}";
				incidence.updateNotificationInfo(r);
			}
		});
        
        notifyReopenToggle.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String r= "{\"notifyReopen\":\""+notifyReopenToggle.getValue() +"\"}";
				incidence.updateNotificationInfo(r);
			}
		});
        
        notifyCommentToggle.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String r= "{\"notifyComment\":\""+notifyCommentToggle.getValue() +"\"}";
				incidence.updateNotificationInfo(r);
			}
		});
    }
    
    private void initBccInput() {
        bccInput.setLabel("Incluir en BCC");        
        bccInput.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String r= "{\"bcc\":\""+bccInput.getValue() +"\"}";
				incidence.updateNotificationInfo(r);
			}
		});
	}
}
