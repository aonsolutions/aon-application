package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Signature;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonSignaturePanel extends HTMLPanel {
	
	// Callback
	
	public static interface AonSignaturePanelCallback {
		void onAccept(Signature signature);
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
	
	private AonSignaturePanelCallback callback;
	private Signature signature;
	
	// Media Info
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	
	protected AonCustomTextBox name = new AonCustomTextBox("Nombre");
	private AonCustomRichText signatureRT = new AonCustomRichText("Firma");
	
	// Constructor
	
	public AonSignaturePanel(String domainName, Integer domain, String user, AonSignaturePanelCallback callback) {
		super(EMPTY_STRING);
		this.signature = new Signature().setDomain(domain);
		aonSignaturePanel(domainName, domain, user, callback);
	}
	
	public AonSignaturePanel(String domainName, Integer domain, String user, Signature signature, AonSignaturePanelCallback callback) {
		super(EMPTY_STRING);
		this.signature = signature;
		aonSignaturePanel(domainName, domain, user, callback);
	}
	
	private void aonSignaturePanel(String domainName, Integer domain, String user, AonSignaturePanelCallback callback) {
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
		container.getElement().getStyle().setProperty("min-width", "55rem");
		
		// First Row
		HTMLPanel row = new HTMLPanel(EMPTY_STRING);
		row.setStyleName(AON.CSS.aonItemFlex());
		row.add(name);
		container.add(row);
		
		// Second Row
		HTMLPanel row2 = new HTMLPanel(EMPTY_STRING);
		row2.setStyleName(AON.CSS.aonItemFlex());
		
		signatureRT.setMinHeight("18rem");
		
		row2.add(signatureRT);
		container.add(row2);
		
		// Fill info
		if(signature.getId() != null) {
			name.setValue(signature.getName());
			signatureRT.setValue(signature.getSignature());
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
			
    		signature
				.setName(name.getValue())
				.setSignature(signatureRT.getValue())
				;
			
			commonService.saveSignature(domainName, domainId, user, signature, new AsyncCallback<Signature>() {
				
				@Override
				public void onSuccess(Signature result) {
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
