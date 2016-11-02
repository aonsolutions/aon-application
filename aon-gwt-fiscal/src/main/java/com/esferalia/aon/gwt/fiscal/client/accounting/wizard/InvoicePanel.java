package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.WithholdingTypeListBox;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.SessionLog;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.IAccountingInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.InvoiceRecorder;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


public class InvoicePanel extends WizardContentBase {
	
	static FiscalServiceAsync fiscalService;
	static FinanceServiceAsync financeService;
	
	public static interface IInvoicePanelCallback extends IAccountEntryModuleCallback{
		AccountingInvoice getInvoice();
		boolean isInvestAssetsAvailable();
		void transactionChanged();
		void withholdingChanged();
		void surchargeChanged();
		void invoiceTotalChanged();
		void enableInvoiceTotal(boolean enable);
		void paintEntry();
		void setFocusOnRegistry();
	}

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
	IntegerBox number;
	@UiField
	TextBox referenceCode;
	@UiField
	DoubleBox invoiceTotal;
	@UiField
	Button fastSave;
	
	@UiField(provided=true)
	InvoiceVATPanel vatPanel;
	@UiField
	Label withholdingLabel;
	
	@UiField
	HTMLPanel withholdingPanel;
	@UiField
	ListBox withholdingTaxs;
	@UiField(provided=true)
	DoubleBox withholdingBase;
	@UiField
	DoubleBox withholdingPercent;
	@UiField
	DoubleBox withholdingQuota;
	@UiField
	WithholdingTypeListBox withholdingType;
	@UiField(provided=true)
	AccountBox withholdingAccount;
	
	@UiField(provided=true)
	InvoiceExtraPanel extraPanel;

	@UiField
	SessionLog workingLog;
	
	private AccountingInvoice invoice;
	private InvoicePanelRegistryVisitor invoicePanelRegistryVisitor;
	
	public InvoicePanel(final IAccountEntryModuleCallback callback) {
		setCallback(callback);
		
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);

		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		financeService = new FinanceServiceAsyncDecorator(financeServiceRaw);

		invoicePanelRegistryVisitor = new InvoicePanelRegistryVisitor();
		InvoicePanelCallback invoiceCallback = new InvoicePanelCallback();
		
		registryBox = new AccountingRegistryBox(
			AccountEntryModule.getCurrentDomainName()
			,AccountEntryModule.getCurrentDomain()
			,callback.getConfiguration()
			,true);
		withholdingBase = new DoubleBox(12,4);
		withholdingAccount = new AccountBox(AccountEntryModule.getCurrentDomainName()
				, AccountEntryModule.getCurrentDomain(), false);
		vatPanel = new InvoiceVATPanel( invoiceCallback );
		extraPanel = new InvoiceExtraPanel(  );
		
		Widget ui = DATA_BINDER.createAndBindUi(InvoicePanel.this);
		initWidget(ui);
		number.addStyleName(AON.AON_CSS.aonMarginRight5());
		invoiceDataPanel.setVisible(false);
		withholdingPanel.setVisible(false);
	}
	
	@Override
	public void reset() {
		
	}
	
	public void setInvoice(AccountingInvoice invoice) {
		this.invoice = invoice;
	}
	
	@Override
	public void select(final AccountEntry entry) {
		this.ae = entry;
		if (this.ae.getId() != null) {
			fiscalService.getAccountingInvoice(
					AccountEntryModule.getCurrentDomainName()
					,AccountEntryModule.getCurrentDomain()
					,entry.getId()
					, new AsyncCallback<AccountingInvoice>() {
						
						@Override
						public void onSuccess(AccountingInvoice result) {
							populate(result);
							callback.onBalance(entry);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							callback.onError(caught.getMessage());
						}
					});
		} else {
			AccountingInvoice i = new AccountingInvoice();
			i.setAccountEntry(getAccountEntry());
			setInvoice(i);
			workingLog.clear();
			invoiceDataPanel.setVisible(false);
			withholdingPanel.setVisible(false);
			registryBox.setValue(new AccountingRegistry());
			vatPanel.setVisible(false);
			extraPanel.invoiceChanged(invoice);
			callback.onBalance(entry);
		}
	}

	private void populate(AccountingInvoice result) {
		setInvoice(result);
		paint();
		registryBox.setValue(invoice.getRegistry());
		extraPanel.invoiceChanged(invoice);
		invoice.getInvoice().getType().visit(invoice,new InvoicePanelVisitor());
	}

	private void paint() {
		if (invoice != null) {
			vatPanel.setVisible(true);
			extraPanel.setVisible(true);
			InvoicePanelCallback invoiceCallback = new InvoicePanelCallback();
			fillSalesSeries();
			fillWithholdingTaxs();
			extraPanel.paint(invoiceCallback);
			vatPanel.paint();
			// #TODO 
			enableWithholdingIfNeeded();
			// -----
			_paintEntry();
		}
	}

	private void enableWithholdingIfNeeded() {
		withholdingPanel.setVisible(invoice.isWithholding());
		withholdingLabel.setVisible(invoice.isWithholding());
		withholdingTaxs.setVisible(invoice.isWithholding());
		withholdingBase.setVisible(invoice.isWithholding());
		withholdingBase.setReadOnly(true);
		withholdingPercent.setVisible(invoice.isWithholding());
		withholdingQuota.setVisible(invoice.isWithholding());
		withholdingQuota.setReadOnly(true);
		withholdingAccount.setVisible(invoice.isWithholding());
		withholdingType.setVisible(invoice.isWithholding());
	}
	private void enableSurchargeIfNeeded() {
		vatPanel.surchargeChanged(extraPanel.isSurcharge());
	}
	
	private void fillSalesSeries() {
		if (callback.getConfiguration().getInvoiceSalesSeries() != null && callback.getConfiguration().getInvoiceSalesSeries().size() > 0) {
			series.addItem(" --- ", (String) null);
			for (String ser : callback.getConfiguration().getInvoiceSalesSeries()) {
				series.addItem(ser);
			}
			series.setSelectedIndex(0);
		}
	}

	private void fillWithholdingTaxs() {
		if (withholdingTaxs.getItemCount() == 0) {
			withholdingTaxs.setWidth("100px");
			withholdingTaxs.addItem("--------","-1");
			if (callback.getConfiguration().getWithholdingTaxes() != null && callback.getConfiguration().getWithholdingTaxes().size() > 0) {
				for (Tax tax : callback.getConfiguration().getWithholdingTaxes()) {
					withholdingTaxs.addItem(tax.getName(),AonNumberUtils.toString( tax.getId()));
				}
			}
		}
	}
	@UiHandler("series")
	public void onSelectSeries(ChangeEvent event) {
		invoice.getInvoice().setSeries(series.getSelectedValue());
		financeService.getInvoiceNextNumber(
				 AccountEntryModule.getCurrentDomainName()
				,AccountEntryModule.getCurrentDomain()
				,new Byte[]{invoice.getInvoice().getType().value()}
				 , series.getSelectedValue()
				, new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onError(caught.getMessage());
					}

					@Override
					public void onSuccess(Integer result) {
						number.setValue(result,false,true);
						invoice.getInvoice().setNumber(result);
						_paintEntry();
					}
				});
	}
	
	@UiHandler("number")
	public void onValueChangeEvent(ValueChangeEvent<Integer> event) {
		invoice.getInvoice().setNumber(number.getValue());
		_paintEntry();
	}

	@UiHandler("registryBox")
	public void onSelectRegistry(SelectionEvent<AccountingRegistry> event) {
		initializeInvoice(invoice.getAccountEntry(), event.getSelectedItem());
	}
	
	private void initializeInvoice(final AccountEntry entry,final AccountingRegistry ar) {
		fiscalService.initializeInvoice(
				 AccountEntryModule.getCurrentDomainName()
				,AccountEntryModule.getCurrentDomain()
				,entry,ar
				, new AsyncCallback<AccountingInvoice>() {
					
					@Override
					public void onSuccess(AccountingInvoice result) {
						invoice = result;
						invoiceTotal.setEnabled(true);
						Account account = new Account();
						account.setId(ar.getAccountId());
						account.setCode(ar.getAccountCode());
						account.setDescription(ar.getAccountDescription());
						callback.onBalance(account);
						
						vatPanel.setSuggestedAccounts(invoice.getSuggestedAccounts());
						paint();
						extraPanel.invoiceChanged(result);
						invoice.getRegistry().getType().visit(invoice.getRegistry(),invoicePanelRegistryVisitor);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						callback.onError(caught.getMessage());
					}
				});
	}

	private void populateWithholding() {
		invoice.refreshWithholdingData();
		withholdingBase.setValue( invoice.getWithholdingData().getBase());
		withholdingPercent.setValue( invoice.getWithholdingData().getPercentage() );
		withholdingQuota.setValue( invoice.getWithholdingData().getQuota() );
		withholdingType.setValue(invoice.getWithholdingData().getWithholdingType());
		withholdingAccount.setValue(invoice.getWithholdingData().getAccountId()
				,invoice.getWithholdingData().getAccountCode()
				,invoice.getWithholdingData().getAccountDescription());
		enableWithholdingIfNeeded();
	}

	@UiHandler("withholdingTaxs")
	public void onChangeWithholdingTaxs(ChangeEvent event) {
		for (Tax tax : callback.getConfiguration().getWithholdingTaxes()) {
			if ( AonNumberUtils.toInteger( withholdingTaxs.getSelectedValue()).equals(tax.getId())  ) {
				
				Account taxAccount = invoice.isSales()
						?tax.getSalesAccount()
						:tax.getPurchaseAccount();
				if (taxAccount == null) {
					taxAccount = invoice.isSales()
						?callback.getConfiguration().getDefaultChargedRetAccount()
						:callback.getConfiguration().getDefaultPaidRetAccount();
				}
				invoice.getWithholdingData().setPercentage(tax.getPercentage());
				invoice.getWithholdingData().setWithholdingType(tax.getWithholdingType());
				if (taxAccount != null) {
					invoice.getWithholdingData().setAccountId(taxAccount.getId());
					invoice.getWithholdingData().setAccountCode(taxAccount.getCode());
					invoice.getWithholdingData().setAccountDescription(taxAccount.getDescription());
					callback.onBalance(taxAccount);
				} else {
					invoice.getWithholdingData().setAccountId(null);
					invoice.getWithholdingData().setAccountCode(null);
					invoice.getWithholdingData().setAccountDescription(null);
				}
				invoice.calculateInvoiceTotals();
				populateWithholding();
				_paintEntry();
			}
		}
	}
	private class InvoicePanelVisitor implements IAccountingInvoiceTypeVisitor {

		@Override
		public void visitPurchase(AccountingInvoice invoice) {
			populatePurchaseInvoice(invoice);
		}

		@Override
		public void visitSales(AccountingInvoice invoice) {
			populateSalesInvoice(invoice);
		}

		@Override
		public void visitExpenses(AccountingInvoice invoice) {
			populateExpensesInvoice(invoice);
		}

		@Override
		public void visitUndeductible(AccountingInvoice invoice) {
			populateExpensesInvoice(invoice);
		}
		
	}

	private void populateSalesInvoice(AccountingInvoice invoice) {
		invoice.getAccountEntry().setEntryType(AccountEntryType.SALES_INVOICE);
		for (int i = 0; i < series.getItemCount(); i++) {
			if (AonStringUtils.equals(invoice.getInvoice().getSeries(), series.getValue(i))) {
				series.setSelectedIndex(i);
			}
		}
		number.setValue(invoice.getInvoice().getNumber());
		invoiceDataPanel.setVisible(true);
		series.setVisible(true);
		number.setVisible(true);
		referenceCode.setVisible(false);
		series.setFocus(true);
		invoiceTotal.setValue(invoice.getInvoice().getTotal());
		populateWithholding();
	}
	private void populatePurchaseInvoice(AccountingInvoice invoice) {
		invoice.getAccountEntry().setEntryType(AccountEntryType.PURCHASE_INVOICE);
		invoiceDataPanel.setVisible(true);
		series.setVisible(false);
		number.setVisible(false);
		referenceCode.setValue(invoice.getInvoice().getReferenceCode());
		referenceCode.setVisible(true);
		referenceCode.setFocus(true);
		invoiceTotal.setValue(invoice.getTotalInvoice());
		populateWithholding();
	}
	private void populateExpensesInvoice(AccountingInvoice invoice) {
		invoice.getAccountEntry().setEntryType(AccountEntryType.EXPENSE_INVOICE);
		invoiceDataPanel.setVisible(true);
		series.setVisible(false);
		number.setVisible(false);
		referenceCode.setValue(invoice.getInvoice().getReferenceCode());
		referenceCode.setVisible(true);
		referenceCode.setFocus(true);
		invoiceTotal.setValue(invoice.getTotalInvoice());
		populateWithholding();
	}

	private class InvoicePanelRegistryVisitor implements IAccountingRegistryTypeVisitor {

		@Override
		public void visitCustomer(AccountingRegistry reg) {
			populateSalesInvoice(invoice);
			fastSave.setVisible(false);
		}

		@Override
		public void visitCreditor(AccountingRegistry reg) {
			populateExpensesInvoice(invoice);
			fastSave.setVisible(false);
		}

		@Override
		public void visitSupplier(AccountingRegistry reg) {
			populatePurchaseInvoice(invoice);
			fastSave.setVisible(false);
		}
		
	}
	
	@UiHandler("referenceCode")
	public void onValueChangeReferenceCode(ValueChangeEvent<String> event) {
		invoice.getInvoice().setReferenceCode(referenceCode.getValue());
		_paintEntry();
	}
	
	@UiHandler("invoiceTotal")
	public void onValueChangeInvoiceTotal(ValueChangeEvent<Double> event) {
		if (invoiceTotal.getValue() == null) invoiceTotal.setValue(0.0, false);
		vatPanel.invoiceTotalChanged(invoiceTotal.getValue());
		_paintEntry();
		if (invoiceTotal.getValue() == null || invoiceTotal.getValue() != 0) {
			fastSave.setVisible(true);
			fastSave.setFocus(true);
		}
	}
	@UiHandler("fastSave")
	public void onFastSave(ClickEvent event) {
		callback.save(event);
	}
	
	@UiHandler("vatPanel")
	public void onValueChangeVatPanel(ValueChangeEvent<InvoiceVAT> event) {
		if (invoice.isWithholding()) populateWithholding();
		_paintEntry();
	}
	@UiHandler("vatPanel")
	public void onSelectionAccountVatPanel(SelectionEvent<Account> event) {
		callback.onBalance(event.getSelectedItem());
		_paintEntry();
	}
	
	private void _paintEntry() {
		AccountEntry[] entries = InvoiceRecorder.recordInvoice(invoice);
		onLog(entries);
	}
	
	@UiHandler({"registryBox", "series" , "number", "referenceCode"})
	public void onKeyUp(KeyUpEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
            extraPanel.setFocus();
        }
	}
	
	@UiHandler("withholdingBase")
	public void onValueChangeWithholdingBase(ValueChangeEvent<Double> event) {
		invoice.setWithholdingBase( event.getValue() );
		invoice.calculateInvoiceTotals();
		populateWithholding();
		_paintEntry();
	}
	@UiHandler("withholdingPercent")
	public void onValueChangeWithholdingPercent(ValueChangeEvent<Double> event) {
		invoice.setWithholdingPercent( event.getValue() );
		invoice.calculateInvoiceTotals();
		populateWithholding();
		_paintEntry();
	}
	@UiHandler("withholdingQuota")
	public void onValueChangeWithholdingQuota(ValueChangeEvent<Double> event) {
		invoice.setWithholdingQuota( event.getValue() );
		invoice.calculateInvoiceTotals();
		populateWithholding();
		_paintEntry();
	}
	
	@UiHandler("withholdingAccount")
	public void onSelectionWithholdingAccount(SelectionEvent<Account> event) {
		invoice.setWithholdingAccount( event.getSelectedItem() );
		callback.onBalance(event.getSelectedItem());
		_paintEntry();
	}
	@UiHandler("withholdingType")
	public void onChangeWithholdingType(ChangeEvent event) {
		invoice.setWithholdingType( withholdingType.getValue() );
	}
	
	public void setFocus(boolean b) {
		registryBox.setFocus(b);
	}

	public void onLog(AccountEntry entry) {
		workingLog.clear();
		workingLog.addPreview(entry);			
	}
	
	public void onLog(AccountEntry[] entries) {
		workingLog.clear();
		for (int i = (entries.length - 1); i>=0; i--) {
			workingLog.addPreview(entries[i]);
		}
	}
	@Override
	public boolean isUpdatable() {
		return isNew() ||  (super.isUpdatable() && getAccountEntry().isInvoice());
	}
	@Override
	public AccountEntryType getAccountEntryType() {
		return (invoice != null && invoice.getRegistry() != null && invoice.getRegistry().getType() != null)
				?invoice.getRegistry().getType().getAccountEntryType()
				:null;
	}
	
	@Override
	public void save(final AsyncCallback<AccountEntry[]> callback) {
		
		fiscalService.save(AccountEntryModule.getCurrentDomainName()
				,AccountEntryModule.getCurrentDomain()
				, invoice, new AsyncCallback<AccountingInvoice>() {

			@Override
			public void onSuccess(AccountingInvoice result) {
				invoice = result;
				int entriesSize = invoice.getAccountEntries().size();
				AccountEntry[] entries = new AccountEntry[entriesSize];  
				callback.onSuccess(invoice.getAccountEntries().toArray(entries));
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

		});
	}
	
	private class InvoicePanelCallback implements IInvoicePanelCallback {
		
		@Override
		public void onBalance(Account account) {
			callback.onBalance(account);
		}
		@Override
		public void onBalance(AccountEntry entry) {
			callback.onBalance(entry);
		}
		@Override
		public AonConfiguration getConfiguration() {
			return callback.getConfiguration();
		}
		
		@Override
		public void onError(String msg) {
			callback.onError(msg);
		}
		
		@Override
		public AccountingInvoice getInvoice() {
			return invoice;
		}

		@Override
		public void transactionChanged() {
			vatPanel.transactionChanged();
			invoice.calculateInvoiceTotals();
			_paintEntry();
		}

		@Override
		public void withholdingChanged() {
			vatPanel.withholdingChanged();
			enableWithholdingIfNeeded();
			_paintEntry();
		}

		@Override
		public void surchargeChanged() {
			enableSurchargeIfNeeded();
			_paintEntry();
		}
		@Override
		public void invoiceTotalChanged() {
			invoice.calculateInvoiceTotals();
			invoiceTotal.setValue(invoice.getTotalInvoice());
		}
		@Override
		public void enableInvoiceTotal(boolean enable) {
			invoiceTotal.setEnabled(enable);
		}

		@Override
		public void setFocusOnRegistry() {
			registryBox.setFocus(true);
		}

		@Override
		public void paintEntry() {
			_paintEntry();
		}

		@Override
		public boolean isInvestAssetsAvailable() {
			return !invoice.isSales() 
				&& !invoice.isSurcharge()
				&& invoice.isOutputVatEnabled() != invoice.isInputVatEnabled()
				&& callback.getConfiguration().isInvestAssetsAvailable();
			
		}

		@Override
		public IWizardContent getWizardContent() {
			return callback.getWizardContent();
		}

		@Override
		public void onRefreshId() {
			callback.onRefreshId();
			
		}

		@Override
		public void onStatement(Integer accountId) {
			callback.onStatement(accountId);
		}

		@Override
		public void save(ClickEvent event) {
			callback.save(event);
		}
		@Override
		public String getDomainName() {
			return callback.getDomainName();
		}
		@Override
		public int getDomainId() {
			return callback.getDomainId();
		}

	};
	
	
}
