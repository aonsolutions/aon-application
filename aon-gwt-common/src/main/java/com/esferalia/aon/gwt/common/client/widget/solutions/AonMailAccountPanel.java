package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.MailAccountType;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
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
	
	private AonCustomTextBox name = new AonCustomTextBox(AON.MSG.description() + " / " + AON.MSG.aliasAbbr());
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	private AonCustomListBox protocol = new AonCustomListBox("Protocolo");
	
	private AonCustomListBox users = new AonCustomListBox("Usuario");
	
	private AonCustomTextBox email = new AonCustomTextBox("Email Env\u00edo");
	private AonCustomListBox host = new AonCustomListBox(" ");
	
	private AonCustomTextBox showAs = new AonCustomTextBox("Mostrar Como");
	private AonCustomListBox signature = new AonCustomListBox("Firma");
	
	private AonCustomTextBox replayTo = new AonCustomTextBox("Email(s) Respuesta");
	private AonCustomToogleButton bccInclude = new AonCustomToogleButton("Incluir BCC");
	
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
		getElement().getStyle().setProperty("min-width", "43rem");
		add(messagePanel);
		
		HTMLPanel container = new HTMLPanel(EMPTY_STRING);
		container.setStyleName(AON.CSS.aonFlexColumn2());
		container.getElement().getStyle().setProperty("padding", "0 1rem");
		container.getElement().getStyle().setProperty("min-width", "25rem");
		
		// First Row
		HTMLPanel row = new HTMLPanel(EMPTY_STRING);
		row.setStyleName(AON.CSS.aonItemFlex());
		
		type.getElement().getStyle().setProperty("max-width", "8rem");
		protocol.getElement().getStyle().setProperty("max-width", "8rem");
		
		type.clearItems();
		type.addItem("Empresa", MailAccountType.SYSTEM.name());
		type.addItem("Usuario", MailAccountType.USER.name());
		
		protocol.clearItems();
		protocol.addItem("Aplicaci\u00f3n", "aon");
		protocol.addItem("Personalizado", "ses");
		protocol.addItem("IMAP", "imap");
		protocol.addItem("SMTP", "smtp");
		
		protocol.getListBox().getElement().getElementsByTagName("option").getItem(2).setAttribute("disabled", "disabled");
		protocol.getListBox().getElement().getElementsByTagName("option").getItem(3).setAttribute("disabled", "disabled");
		
		row.add(name);
		row.add(type);
		row.add(protocol);
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
		
		email.setValue("app");
		host.clearItems();
		
		host.getElement().getStyle().setProperty("max-width", "16.5rem");
		
		row2.add(email);
		row2.add(host);
		container.add(row2);
		
		// Third Row
		HTMLPanel row3 = new HTMLPanel(EMPTY_STRING);
		row3.setStyleName(AON.CSS.aonItemFlex());
		
		signature.getElement().getStyle().setProperty("max-width", "16.5rem");
		
		signature.clearItems();
		signature.addItem("-", "");
		this.signatures.forEach(s -> signature.addItem(s.getName(), s.getId().toString()));

		row3.add(showAs);
		row3.add(signature);
		container.add(row3);
		
		// Fourth Row
		HTMLPanel row4 = new HTMLPanel(EMPTY_STRING);
		row4.setStyleName(AON.CSS.aonItemFlex());

		bccInclude.getElement().getStyle().setProperty("max-width", "5rem");
		bccInclude.setEnable(false);

		replayTo.getTextBox().getElement().setPropertyString("placeholder", "Lista de correos v\u00e1lidos separados por coma");
		replayTo.addValueChangeHandler(e -> {
			checkReplayToEmails();
		});
		
		row4.add(replayTo);
		row4.add(bccInclude);
		container.add(row4);
		
		type.addChangeHandler(e -> {
			checkUsersVisibility(row_);
		});
		
		protocol.addChangeHandler(e -> {
			if(mailAccount.getId() != null && !AonStringUtils.equalsIgnoreCase(mailAccount.getProtocol(), "aon") && AonStringUtils.equalsIgnoreCase(protocol.getValue(), "aon")) {
				replayTo.setValue(AonStringUtils.isBlank(replayTo.getValue()) ? mailAccount.getEmail() : replayTo.getValue() + ", " + mailAccount.getEmail());
				bccInclude.setValue(true);
			}
			
			checkEmailVisibility(row2);
		});
		
		
		// Fill info
		if(mailAccount.getId() != null) {
			name.setValue(mailAccount.getName());
			type.setValue(mailAccount.getType().name());
			protocol.setValue(mailAccount.getProtocol());
			
			email.setValue(getEmailWithoutHost(mailAccount.getEmail()));
			
			showAs.setValue(mailAccount.getDisplayName());
			signature.setValue(mailAccount.getSignatureId() == null ? "" : mailAccount.getSignatureId().toString());
			
			bccInclude.setValue(mailAccount.isIncludeBcc());
			replayTo.setValue(mailAccount.getReplytoMail());
			
			checkReplayToEmails();
		}
		
		checkUsersVisibility(row_);
		checkEmailVisibility(row2);
		
		// Buttons
		container.add(createButtonsPanel());
		add(container);	
		
		getVerifiedHostEmails(hostEmails -> {
			hostEmails.entrySet().forEach(h -> {
				if(h.getValue()) host.addItem("@" + h.getKey());
			});
			
			if(hostEmails.isEmpty())
				protocol.getListBox().getElement().getElementsByTagName("option").getItem(1).setAttribute("disabled", "disabled");
			
			host.addItem("@aon.solutions");
			
			if(mailAccount.getId() != null)
				host.setValue(getEmailHost(mailAccount.getEmail()));
		});
		
		getDomainUsers(userList -> {
			userList.forEach(u -> users.addItem(u.getName() + " (" + u.getLogin() + ")", u.getId().toString()));
			
			if(mailAccount.getId() != null && mailAccount.getUserId() != null)
				users.setValue(mailAccount.getUserId().toString());
		});
	}
	
	private void checkEmailVisibility(HTMLPanel row) {
		if(AonStringUtils.equalsIgnoreCase(protocol.getValue(), "aon")) {
			row.getElement().getStyle().setDisplay(Display.NONE);
		} else {
			row.getElement().getStyle().clearDisplay();
		}
	}

	private void checkUsersVisibility(HTMLPanel row_) {
		if(AonStringUtils.equalsIgnoreCase(MailAccountType.SYSTEM.name(), type.getValue())){
			row_.getElement().getStyle().setDisplay(Display.NONE);
			users.setValue("");
		} else {
			row_.getElement().getStyle().clearDisplay();
		}
	}

	private void checkReplayToEmails() {
	    String emails = replayTo.getValue();

	    if (AonStringUtils.isBlank(emails)) {
	        bccInclude.setEnable(false);
	        return;
	    }

	    // Separar por coma
	    String[] parts = emails.split(",");

	    boolean allValid = true;

	    for (String part : parts) {
	        String email = part.trim();

	        if (email.isEmpty() || !isValidEmail(email)) {
	            allValid = false;
	            break;
	        }
	    }

	    bccInclude.setEnable(allValid);
	}

	private String getEmailHost(String email) {
		if(AonStringUtils.isBlank(email)) return "@aon.solutions";
		return AonStringUtils.containsIgnoreCase(email, "@") ? '@' + AonStringUtils.split(email, '@')[1] : null;
	}
	
	private String getEmailWithoutHost(String email) {
		if(AonStringUtils.isBlank(email)) return null;
		return AonStringUtils.containsIgnoreCase(email, "@") ? AonStringUtils.split(email, '@')[0] : null;
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
    		
    		if(!isValidEmail(email.getValue() + host.getValue())) {
    			okButton.setEnabled(true);
    			AonMessagePanel.showError(messagePanel, "Email incorrecto");
    			return;
    		}
    		
    		if(!isReplyToValid(replayTo.getValue())) {
    			okButton.setEnabled(true);
    			AonMessagePanel.showError(messagePanel, "Email(s) Respuesta obligatorio. Formato email o email1, email2, ...");
    			return;
    		}
    		
    		mailAccount
				.setName(name.getValue())
				.setEmail(email.getValue() + host.getValue())
				.setDisplayName(showAs.getValue())
				.setIncludeBcc(bccInclude.getValue())
				.setReplytoMail(replayTo.getValue())
				.setType(MailAccountType.safeValueOf(type.getValue()))
				.setSignatureId(AonStringUtils.isBlank(signature.getValue()) ? null : Integer.parseInt(signature.getValue()))
				.setProtocol(protocol.getValue())
				;
    		
    		if(AonStringUtils.isBlank(users.getValue())) {
    			mailAccount.setType(MailAccountType.SYSTEM);
    			mailAccount.setUserId(null);
    		} else {
    			mailAccount.setUserId(Integer.parseInt(users.getValue()));
    		}
			
			commonService.saveMailAccount(domainName, domainId, user, mailAccount, new AsyncCallback<MailAccount>() {
				
				@Override
				public void onSuccess(MailAccount result) {
					callback.onAccept(result);
				}
				
				@Override
				public void onFailure(Throwable error) {
					AonMessagePanel.showError(messagePanel, error.getMessage());
					okButton.setEnabled(true);
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
	
	public boolean isReplyToValid(String value) {
	    if (AonStringUtils.isBlank(value)) {
	        return false;
	    }

	    String trimmed = value.trim();
	   String[] parts = trimmed.split(",");
	    for (String part : parts) {
	        String email = part.trim();
	        if (email.isEmpty() || !isValidEmail(email)) {
	            return false; // hay una parte vacía (ej. coma doble) o un email inválido
	        }
	    }
	    return true;
	}
	
	public static boolean isValidEmail(String email) {
	    if (email == null) return false;

	    email = email.trim();

	    int at = email.indexOf('@');
	    int lastAt = email.lastIndexOf('@');

	    // Debe haber exactamente un '@'
	    if (at <= 0 || at != lastAt) return false;

	    String local = email.substring(0, at);
	    String domain = email.substring(at + 1);

	    // Local part no puede estar vacía
	    if (local.isEmpty()) return false;

	    // Dominio debe tener al menos un punto
	    int dot = domain.lastIndexOf('.');
	    if (dot <= 0 || dot == domain.length() - 1) return false;

	    // Caracteres prohibidos
	    if (email.contains(" ") || email.contains("..")) return false;

	    return true;
	}

	
	private void getVerifiedHostEmails(Consumer<HashMap<String, Boolean>> success) {
		commonService.getVerifiedHostEmails(domainName, domainId, user, new AsyncCallback<HashMap<String, Boolean>>() {
			
			@Override
			public void onSuccess(HashMap<String, Boolean> result) {
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, error.getMessage());
			}
			
		});
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
