package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.MailAccountType;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonMailAccountPanel extends HTMLPanel {
	
	// Callback
	
	public static interface AonMailAccountPanelCallback {
		void onAccept(MailAccount mailAccount);
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
	
	private AonMailAccountPanelCallback callback;
	private MailAccount mailAccount;
	
	// Media Info
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomTextBox name = new AonCustomTextBox("Nombre");
	private AonCustomTextBox email = new AonCustomTextBox("Email");
	private AonCustomTextBox showAs = new AonCustomTextBox("Mostrar Como");
	
	private AonCustomCheckBox bccInclude = new AonCustomCheckBox("Incluir BCC");
	private AonCustomTextBox replayTo = new AonCustomTextBox("Email Respuesta");

	private AonCustomTextBox verified = new AonCustomTextBox("Estado");
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	private AonCustomListBox signature = new AonCustomListBox("Firma");
	
	private LinkedList<Signature> signatures;
	
	// Constructor
	
	public AonMailAccountPanel(String domainName, Integer domain, String user, LinkedList<Signature> signatures, AonMailAccountPanelCallback callback) {
		super(EMPTY_STRING);
		this.mailAccount = new MailAccount().setDomain(domain);
		aonSignaturePanel(domainName, domain, user, signatures, callback);
	}
	
	public AonMailAccountPanel(String domainName, Integer domain, String user, LinkedList<Signature> signatures, MailAccount mailAccount, AonMailAccountPanelCallback callback) {
		super(EMPTY_STRING);
		this.mailAccount = mailAccount;
		aonSignaturePanel(domainName, domain, user, signatures, callback);
	}
	
	private void aonSignaturePanel(String domainName, Integer domain, String user, LinkedList<Signature> signatures, AonMailAccountPanelCallback callback) {
		initializeCommonService();
		
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.callback = callback;
		this.signatures = signatures;
		
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
		row.add(name);
		row.add(email);
		row.add(showAs);
		container.add(row);
		
		// Second Row
		HTMLPanel row2 = new HTMLPanel(EMPTY_STRING);
		row2.setStyleName(AON.CSS.aonItemFlex());
		
		bccInclude.getElement().getStyle().setProperty("max-width", "5rem");
		
		replayTo.setEnable(false);
		
		bccInclude.addValueChangeHandler(e -> {
			replayTo.setEnable(e.getValue());
		});
		
		row2.add(bccInclude);
		row2.add(replayTo);
		container.add(row2);
		
		// Third Row
		type.clearItems();
		type.addItem("Usuario", MailAccountType.USER.name());
		type.addItem("Sistema", MailAccountType.SYSTEM.name());
		
		signature.clearItems();
		signature.addItem("-", "");
		this.signatures.forEach(s -> signature.addItem(s.getName(), s.getId().toString()));
		
		HTMLPanel row3 = new HTMLPanel(EMPTY_STRING);
		row3.setStyleName(AON.CSS.aonItemFlex());
		
		verified.setEnable(false);
		
		row3.add(verified);
		row3.add(type);
		row3.add(signature);
		container.add(row3);
		
		// Fill info
		if(mailAccount.getId() != null) {
			name.setValue(mailAccount.getName());
			email.setValue(mailAccount.getEmail());
			showAs.setValue(mailAccount.getDisplayName());
			
			bccInclude.setValue(mailAccount.isIncludeBcc());
			replayTo.setEnable(mailAccount.isIncludeBcc());
			replayTo.setValue(mailAccount.getReplytoMail());
			
			verified.setValue(mailAccount.isSESVerified() ? "Verificado" : "No verificado");
			type.setValue(mailAccount.getType().name());
			signature.setValue(mailAccount.getSignatureId() == null ? "" : mailAccount.getSignatureId().toString());
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
			
    		mailAccount
				.setName(name.getValue())
				.setEmail(email.getValue())
				.setDisplayName(showAs.getValue())
				.setIncludeBcc(bccInclude.getValue())
				.setReplytoMail(replayTo.getValue())
				.setType(MailAccountType.safeValueOf(type.getValue()))
				.setSignatureId(AonStringUtils.isBlank(signature.getValue()) ? null : Integer.parseInt(signature.getValue()))
				.setProtocol("aon")
				;
			
			commonService.saveMailAccount(domainName, domainId, user, mailAccount, new AsyncCallback<MailAccount>() {
				
				@Override
				public void onSuccess(MailAccount result) {
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
