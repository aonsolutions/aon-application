package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.JsNotify;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
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

import net.aonsolutions.polymer.aon.widget.AonComboBox;

public class NotificationPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, NotificationPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);
    @UiField HTMLPanel emailPanel;
    @UiField HTMLPanel signPanel;
    @UiField HTMLPanel modePanel;
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
    	AonComboBox emailComboBox = new AonComboBox();
    	emailComboBox.setLabel("Cuenta de Correo");
    	emailComboBox.setWidth("300px");
    	emailComboBox.getElement().getStyle().setPaddingLeft(20, Unit.PX);
    	emailComboBox.getElement().getStyle().setPaddingRight(20, Unit.PX);
    	emailComboBox.setItemLabelPath("name");
    	emailComboBox.setItems(notify.getMailAccountList());
    	emailComboBox.setValue(notify.getMail());
    	emailComboBox.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
			
			@Override
			public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
				String r= "{\"email\":\""+emailComboBox.getValue() +"\"}";
				incidence.updateNotificationInfo(r);				
			}
		});
    	emailPanel.add(emailComboBox);
	}
    
    private void initSignComboBox(JsNotify notify) {
    	AonComboBox signComboBox = new AonComboBox();
    	signComboBox.setLabel("Firma de Correo");
    	signComboBox.setWidth("300px");
    	signComboBox.getElement().getStyle().setPaddingLeft(20, Unit.PX);
    	signComboBox.getElement().getStyle().setPaddingRight(20, Unit.PX);
    	signComboBox.setItemLabelPath("name");
    	signComboBox.setItems(notify.getSignatureList());
    	signComboBox.setValue(notify.getSign());
    	signComboBox.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
			
			@Override
			public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
				String r= "{\"sign\":\""+signComboBox.getValue() +"\"}";
				incidence.updateNotificationInfo(r);				
			}
		});
    	signPanel.add(signComboBox);
	}
    
    private void initModeComboBox(JsNotify notify) {
    	AonComboBox modeComboBox = new AonComboBox();
    	modeComboBox.setLabel("Modo");
    	modeComboBox.setWidth("300px");
    	modeComboBox.getElement().getStyle().setPaddingLeft(20, Unit.PX);
    	modeComboBox.getElement().getStyle().setPaddingRight(20, Unit.PX);
    	modeComboBox.setItemLabelPath("name");
    	modeComboBox.setItems(notify.getModeList());
    	modeComboBox.setValue(notify.getMode());
    	modeComboBox.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
			
			@Override
			public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
				String r= "{\"mode\":\""+modeComboBox.getValue() +"\"}";
				incidence.updateNotificationInfo(r);				
			}
		});
    	modePanel.add(modeComboBox);
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
    	commentHistoryToggle.setWidth("300px");
        commentHistoryToggle.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateHistory();
			}
		});
    	
        statusHistoryToggle.setChecked(notify.getStatusHistory().equals("1"));
        statusHistoryToggle.setWidth("300px");
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
        bccInput.setWidth("300px");
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
