package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.Country2ListBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DocumentTypeListBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.gwt.common.client.widget.VatDeductionTypeListBox;
import com.esferalia.aon.gwt.common.client.widget.WithholdingTypeListBox;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


public class InvoicePanel extends ResizeComposite implements RequiresResize {
	
	interface InvoicePanelDataBinder extends UiBinder<Widget, InvoicePanel> {}
	
	private static final InvoicePanelDataBinder DATA_BINDER = GWT
			.create(InvoicePanelDataBinder.class);

	@UiField(provided=true)
	AccountingRegistryBox registryBox;
	
	@UiField
	FlowPanel invoiceDataPanel;
	@UiField
	ListBox series;
	@UiField
	TextBox number;
	@UiField
	TextBox referenceCode;
	@UiField
	DoubleBox invoiceTotal;
	
	@UiField
	FlowPanel expAccountPanel;
	@UiField(provided=true)
	AccountBox expAccount;

	@UiField
	Label withholdingLabel;
	@UiField
	ListBox withholdingTaxs;
	@UiField
	DoubleBox withholdingPercent;
	@UiField
	DoubleBox withholdingQuota;
	@UiField
	WithholdingTypeListBox withholdingType;
	@UiField(provided=true)
	AccountBox withholdingAccount;
	
	@UiField
	HTMLPanel vatPanel;
	@UiField
	ListBox vatTaxs;
	@UiField
	DoubleBox taxableBase;
	@UiField
	DoubleBox vatPercent;
	@UiField
	DoubleBox vatQuota;
	@UiField
	VatDeductionTypeListBox vatDeductionType;
	@UiField(provided=true)
	AccountBox vatAccount;
	
	@UiField
	Label surchargeLabel;
	@UiField
	DoubleBox surchargePercent;
	@UiField
	DoubleBox surchargeQuota;
	
	@UiField
	Label invoiceTypeLabel;
	@UiField
	DocumentTypeListBox rDocumenType;
	@UiField
	Country2ListBox rDocumenCountry;
	@UiField
	DocumentTextBox rDocument;
	@UiField
	TextBox rName;
	
	@UiField
	DateBoxEx taxDate;
	@UiField
	InvoiceTransactionListBox transactionBox;
	@UiField
	CheckBox service;
	@UiField
	CheckBox investment;
	@UiField
	CheckBox surcharge;
	@UiField
	CheckBox withholding;
	@UiField
	CheckBox withholdingFarmer;
	@UiField
	CheckBox vatAccrualPayment;
	
	private AccountingInvoice invoice;
	private InvoicePanelVisitor invoicePanelVisitor;
	private IAccountEntryModuleCallback callback;
	
	@UiField
	ScrollPanel eastPanel;
	@UiField
	FlowPanel eastPanelInner;
	
	public InvoicePanel(IAccountEntryModuleCallback callback) {
		this.callback = callback;
		registryBox = new AccountingRegistryBox(AccountEntryModule.getCurrentDomainName()
				, AccountEntryModule.getCurrentDomain(), true);
		expAccount = new AccountBox(AccountEntryModule.getCurrentDomainName()
				, AccountEntryModule.getCurrentDomain());
		vatAccount = new AccountBox(AccountEntryModule.getCurrentDomainName()
				, AccountEntryModule.getCurrentDomain(), false);
		withholdingAccount = new AccountBox(AccountEntryModule.getCurrentDomainName()
				, AccountEntryModule.getCurrentDomain(), false);
		
		Widget ui = DATA_BINDER.createAndBindUi(this);
		initWidget(ui);
		
		invoice = new AccountingInvoice();
		invoiceDataPanel.setVisible(false);
		expAccountPanel.setVisible(false);
		enableWithholdingIfNeeded();
		vatPanel.setVisible(false);
		eastPanelInner.setVisible(false);

		fillSalesSeries();
		fillWithholdingTaxs();
		fillVatTaxs();
		
		invoicePanelVisitor = new InvoicePanelVisitor();
	}
	
	private void enableWithholdingIfNeeded() {
		withholdingLabel.setVisible(withholding.getValue());
		withholdingTaxs.setVisible(withholding.getValue());
		withholdingPercent.setVisible(withholding.getValue());
		withholdingQuota.setVisible(withholding.getValue());
		withholdingAccount.setVisible(withholding.getValue());
		withholdingType.setVisible(withholding.getValue());
	}
	
	private void enableSurchargeIfNeeded() {
		surchargeLabel.setVisible(surcharge.getValue());
		surchargePercent.setVisible(surcharge.getValue());
		surchargeQuota.setVisible(surcharge.getValue());
	}

	private void fillSalesSeries() {
		if (callback.getConfiguration().getInvoiceSalesSeries() != null && callback.getConfiguration().getInvoiceSalesSeries().size() > 0) {
			series.addItem(" --- ", (String) null);
			int i = 1;
			for (String ser : callback.getConfiguration().getInvoiceSalesSeries()) {
				series.addItem(ser);
				if (AonStringUtils.equals(callback.getConfiguration().getDefaultInvoiceSeries(), ser)) {
					series.setSelectedIndex(i);
					DomEvent.fireNativeEvent(Document.get().createChangeEvent(), series);
				}
			}
		}
	}

	private void fillWithholdingTaxs() {
		withholdingTaxs.setWidth("100px");
		if (callback.getConfiguration().getWithholdingTaxes() != null && callback.getConfiguration().getWithholdingTaxes().size() > 0) {
			int i = 0;
			for (Tax tax : callback.getConfiguration().getWithholdingTaxes()) {
				withholdingTaxs.addItem(tax.getName(),AonNumberUtils.toString( tax.getId()));
				if (tax.getPercentage() == callback.getConfiguration().getDefaultWithholdingPercent() ) {
					withholdingTaxs.setSelectedIndex(i);
					DomEvent.fireNativeEvent(Document.get().createChangeEvent(), withholdingTaxs);
				}
			}
		}
	}
	private void fillVatTaxs() {
		vatTaxs.setWidth("100px");
		if (callback.getConfiguration().getVatTaxes() != null && callback.getConfiguration().getVatTaxes().size() > 0) {
			int i = 0;
			for (Tax tax : callback.getConfiguration().getVatTaxes()) {
				vatTaxs.addItem(tax.getName(),AonNumberUtils.toString( tax.getId()));
				if (tax.getId() == callback.getConfiguration().getDefaultVatPercent() ) {
					vatTaxs.setSelectedIndex(i);
					DomEvent.fireNativeEvent(Document.get().createChangeEvent(), vatTaxs);
				}
			}
		}
	}

	@UiHandler("registryBox")
	public void onSelectRegistry(SelectionEvent<AccountingRegistry> event) {
		AccountingRegistry ar = event.getSelectedItem();
		invoice = new AccountingInvoice();
		invoice.setInvoiceType(ar.getType().getInvoiceType());
		invoiceTypeLabel.setText( AON.MSG.accountEntryType( ar.getType().getAccountEntryType() ));		
		invoice.setRegistry(ar);
		fillWidgets();
		eastPanelInner.setVisible(true);
		Account account = new Account();
		account.setId(ar.getAccountId());
		account.setCode(ar.getAccountCode());
		account.setDescription(ar.getAccountDescription());
		callback.onShowBalance(account);
		ar.getType().visit(invoicePanelVisitor);
	}
	
	private void fillWidgets() {
		rDocumenType.setValue(invoice.getRegistryDocumentType());
		rDocumenCountry.setValue(invoice.getRegistryDocumentCountry());
		rDocument.setValue(invoice.getRegistryDocument());
		rName.setValue(invoice.getRegistryName());
		surcharge.setValue(invoice.isSurcharge());
		withholding.setValue(invoice.isWithholding(),true);
		withholdingFarmer.setValue(invoice.isWithholdingFarmer());
		vatAccrualPayment.setValue(invoice.isVatAccrualPayment());
		transactionBox.setValue(invoice.getTransaction());
		taxDate.setValue(callback.getAccountEntry().getAccountEntry().getEntryDate());
	}
	
	@UiHandler("withholdingTaxs")
	public void onChangeWithholdingTaxs(ChangeEvent event) {
		for (Tax tax : callback.getConfiguration().getWithholdingTaxes()) {
			if ( AonNumberUtils.toInteger( withholdingTaxs.getSelectedValue()).equals(tax.getId())  ) {
				withholdingPercent.setValue(tax.getPercentage(), true);
				Account taxAccount = 
					invoice.getInvoiceType() == InvoiceType.SALES
						?tax.getSalesAccount()
						:tax.getPurchaseAccount();
				if (taxAccount == null) {
					taxAccount = invoice.getInvoiceType() == InvoiceType.SALES
						?callback.getConfiguration().getDefaultChargedRetAccount()
						:callback.getConfiguration().getDefaultPaidRetAccount();
				}
				withholdingType.setValue(tax.getWithholdingType());
				withholdingAccount.setAccount(taxAccount);
			}
		}
	}
	@UiHandler("vatTaxs")
	public void onChangeVatTaxs(ChangeEvent event) {
		for (Tax tax : callback.getConfiguration().getVatTaxes()) {
			if ( AonNumberUtils.toInteger( vatTaxs.getSelectedValue()).equals(tax.getId())  ) {
				vatPercent.setValue(tax.getPercentage(), true);
				surchargePercent.setValue(tax.getSurcharge(), true);
				Account taxAccount = 
						invoice.getInvoiceType() == InvoiceType.SALES
							?tax.getSalesAccount()
							:tax.getPurchaseAccount();
					if (taxAccount == null) {
						taxAccount = invoice.getInvoiceType() == InvoiceType.SALES
							?callback.getConfiguration().getDefaultChargedVatAccount()
							:callback.getConfiguration().getDefaultPaidVatAccount();
					}
				vatDeductionType.setValue(tax.getVatDeductionType());
				vatAccount.setAccount(taxAccount);
			}
		}
	}

	@UiHandler("withholding")
	public void onValueChangeWithholding(ValueChangeEvent<Boolean> event) {
		enableWithholdingIfNeeded();
		withholdingType.setValue(withholding.getValue()?WithholdingType.PROFESSIONAL:null);
	}
	@UiHandler("surcharge")
	public void onValueChangeSurcharge(ValueChangeEvent<Boolean> event) {
		enableSurchargeIfNeeded();
	}

	@UiHandler("expAccount")
	public void onSelectExpAccount(SelectionEvent<Account> event) {
		callback.onShowBalance(event.getSelectedItem());
	}
	@UiHandler("invoiceTotal")
	public void onValueChangeInvoiceTotal(ValueChangeEvent<Double> event) {
		if (invoiceTotal.getValue() == null) invoiceTotal.setValue(0.0, false); 
		if (vatPercent.getValue() == null) vatPercent.setValue(0.0, false);  
		double total = invoiceTotal.getValue();
		double coef = (1 + (vatPercent.getValue() /100));
		if (surcharge.getValue()) {
			if (surchargePercent.getValue() == null) surchargePercent.setValue(0.0, false);
			coef = coef + (surchargePercent.getValue()/100);
		}
		if (withholding.getValue()) {
			if (withholdingPercent.getValue() == null) withholdingPercent.setValue(0.0, false);
			coef = coef - (withholdingPercent.getValue() / 100);	
		}
		double tb = total / coef; 
		taxableBase.setValue(tb,true);
	}
	@UiHandler("taxableBase")
	public void onValueChangeTaxableBase(ValueChangeEvent<Double> event) {
		calculate();
	}
	
	private void calculate() {
		if (taxableBase.getValue() == null) taxableBase.setValue(0.0, false);
		if (vatPercent.getValue() == null) vatPercent.setValue(0.0, false);
		vatQuota.setValue( AonMathUtils.round(taxableBase.getValue() * vatPercent.getValue() / 100 ) );
		if (surcharge.getValue()) {
			if (surchargePercent.getValue() == null) surchargePercent.setValue(0.0, false);
			surchargeQuota.setValue( AonMathUtils.round(taxableBase.getValue() * surchargePercent.getValue() / 100 ) );
		} else {
			surchargeQuota.setValue( 0.0 );
		}
		
		if (withholding.getValue()) {
			if (withholdingPercent.getValue() == null) withholdingPercent.setValue(0.0, false);
			withholdingQuota.setValue( AonMathUtils.round(taxableBase.getValue() * withholdingPercent.getValue() / 100 ) );	
		} else {
			withholdingQuota.setValue( 0.0 );
		}
		invoiceTotal.setValue( AonMathUtils.round(taxableBase.getValue()
				+ vatQuota.getValue() 
				+ surchargeQuota.getValue() 
				- withholdingQuota.getValue() ));
		paintEntry();
	}
	
	private class InvoicePanelVisitor implements IAccountingRegistryTypeVisitor {

		@Override
		public void visitCustomer() {
			invoiceDataPanel.setVisible(true);
			expAccountPanel.setVisible(true);
			vatPanel.setVisible(true);
			investment.setVisible(false);
			service.setVisible(true);
			series.setVisible(true);
			number.setVisible(true);
			referenceCode.setVisible(false);
			series.setFocus(true);
			onValueChangeSurcharge(null);
			onValueChangeWithholding(null);
			onChangeVatTaxs(null);
			onChangeWithholdingTaxs(null);
			Account a = callback.getConfiguration().getDefaultSalesAccount();
			if (a != null) {
				expAccount.setValue(a.getId(), a.getCode(), a.getDescription());
			}
		}

		@Override
		public void visitCreditor() {
			invoiceDataPanel.setVisible(true);
			expAccountPanel.setVisible(true);
			vatPanel.setVisible(true);
			investment.setVisible(true);
			series.setVisible(false);
			number.setVisible(false);
			referenceCode.setVisible(true);
			referenceCode.setFocus(true);
			withholdingType.setVisible(withholding.getValue());
			onValueChangeSurcharge(null);
			onValueChangeWithholding(null);
			onChangeVatTaxs(null);
			onChangeWithholdingTaxs(null);
		}

		@Override
		public void visitSupplier() {
			invoiceDataPanel.setVisible(true);
			expAccountPanel.setVisible(true);
			vatPanel.setVisible(true);
			investment.setVisible(true);
			series.setVisible(false);
			number.setVisible(false);
			referenceCode.setVisible(true);
			referenceCode.setFocus(true);
			onValueChangeSurcharge(null);
			onValueChangeWithholding(null);
			onChangeVatTaxs(null);
			onChangeWithholdingTaxs(null);
			Account a = callback.getConfiguration().getDefaultPurchaseAccount();
			if (a != null) {
				expAccount.setValue(a.getId(), a.getCode(), a.getDescription());	
			}
		}
		
	}
	
	private void paintEntry() {
		AccountEntry ae = callback.getAccountEntry().getAccountEntry();
		LinkedList<AccountEntryDetail> details = new LinkedList<AccountEntryDetail>();
		double total = AonMathUtils.round(invoiceTotal.getValue());
		details.add( new AccountEntryDetail()
				.setAccount(invoice.getRegistryAccountId())
				.setAccountCode(invoice.getRegistryAccountCode())
				.setAccountDescription(invoice.getRegistryAccountDescription())
				.setDebit(invoice.getInvoiceType() == InvoiceType.SALES?total:0.0)
				.setCredit(invoice.getInvoiceType() != InvoiceType.SALES?total:0.0)
				.setConcept(invoice.getInvoiceType() == InvoiceType.SALES?"<N/Fra: #>":"<S/Fra: #>")
				.setDocumentNumber("<N. Factura>")
				);
		double vat = AonMathUtils.round(vatQuota.getValue() + surchargeQuota.getValue());
		if (AonMathUtils.isNotZero(vat)) {
			details.add( new AccountEntryDetail()
					.setAccount(vatAccount.getId())
					.setAccountCode(vatAccount.getValue())
					.setAccountDescription(vatAccount.getDescription())
					.setCredit(invoice.getInvoiceType() == InvoiceType.SALES?vat:0.0)
					.setDebit(invoice.getInvoiceType() != InvoiceType.SALES?vat:0.0)
					.setConcept(invoice.getInvoiceType() == InvoiceType.SALES?"<N/Fra: #>":"<S/Fra: #>")
					.setDocumentNumber("<N. Factura>")
					);
		}
		double wh = AonMathUtils.round(withholdingQuota.getValue());
		if (withholding.getValue() && AonMathUtils.isNotZero(wh)) {
			details.add( new AccountEntryDetail()
					.setAccount(withholdingAccount.getId())
					.setAccountCode(withholdingAccount.getValue())
					.setAccountDescription(withholdingAccount.getDescription())
					.setDebit(invoice.getInvoiceType() == InvoiceType.SALES?wh:0.0)
					.setCredit(invoice.getInvoiceType() != InvoiceType.SALES?wh:0.0)
					.setConcept(invoice.getInvoiceType() == InvoiceType.SALES?"<N/Fra: #>":"<S/Fra: #>")
					.setDocumentNumber("<N. Factura>")
					);
		}
		double tb = AonMathUtils.round(taxableBase.getValue());
		if (AonMathUtils.isNotZero(vat)) {
			details.add( new AccountEntryDetail()
					.setAccount(expAccount.getId())
					.setAccountCode(expAccount.getValue())
					.setAccountDescription(expAccount.getDescription())
					.setCredit(invoice.getInvoiceType() == InvoiceType.SALES?tb:0.0)
					.setDebit(invoice.getInvoiceType() != InvoiceType.SALES?tb:0.0)
					.setConcept(invoice.getInvoiceType() == InvoiceType.SALES?"<N/Fra: #>":"<S/Fra: #>")
					.setDocumentNumber("<N. Factura>")
					);
		}
		ae.setDetails(details);
		callback.showWorkingLog(ae);
	}
}
