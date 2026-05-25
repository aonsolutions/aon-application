package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonMediaPanel extends HTMLPanel {
	
	// Callback
	
	public static interface AonMediaPanelCallback {
		void onAccept(RegistryMedia media);
		void onCancel();
	}

	// CommonService
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// Variables
	
	private final static String EMPTY_STRING = "";
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	private AonMediaPanelCallback callback;
	private RegistryMedia registryMedia;
	
	// Media Info
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	private AonCustomTextBox value = new AonCustomTextBox("Valor");
	private AonCustomTextBox comment = new AonCustomTextBox("Comentarios");
	private AonCustomToogleButton admin = new AonCustomToogleButton("Administrativo");
	private AonCustomToogleButton commercial = new AonCustomToogleButton("Comercial");
	private AonCustomToogleButton tecnical = new AonCustomToogleButton("T\u00e9cnico");
	
	// Constructor
	
	public AonMediaPanel(String domainName, Integer domain, String user, Integer registry, AonMediaPanelCallback callback) {
		super(EMPTY_STRING);
		this.registryMedia = new RegistryMedia().setDomain(domain).setRegistry(registry);
		aonMediaPanel(domainName, domain, user, callback);
	}
	
	public AonMediaPanel(String domainName, Integer domain, String user, RegistryMedia registryMedia, AonMediaPanelCallback callback) {
		super(EMPTY_STRING);
		this.registryMedia = registryMedia;
		aonMediaPanel(domainName, domain, user, callback);
	}
	
	private void aonMediaPanel(String domainName, Integer domain, String user, AonMediaPanelCallback callback) {
		initializeCommonService();
		
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.callback = callback;
		
		show();
	}
	
	public void show() {
		// Message Panel
		setStyleName(AON.CSS.aonFlexColumn2());
		getElement().getStyle().setProperty("padding", "1rem 0");
		add(messagePanel);
		
		HTMLPanel container = new HTMLPanel(EMPTY_STRING);
		container.setStyleName(AON.CSS.aonFlexColumn2());
		container.getElement().getStyle().setProperty("padding", "0 1rem");
		container.getElement().getStyle().setProperty("min-width", "25rem");
		
		// First Row
		HTMLPanel row = new HTMLPanel(EMPTY_STRING);
		row.setStyleName(AON.CSS.aonItemFlex());
		row.getElement().getStyle().setProperty("gap", "2rem");
		
		for(int i=0; i<MediaType.values().length; i++) type.addItem(MediaType.values()[i].getDescription(), MediaType.values()[i].name());
		admin.getElement().getStyle().setProperty("max-width", "6rem");
		
		row.add(type);
		row.add(value);
		container.add(row);
		
		// Second Row
		HTMLPanel row2 = new HTMLPanel(EMPTY_STRING);
		row2.setStyleName(AON.CSS.aonItemFlex());
		row2.getElement().getStyle().setProperty("gap", "2rem");
		
		commercial.getElement().getStyle().setProperty("max-width", "6rem");
		
		row2.add(comment);
		container.add(row2);
		
		// Third Row
		HTMLPanel row3 = new HTMLPanel(EMPTY_STRING);
		row3.setStyleName(AON.CSS.aonItemFlex());
		row3.getElement().getStyle().setProperty("gap", "2rem");
		
		tecnical.getElement().getStyle().setProperty("max-width", "6rem");
		
		row3.add(admin);
		row3.add(commercial);
		row3.add(tecnical);
		container.add(row3);
		
		// Fill info
		if(registryMedia.getId() != null) {
			type.setValue(registryMedia.getMedia().name());
			value.setValue(registryMedia.getValue());
			comment.setValue(registryMedia.getComment());
			admin.setValue(registryMedia.isAdministrative());
			commercial.setValue(registryMedia.isCommercial());
			tecnical.setValue(registryMedia.isTechnical());
		}
		
		// Buttons
		container.add(createButtonsPanel());
		add(container);	
	}
	
	private Widget createButtonsPanel() {
		HTMLPanel buttonsPanel = new HTMLPanel(EMPTY_STRING);
		buttonsPanel.setStyleName(AON.CSS.aonTextCenter());
		buttonsPanel.getElement().getStyle().setProperty("margin-top", "1rem");
    	
    	Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
			
			registryMedia
				.setMedia(MediaType.valueOf(type.getValue()))
				.setValue(value.getValue())
				.setComment(comment.getValue())
				.setAdministrative(admin.getValue())
				.setCommercial(commercial.getValue())
				.setTechnical(tecnical.getValue())
				;
			
			commonService.saveRegistryMedia(domainName, domainId, user, registryMedia, new AsyncCallback<RegistryMedia>() {
				
				@Override
				public void onSuccess(RegistryMedia result) {
					callback.onAccept(result);
				}
				
				@Override
				public void onFailure(Throwable error) {
					AonMessagePanel.showError(messagePanel, error.getMessage());
				}
				
			});
    	});
    	buttonsPanel.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addClickHandler(e -> {
    		cancelButton.setEnabled(false);
			callback.onCancel();
    	});
    	buttonsPanel.add(cancelButton);
    	
    	return buttonsPanel;
	}

}
