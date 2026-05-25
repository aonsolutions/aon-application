package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class RegistryGeneralDataPanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;

	private static final Logger LOGGER = Logger.getLogger(RegistryGeneralDataPanel.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}
	
	private HTMLPanel content;
	
	private AonCustomListBox scopeLB = new AonCustomListBox("\u00c1mbito");
	private AonCustomTextArea observation = new AonCustomTextArea("Observaciones");
	private AonCustomListBox accountLB = new AonCustomListBox("Cuenta Contable"); 
	private AonCustomListBox transactionLB = new AonCustomListBox("Tipo Transacci\u00f3n");
	private AonCustomToogleButton irpf = new AonCustomToogleButton("I.R.P.F.");
	private AonCustomToogleButton re = new AonCustomToogleButton("R.E.");
	private AonCustomToogleButton criterio = new AonCustomToogleButton("Criterio Caja");
	
	private String domainName;
	private Integer domain;
	private String user;
	
	private CustomerFull customerFull;
	private CreditorFull creditorFull;
	private SupplierFull supplierFull;
	
	private RegistrySource registrySource;
	
	private List<Account> accounts;
	private List<Scope> scopes;

	public RegistryGeneralDataPanel(String domainName, int domain, String user, LinkedList<Scope> scopes, CustomerFull customerFull, RegistrySource registrySource) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
		this.customerFull = customerFull;
		this.scopes = scopes;
		this.registrySource = registrySource;
		
		content = new HTMLPanel("");
		content.addStyleName(AON.CSS.aonFlexColumn2());
		
		getContext(end -> {
			createRPaymethodPanel();
			setWidget(content);
		});
	}
	
	public RegistryGeneralDataPanel(String domainName, int domain, String user, LinkedList<Scope> scopes, CreditorFull creditorFull, RegistrySource registrySource) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
		this.creditorFull = creditorFull;
		this.scopes = scopes;
		this.registrySource = registrySource;
		
		content = new HTMLPanel("");
		content.addStyleName(AON.CSS.aonFlexColumn2());
		
		getContext(end -> {
			createRPaymethodPanel();
			setWidget(content);
		});
	}
	
	public RegistryGeneralDataPanel(String domainName, int domain, String user, LinkedList<Scope> scopes, SupplierFull supplierFull, RegistrySource registrySource) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
		this.supplierFull = supplierFull;
		this.scopes = scopes;
		this.registrySource = registrySource;
		
		content = new HTMLPanel("");
		content.addStyleName(AON.CSS.aonFlexColumn2());
		
		getContext(end -> {
			createRPaymethodPanel();
			setWidget(content);
		});
	}

	private void createRPaymethodPanel() {
		content.clear();
		
		HTMLPanel gridPanel = new HTMLPanel(AonStringUtils.EMPTY);
		gridPanel.setStyleName(AON.CSS.aonItemFlex());
		gridPanel.getElement().getStyle().setProperty("margin", "1rem 0");
		gridPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		gridPanel.getElement().getStyle().setProperty("gap", "2rem");

		HTMLPanel leftInfoTable = createTable();
		leftInfoTable.getElement().getStyle().setProperty("flex", "1");
		
		HTMLPanel rightInfoTable = createTable();
		rightInfoTable.getElement().getStyle().setProperty("flex", "1");
		
		scopeLB.clearItems();
		scopes.forEach(s -> scopeLB.addItem(s.getDescription(), s.getId().toString()));
		
		accountLB.clearItems();
		accountLB.addItem("-", "");
		accounts.forEach(a -> accountLB.addItem(a.getFullName(), a.getId().toString()));
		
		leftInfoTable.add(createRow(scopeLB, accountLB));
		transactionLB.clearItems();
		for(int i=0; i < InvoiceTransactionType.values().length; i++)
			transactionLB.addItem(InvoiceTransactionType.values()[i].getDescription(), InvoiceTransactionType.values()[i].name());
		
		irpf.getElement().getStyle().setProperty("max-width", "5rem");
		re.getElement().getStyle().setProperty("max-width", "5rem");
		criterio.getElement().getStyle().setProperty("max-width", "5rem");
		
		if(this.registrySource.equals(RegistrySource.CUSTOMER))
			leftInfoTable.add(createRow(transactionLB, irpf, re));
			
		else if(this.registrySource.equals(RegistrySource.SUPPLIER) || this.registrySource.equals(RegistrySource.CREDITOR))
			leftInfoTable.add(createRow(transactionLB, irpf, criterio));
		
		rightInfoTable.add(createRow(observation));
		
		gridPanel.add(leftInfoTable);
		gridPanel.add(rightInfoTable);
		
		content.add(gridPanel);
		
		if(null != customerFull && null != customerFull.getId()) {
			scopeLB.setValue(customerFull.getRegistry().getScope().getId().toString());
			accountLB.setValue(null == customerFull.getAccount() ? "" : customerFull.getAccount().getId().toString());
		
			transactionLB.setValue(customerFull.getRegistry().getTransaction().name());
			irpf.setValue(customerFull.getRegistry().isWithholding());
			
			if(this.registrySource.equals(RegistrySource.CUSTOMER))
				re.setValue(customerFull.getRegistry().isSurcharge());
			
			observation.setValue(customerFull.getRegistry().getObservation());
		} else if(null != creditorFull && null != creditorFull.getId()) {
			scopeLB.setValue(creditorFull.getRegistry().getScope().getId().toString());
			accountLB.setValue(null == creditorFull.getAccount() ? "" : creditorFull.getAccount().getId().toString());
		
			transactionLB.setValue(creditorFull.getRegistry().getTransaction().name());
			irpf.setValue(creditorFull.getRegistry().isWithholding());
			
			if(this.registrySource.equals(RegistrySource.CREDITOR))
				criterio.setValue(creditorFull.getRegistry().isVatAccrualPayment());
			
			observation.setValue(creditorFull.getRegistry().getObservation());
		} else if(null != supplierFull && null != supplierFull.getId()) {
			scopeLB.setValue(supplierFull.getRegistry().getScope().getId().toString());
			accountLB.setValue(null == supplierFull.getAccount() ? "" : supplierFull.getAccount().getId().toString());
		
			transactionLB.setValue(supplierFull.getRegistry().getTransaction().name());
			irpf.setValue(supplierFull.getRegistry().isWithholding());
			
			if(this.registrySource.equals(RegistrySource.SUPPLIER))
				re.setValue(supplierFull.getRegistry().isVatAccrualPayment());
			
			observation.setValue(supplierFull.getRegistry().getObservation());
		}
		
	}

	private HTMLPanel createTable() {
		HTMLPanel table = new HTMLPanel(AonStringUtils.EMPTY);
		table.addStyleName(AON.CSS.aonFlexColumn2());
		return table;
	}

	private HTMLPanel createRow(Widget... ws) {
		HTMLPanel row = new HTMLPanel(AonStringUtils.EMPTY);
		row.addStyleName(AON.CSS.aonItemFlex());

		for (int i = 0; i < ws.length; i++)
			row.add(ws[i]);

		return row;
	}
	
	private void getContext(Consumer<Void> end) {
		COMMON_SERVICE.getAccountsForRegistry(domainName, domain, user, registrySource, new AsyncCallback<List<Account>>() {

			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error cuentas contables: " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<Account> accountsDB) {
				accounts = accountsDB;
				end.accept(null);
			}
		});
	}

	public void onSave(CustomerFull customerSave) {
		customerSave.getRegistry().setScope(new Scope().setId(Integer.parseInt(scopeLB.getValue())));
		customerSave.setAccount(AonStringUtils.isBlank(accountLB.getValue()) ? null : new Account().setId(Integer.parseInt(accountLB.getValue())));
		customerSave.getRegistry().setTransaction(InvoiceTransactionType.valueOf(transactionLB.getValue()));
		customerSave.getRegistry().setWithholding(irpf.getValue());
		if(this.registrySource.equals(RegistrySource.CUSTOMER))
			customerSave.getRegistry().setSurcharge(re.getValue());
	}
	
	public void onSave(CreditorFull creditorSave) {
		creditorSave.getRegistry().setScope(new Scope().setId(Integer.parseInt(scopeLB.getValue())));
		creditorSave.setAccount(AonStringUtils.isBlank(accountLB.getValue()) ? null : new Account().setId(Integer.parseInt(accountLB.getValue())));
		creditorSave.getRegistry().setTransaction(InvoiceTransactionType.valueOf(transactionLB.getValue()));
		creditorSave.getRegistry().setWithholding(irpf.getValue());
		if(this.registrySource.equals(RegistrySource.CREDITOR))
			creditorSave.getRegistry().setVatAccrualPayment(criterio.getValue());
	}
	
	public void onSave(SupplierFull supplierFull) {
		supplierFull.getRegistry().setScope(new Scope().setId(Integer.parseInt(scopeLB.getValue())));
		supplierFull.setAccount(AonStringUtils.isBlank(accountLB.getValue()) ? null : new Account().setId(Integer.parseInt(accountLB.getValue())));
		supplierFull.getRegistry().setTransaction(InvoiceTransactionType.valueOf(transactionLB.getValue()));
		supplierFull.getRegistry().setWithholding(irpf.getValue());
		if(this.registrySource.equals(RegistrySource.SUPPLIER))
			supplierFull.getRegistry().setVatAccrualPayment(criterio.getValue());
	}

	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onShowSuccess(String successMessage);

}
