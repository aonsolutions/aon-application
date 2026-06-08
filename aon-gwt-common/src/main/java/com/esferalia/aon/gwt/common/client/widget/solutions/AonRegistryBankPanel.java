package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.BankSwift;
import com.esferalia.aon.occam.api.model.Iban;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonRegistryBankPanel extends HTMLPanel {
	
	// Callback
	
	public static interface AonRegistryBankPanelCallback {
		void onAccept(RegistryBank registryBank);
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
	
	private AonRegistryBankPanelCallback callback;
	private RegistryBank registryBank;
	private List<Account> accounts;
	private RegistrySource registrySource;
	
	// Wrokplace Info
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomTextBox alias = new AonCustomTextBox("Alias");
	private AonCustomToogleButton activo = new AonCustomToogleButton("Activo");
	private AonCustomTextBox iban = new AonCustomTextBox("IBAN");
	private AonCustomTextBox bic = new AonCustomTextBox("BIC");
	private AonCustomTextBox suffix = new AonCustomTextBox("Sufijo");
	private AonCustomListBox accountLB = new AonCustomListBox("Cuenta Contable");
	
	// Constructor
	
	public AonRegistryBankPanel(String domainName, Integer domain, String user, Integer registry, List<Account> accounts, RegistrySource registrySource, AonRegistryBankPanelCallback callback) {
		super(EMPTY_STRING);
		
		this.registryBank = new RegistryBank()
				.setDomain(domain)
				.setRegistry(registry);
		
		aonRegistryBankPanel(domainName, domain, user, accounts, registrySource, callback);
	}
	
	public AonRegistryBankPanel(String domainName, Integer domain, String user, RegistryBank registryBank, List<Account> accounts, RegistrySource registrySource, AonRegistryBankPanelCallback callback) {
		super(EMPTY_STRING);
		
		this.registryBank = registryBank;
		
		aonRegistryBankPanel(domainName, domain, user, accounts, registrySource, callback);
	}
	
	private void aonRegistryBankPanel(String domainName, Integer domain, String user, List<Account> accounts, RegistrySource registrySource, AonRegistryBankPanelCallback callback) {
		initializeCommonService();
		
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.callback = callback;
		
		this.accounts = accounts;
		this.registrySource = registrySource;
		
		if(null != this.registryBank.getId() && null != this.registryBank.getAccount() && null != this.registryBank.getAccount().getId()) {
			this.accounts.stream().map(acc -> {
				if(acc.getId().equals(this.registryBank.getAccount().getId()) && !acc.isActive())
					acc.setActive(true);
				return acc;
			});
		}
		
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
		
		// Row 1
		HTMLPanel row = new HTMLPanel(EMPTY_STRING);
		row.setStyleName(AON.CSS.aonItemFlex());
		
		iban.addValueChangeHandler(e -> {
			String accountStr = this.iban.getValue();
			accountStr = accountStr.replaceAll("\\W+", "");
			accountStr = accountStr.toUpperCase();
			iban.setValue(accountStr);

			if (accountStr.length() > 0 && !Iban.validateIBAN(accountStr))
				AonMessagePanel.showError(messagePanel, "IBAN no valido");
		
			String bankAlias = getBankAlias(accountStr);
			if(AonStringUtils.isNotBlank(bankAlias))
				alias.setValue(bankAlias);
			
			String bankSwift = getBankSwift(accountStr);
			if(AonStringUtils.isNotBlank(bankSwift))
				bic.setValue(bankSwift);
			
		});
		
		activo.getElement().getStyle().setProperty("max-width", "5rem");
		activo.setValue(true);
		
		row.add(iban);
		row.add(alias);
		row.add(activo);
		container.add(row);
		
		// Row 2
		HTMLPanel row2 = new HTMLPanel(EMPTY_STRING);
		row2.setStyleName(AON.CSS.aonItemFlex());
		
		bic.getTextBox().setMaxLength(11);
		
		row2.add(bic);
		container.add(row2);
		
		// Row 4
		HTMLPanel row4 = new HTMLPanel(EMPTY_STRING);
		row4.setStyleName(AON.CSS.aonItemFlex());
		
		accountLB.clearItems();
		accountLB.addItem("-", "");
		this.accounts.stream().filter(acc -> acc.isActive()).forEach(acc -> accountLB.addItem(acc.getFullName(), acc.getId().toString()));
		
		suffix.getElement().getStyle().setProperty("max-width", "6rem");
		suffix.getTextBox().setMaxLength(3);
		
		row4.add(bic);
		
		if(this.registrySource.equals(RegistrySource.COMPANY))
			row4.add(suffix);
		
		row4.add(accountLB);
		container.add(row4);
		
		// Fill info
		if(registryBank.getId() != null) {
			alias.setValue(registryBank.getAlias());
			activo.setValue(registryBank.isActive());
			iban.setValue(registryBank.getBankAccount().toString());
			bic.setValue(registryBank.getBic());
			
			if(this.registrySource.equals(RegistrySource.COMPANY))
				suffix.setValue(registryBank.getSuffix());
			
			accountLB.setValue(null == registryBank.getAccount() || null == registryBank.getAccount().getId() ? "" : registryBank.getAccount().getId().toString());
		}
		
		// Buttons
		container.add(createButtonsPanel());
		add(container);	
	}

	private String getBankSwift(String account) {
		if (AonStringUtils.isNotBlank(account)) {
			BankSwift bankSwiftEntry = BankSwift.safeValueOf("B" + AonStringUtils.substring(account, 4, 8));
			return null == bankSwiftEntry ? null : bankSwiftEntry.getSwift();
		}
		return null;
	}

	private String getBankAlias(String account) {
		if (AonStringUtils.isNotBlank(account)) {
			BankSwift bankSwiftEntry = BankSwift.safeValueOf("B" + AonStringUtils.substring(account, 4, 8));
			return null == bankSwiftEntry ? null : bankSwiftEntry.getBankName();
		}
		return null;
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
    		
    		String accountStr = this.iban.getValue();
			accountStr = accountStr.replaceAll("\\W+", "");
			accountStr = accountStr.toUpperCase();
			
    		registryBank
				.setAlias(alias.getValue())
				.setActive(activo.getValue())
				.setBankAccount(AonStringUtils.isBlank(accountStr) ? null : new BankAccount(accountStr))
				.setBic(bic.getValue())
				.setSuffix(suffix.getValue())
				;
    		
    		registryBank.setAccount( AonStringUtils.isBlank(accountLB.getValue()) 
    				? null 
    				: accounts.stream().filter(acc -> acc.getId().equals(Integer.parseInt(accountLB.getValue()))).findFirst().orElse(null)
    		);
    		
    		
			commonService.saveRregistryBank(domainName, domainId, user, registryBank, new AsyncCallback<RegistryBank>() {
				
				@Override
				public void onSuccess(RegistryBank result) {
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

}
