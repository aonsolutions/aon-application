package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonTagPanel extends AonCustomDialog {
	
	public static interface AonTagPanelCallback {
		void onAccept(Tag tag);
		void onCancel();
	}

	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// Project Info
	private HTMLPanel content = new HTMLPanel("");
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonCustomTextBox descriptionTextBox = new AonCustomTextBox("Descripci\u00f3n");
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	public AonTagPanel(String domainName, int domain, String user, TagType tagType, AonTagPanelCallback callback) {
		initializeCommonService();
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.getElement().getStyle().setProperty("min-width", "35rem");
		
		setCaption("Nuevo Estado");
		
		Tag newTag = new Tag()
				.setDomain(domainId)
				.setType(tagType)
				.setStartDate(new Date())
				;
		
		show(newTag, callback);
		
	}
	
	public AonTagPanel(String domainName, int domain, String user, Tag tag, AonTagPanelCallback callback) {
		initializeCommonService();
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.getElement().getStyle().setProperty("min-width", "35rem");
		
		setCaption("Edici\u00f3n Estado");
		
		show(tag, callback);
		
	}
	
	public void show(Tag tag, AonTagPanelCallback callback) {
		content.setStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setProperty("padding", "1rem 0");
		
		HTMLPanel container = new HTMLPanel("");
		container.setStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "1rem");
		
		content.add(messagePanel);
		
		if(null != tag.getId()) descriptionTextBox.setValue(tag.getName());
		descriptionTextBox.getTextBox().setMaxLength(64);
		container.add(createRowPanel(descriptionTextBox, null));
		
		content.add(container);
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
		Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
    		
    		if(AonStringUtils.isBlank(descriptionTextBox.getValue())) {
    			okButton.setEnabled(true);
    			AonMessagePanel.showWarning(messagePanel, "El campo descripci\u00f3n es obligatorio");
    		} else if(descriptionTextBox.getValue().length() > 64) {
    			okButton.setEnabled(true);
    			AonMessagePanel.showWarning(messagePanel, "El campo descripci\u00f3n es muy largo. Maximo 64 caracteres");
    		} else {
    			
    			tag.setName(descriptionTextBox.getValue());
    			
    			commonService.saveTag(domainName, domainId, user, tag, new AsyncCallback<Tag>() {

    				@Override
    				public void onSuccess(Tag tagDB) {
    					hide();
    					callback.onAccept(tagDB);
    				}
    				@Override
    				public void onFailure(Throwable caught) {
    					AonMessagePanel.showError(messagePanel, caught.getMessage());
    					okButton.setEnabled(true);
    				}
    			});
    			
    		}
    		
    	});
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addClickHandler(e -> {
    		cancelButton.setEnabled(false);
    		hide();
			callback.onCancel();
    	});
    	buttons.add(cancelButton);
    	
		content.add(buttons);
		setWidget(content);
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				center();
				show();
				center();
			}
		});
		
	}
	
	private HTMLPanel createRowPanel(Widget w1, Widget w2) {
		HTMLPanel row = new HTMLPanel("");
		row.addStyleName(AON.CSS.aonItemFlex());
		
		row.add(w1);
		
		if(null != w2) row.add(w2);
		
		return row;
	}

}
