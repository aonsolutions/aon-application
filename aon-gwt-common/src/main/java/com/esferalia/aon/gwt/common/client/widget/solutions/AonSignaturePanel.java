package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
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
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	
	private AonCustomListBox users = new AonCustomListBox("Usuario");
	
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
		
		type.getElement().getStyle().setProperty("max-width", "8rem");
		
		type.clearItems();
		type.addItem("Empresa", "enterprise");
		type.addItem("Usuario", "user");
		
		row.add(type);
		row.add(name);
		container.add(row);
		
		// Row
		HTMLPanel row_ = new HTMLPanel(EMPTY_STRING);
		row_.setStyleName(AON.CSS.aonItemFlex());
		
		users.clearItems();
		users.addItem("-", "");
		
		row_.add(users);
		container.add(row_);
		
		// Second Row
		HTMLPanel row2 = new HTMLPanel(EMPTY_STRING);
		row2.setStyleName(AON.CSS.aonItemFlex());
		
		signatureRT.setMinHeight("18rem");
		
		row2.add(signatureRT);
		container.add(row2);
		
		// Fill info
		if(signature.getId() != null) {
			name.setValue(signature.getName());
			type.setValue(null == signature.getUserId() ? "enterprise" : "user");
			signatureRT.setValue(signature.getSignature());
		}
		
		type.addChangeHandler(e -> {
			checkUsersVisibility(row_);
		});
		
		getDomainUsers(userList -> {
			userList.forEach(u -> users.addItem(u.getName() + " (" + u.getLogin() + ")", u.getId().toString()));
			
			if(signature.getId() != null && signature.getUserId() != null)
				users.setValue(signature.getUserId().toString());
		});
		
		checkUsersVisibility(row_);
		
		// Buttons
		container.add(createButtonsPanel());
		add(container);	
	}
	
	private void checkUsersVisibility(HTMLPanel row_) {
		if(AonStringUtils.equalsIgnoreCase("enterprise", type.getValue())){
			row_.getElement().getStyle().setDisplay(Display.NONE);
			users.setValue("");
		} else {
			row_.getElement().getStyle().clearDisplay();
		}
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
    		
    		if(AonStringUtils.isBlank(users.getValue())) {
    			signature.setUserId(null);
    		} else {
    			signature.setUserId(Integer.parseInt(users.getValue()));
    		}
			
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
	
	private void getDomainUsers(Consumer<ArrayList<User>> success) {
		commonService.getUsers(domainName, domainId, user, new AsyncCallback<ArrayList<User>>() {
			
			@Override
			public void onSuccess(ArrayList<User> result) {
				result.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, error.getMessage());
			}
			
		});
	}

}
