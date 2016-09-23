package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.WithholdingTypeListBox;
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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


public class InvoicePanel extends WizardContentBase {
	
	static FiscalServiceAsync fiscalService;
	
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
	
	@UiField(provided=true)
	InvoiceVATPanel vatPanel;
	@UiField
	Label withholdingLabel;
	
	@UiField
	HTMLPanel withholdingPanel;
	@UiField
	ListBox withholdingTaxs;
	@UiField
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
	private InvoicePanelVisitor invoicePanelVisitor;
	private IAccountEntryModuleCallback callback;
	
	
	public InvoicePanel(final IAccountEntryModuleCallback callback) {
		this.callback = callback;
		
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		
		invoicePanelVisitor = new InvoicePanelVisitor();
		InvoicePanelCallback invoiceCallback = new InvoicePanelCallback();
		
		registryBox = new AccountingRegistryBox(AccountEntryModule.getCurrentDomainName()
				, AccountEntryModule.getCurrentDomain(), true);
		withholdingAccount = new AccountBox(AccountEntryModule.getCurrentDomainName()
				, AccountEntryModule.getCurrentDomain(), false);
		vatPanel = new InvoiceVATPanel( invoiceCallback );
		extraPanel = new InvoiceExtraPanel( invoiceCallback );
		
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
	public void select(AccountEntry entry) {
		fiscalService.getAccountingInvoice(
				 AccountEntryModule.getCurrentDomainName()
				,AccountEntryModule.getCurrentDomain()
				,entry.getId()
				, new AsyncCallback<AccountingInvoice>() {
					
					@Override
					public void onSuccess(AccountingInvoice result) {
						setInvoice(result);
						paint();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						callback.onError(caught.getMessage());
					}
				});
	}

	@Override
	public void paint() {
		if (invoice != null) {
			fillSalesSeries();
			fillWithholdingTaxs();
			extraPanel.paint();
			vatPanel.paint();
			// #TODO 
			enableWithholdingIfNeeded();
			// -----
			_paintEntry();
		}
	}

	private void enableWithholdingIfNeeded() {
		withholdingPanel.setVisible(extraPanel.isWithholding());
		if (extraPanel.isWithholding()) populateWithholding();
		withholdingLabel.setVisible(extraPanel.isWithholding());
		withholdingTaxs.setVisible(extraPanel.isWithholding());
		withholdingBase.setVisible(extraPanel.isWithholding());
		withholdingBase.setReadOnly(true);
		withholdingPercent.setVisible(extraPanel.isWithholding());
		withholdingQuota.setVisible(extraPanel.isWithholding());
		withholdingQuota.setReadOnly(true);
		withholdingAccount.setVisible(extraPanel.isWithholding());
		withholdingType.setVisible(extraPanel.isWithholding());
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
		withholdingTaxs.setWidth("100px");
		if (callback.getConfiguration().getWithholdingTaxes() != null && callback.getConfiguration().getWithholdingTaxes().size() > 0) {
			for (Tax tax : callback.getConfiguration().getWithholdingTaxes()) {
				withholdingTaxs.addItem(tax.getName(),AonNumberUtils.toString( tax.getId()));
			}
		}
	}
	@UiHandler("series")
	public void onSelectSeries(ChangeEvent event) {
		invoice.getInvoice().setSeries(series.getSelectedValue());
		_paintEntry();
	}
	@UiHandler("number")
	public void onValueChangeEvent(ValueChangeEvent<Integer> event) {
		invoice.getInvoice().setNumber(number.getValue());
		_paintEntry();
	}

	@UiHandler("registryBox")
	public void onSelectRegistry(SelectionEvent<AccountingRegistry> event) {
		initializeInvoice(event.getSelectedItem());
	}
	
	private void initializeInvoice(final AccountingRegistry ar) {
		fiscalService.initializeInvoice(
				 AccountEntryModule.getCurrentDomainName()
				,AccountEntryModule.getCurrentDomain()
				,ar.getType().getInvoiceType()
				,ar.getId()
				,getAccountEntry().getEntryDate()
				, new AsyncCallback<AccountingInvoice>() {
					
					@Override
					public void onSuccess(AccountingInvoice result) {
						invoice = result;
//						extraPanel.invoiceChanged( result );
						
						Account account = new Account();
						account.setId(ar.getAccountId());
						account.setCode(ar.getAccountCode());
						account.setDescription(ar.getAccountDescription());
						callback.onBalance(account);
						
						vatPanel.setSuggestedAccounts(invoice.getSuggestedAccounts());
						invoice.getRegistry().getType().visit(invoice.getRegistry(),invoicePanelVisitor);
						paint();
						extraPanel.invoiceChanged(result);
						onChangeWithholdingTaxs(null);
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
	}

	@UiHandler("withholdingTaxs")
	public void onChangeWithholdingTaxs(ChangeEvent event) {
		for (Tax tax : callback.getConfiguration().getWithholdingTaxes()) {
			if ( AonNumberUtils.toInteger( withholdingTaxs.getSelectedValue()).equals(tax.getId())  ) {
				withholdingPercent.setValue(tax.getPercentage(), true);
				Account taxAccount = invoice.isSales()
						?tax.getSalesAccount()
						:tax.getPurchaseAccount();
				if (taxAccount == null) {
					taxAccount = invoice.isSales()
						?callback.getConfiguration().getDefaultChargedRetAccount()
						:callback.getConfiguration().getDefaultPaidRetAccount();
				}
				withholdingType.setValue(tax.getWithholdingType());
				withholdingAccount.setAccount(taxAccount);
			}
		}
	}

	private class InvoicePanelVisitor implements IAccountingRegistryTypeVisitor {

		@Override
		public void visitCustomer(AccountingRegistry reg) {
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
			invoice.getInvoice().setReferenceCode(null);
		}

		@Override
		public void visitCreditor(AccountingRegistry reg) {
			invoiceDataPanel.setVisible(true);
			series.setVisible(false);
			number.setVisible(false);
			referenceCode.setVisible(true);
			referenceCode.setFocus(true);
			withholdingType.setVisible(reg.isWithholding());
		}

		@Override
		public void visitSupplier(AccountingRegistry reg) {
			invoiceDataPanel.setVisible(true);
			series.setVisible(false);
			number.setVisible(false);
			referenceCode.setVisible(true);
			referenceCode.setFocus(true);
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
		AccountEntry[] entries = InvoiceRecorder.recordInvoice( getAccountEntry(),invoice);
		onLog(entries);
	}
	
	@UiHandler("registryBox")
	public void onKeyUp(KeyUpEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
            extraPanel.setFocus();
        }
	}
	@UiHandler("withholdingPercent")
	public void onValueChangeWithholdingPercent(ValueChangeEvent<Double> event) {
		invoice.setWithholdingPercent( event.getValue() );
		populateWithholding();
		_paintEntry();
	}
	
	@UiHandler("withholdingAccount")
	public void onSelectionWithholdingAccount(SelectionEvent<Account> event) {
		invoice.setWithholdingAccount( event.getSelectedItem() );
		callback.onBalance(event.getSelectedItem());
		_paintEntry();
	}

	public void setFocus(boolean b) {
		registryBox.setFocus(b);
	}

	public void onLog(AccountEntry entry) {
		workingLog.clear();
		workingLog.add(entry);			
	}
	
	public void onLog(AccountEntry[] entries) {
		workingLog.clear();
		for (AccountEntry entry : entries) {
			workingLog.add(entry,"PREVISUALIAZACI\u00D3N");			
		}
	}
	
	@Override
	public AccountEntryType getAccountEntryType() {
		return (invoice != null && invoice.getRegistry() != null && invoice.getRegistry().getType() != null)
				?invoice.getRegistry().getType().getAccountEntryType()
				:null;
	}
	
	private class InvoicePanelCallback implements IInvoicePanelCallback {
		
		@Override
		public void onBalance(Account account) {
			callback.onBalance(account);
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

	};
	
	
}
