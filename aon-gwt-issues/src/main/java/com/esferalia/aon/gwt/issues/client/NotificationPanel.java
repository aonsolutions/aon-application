package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.JsNotify;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperSlider;
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
    @UiField PaperSlider logoSlider;
    @UiField PaperInput bccInput;
    
    Incidence incidence;
    public NotificationPanel(Incidence incidence) {
        initWidget(binder.createAndBindUi(this));
        this.incidence = incidence;
        incidence.getNotificationInfo(new AsyncCallback<JSON<JsNotify>>() {
			
			@Override
			public void onSuccess(JSON<JsNotify> result) {
				JsNotify notify = result.getData().get(0);
				initEmailComboBox(notify);
				initSignComboBox(notify);
				initModeComboBox(notify);
				initToggles(notify);
				initLogoSlider(notify);
				initBccInput(notify);  
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});				     
    }
    
    private void initEmailComboBox(JsNotify notify) {
		String arr= "[";
		for(Integer i = 0; i < notify.getMailAccountList().length(); i++){
			if(i > 0) arr = arr + " , ";
 			arr = arr + "\""+ notify.getMailAccountList().get(i).getName()+"\"";
		}
		arr = arr + "]";
    	
    	//get emails incidence ....
    	emailComboBox.setLabel("Cuenta de Correo");
    	emailComboBox.setItems(arr);
    	emailComboBox.setValue(notify.getMail());
    	emailComboBox.addValueChangedHandler(new ValueChangedEventHandler() {
			
			@Override
			public void onValueChanged(ValueChangedEvent event) {
				String r= "{\"email\":\""+emailComboBox.getValue() +"\"}";
				incidence.updateNotificationInfo(r);
			}
		});
	}
    
    private void initSignComboBox(JsNotify notify) {
		String arr= "[";
		for(Integer i = 0; i < notify.getSignatureList().length(); i++){
			if(i > 0) arr = arr + " , ";
 			arr = arr + "\""+ notify.getSignatureList().get(i).getName()+"\"";
		}
		arr = arr + "]";

		//get emails incidence ....
    	signComboBox.setLabel("Firma de Correo");
    	signComboBox.setItems(arr);
    	signComboBox.setValue(notify.getSign());
    	signComboBox.addValueChangedHandler(new ValueChangedEventHandler() {
			
			@Override
			public void onValueChanged(ValueChangedEvent event) {
				String r= "{\"sign\":\""+signComboBox.getValue() +"\"}";
				incidence.updateNotificationInfo(r);
			}
		});
	}
    
    private void initModeComboBox(JsNotify notify) {
		String arr= "[";
		for(Integer i = 0; i < notify.getModeList().length(); i++){
			if(i > 0) arr = arr + " , ";
 			arr = arr + "\""+ notify.getModeList().get(i).getName()+"\"";
		}
		arr = arr + "]";
    	//get emails incidence ....
    	modeComboBox.setLabel("Modo");
    	modeComboBox.setItems(arr);
    	modeComboBox.setValue(notify.getMode());
    	modeComboBox.addValueChangedHandler(new ValueChangedEventHandler() {
			
			@Override
			public void onValueChanged(ValueChangedEvent event) {
				String r= "{\"mode\":\""+modeComboBox.getValue() +"\"}";
				incidence.updateNotificationInfo(r);
			}
		});
    }
    
    private void initToggles(JsNotify notify){
        logoToggle.setChecked(notify.getLogo().equals("1"));
    	logoToggle.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				if(logoToggle.getChecked())
					logoSlider.setDisabled(false);
				else logoSlider.setDisabled(true);
				updateLogo();
			}
		});
    
        commentHistoryToggle.setChecked(notify.getCommentHistory().equals("1"));
    	commentHistoryToggle.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateHistory();
			}
		});
    	
        statusHistoryToggle.setChecked(notify.getStatusHistory().equals("1"));
        statusHistoryToggle.addChangeHandler(new ChangeEventHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				updateHistory();
			}
		});
        
        notifyOpenToggle.setChecked(notify.getOpen().equals("1"));
        notifyOpenToggle.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateNotify();
			}
		});
        
        notifyCloseToggle.setChecked(notify.getClose().equals("1"));
        notifyCloseToggle.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateNotify();
			}
		});
        
        notifyReopenToggle.setChecked(notify.getReopen().equals("1"));
        notifyReopenToggle.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateNotify();
			}
		});
        
        notifyCommentToggle.setChecked(notify.getComment().equals("1"));
        notifyCommentToggle.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateNotify();
			}
		});
    }
    
    private void initBccInput(JsNotify notify) {
        bccInput.setLabel("Incluir en BCC"); 
        bccInput.setValue(notify.getBcc());
        bccInput.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String r= "{\"bcc\":\""+bccInput.getValue() +"\"}";
				incidence.updateNotificationInfo(r);
			}
		});
	}
    
    private void initLogoSlider(JsNotify notify) {
    	if(notify.getLogo().equals("1"))
			logoSlider.setDisabled(false);
		else logoSlider.setDisabled(true);
    	
    	logoSlider.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateLogo();
			}
		});
	}
    
    private void updateLogo(){
		String r= "{\"logo\":\""+logoToggle.getChecked() +"\","
				+ "\"logo_percentage\":\""+logoSlider.getValue() +"\"}";
		incidence.updateNotificationInfo(r);
    }
    
    private void updateHistory(){
		String r= "{\"commentHistory\":\""+commentHistoryToggle.getChecked() +"\","
				+ "\"statusHistory\":\""+statusHistoryToggle.getChecked() +"\"}";
		incidence.updateNotificationInfo(r);
    }
    
    private void updateNotify(){
		String r= "{\"notifyOpen\":\""+notifyOpenToggle.getChecked() +"\","
				+ "\"notifyClose\":\""+notifyCloseToggle.getChecked() +"\","
				+ "\"notifyReopen\":\""+notifyReopenToggle.getChecked() +"\","
				+ "\"notifyComment\":\""+notifyCommentToggle.getChecked() +"\"}";
		incidence.updateNotificationInfo(r);
    }
    
}
