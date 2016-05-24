package com.esferalia.aon.gwt.fiscal.client.accounting;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.WithholdingTypeListBox;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.InvoiceRecorder;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.DomEvent;
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
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


public class InvoicePanel extends ResizeComposite implements RequiresResize {
	
	
	public static interface IInvoicePanelCallback extends IAccountEntryModuleCallback{
		AccountingInvoice getInvoice();
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
	DoubleBox withholdingPercent;
	@UiField
	DoubleBox withholdingQuota;
	@UiField
	WithholdingTypeListBox withholdingType;
	@UiField(provided=true)
	AccountBox withholdingAccount;
	
	@UiField(provided=true)
	InvoiceExtraPanel extraPanel;

	
	private AccountingInvoice invoice;
	private InvoicePanelVisitor invoicePanelVisitor;
	private IAccountEntryModuleCallback callback;
	
	public InvoicePanel(final IAccountEntryModuleCallback callback) {
		this.callback = callback;
		registryBox = new AccountingRegistryBox(AccountEntryModule.getCurrentDomainName()
				, AccountEntryModule.getCurrentDomain(), true);
		withholdingAccount = new AccountBox(AccountEntryModule.getCurrentDomainName()
				, AccountEntryModule.getCurrentDomain(), false);
		IInvoicePanelCallback invoiceCallback = new IInvoicePanelCallback() {
			
			@Override
			public void showWorkingLog(AccountEntry entry) {
				callback.showWorkingLog(entry);
			}
			
			@Override
			public void showWorkingLog(AccountEntry[] entries) {
				callback.showWorkingLog(entries);
			}

			@Override
			public void onShowBalance(Account account) {
				callback.onShowBalance(account);
			}
			
			@Override
			public AonConfiguration getConfiguration() {
				return callback.getConfiguration();
			}
			
			@Override
			public AccountEntryObject getAccountEntry() {
				return callback.getAccountEntry();
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
				paintEntry();
			}

			@Override
			public void withholdingChanged() {
				enableWithholdingIfNeeded();
				withholdingType.setValue(extraPanel.isWithholding()?WithholdingType.PROFESSIONAL:null);
				paintEntry();
			}

			@Override
			public void surchargeChanged() {
				enableSurchargeIfNeeded();
				paintEntry();
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
				paintEntry();
			}

		};
		vatPanel = new InvoiceVATPanel( invoiceCallback );
		extraPanel = new InvoiceExtraPanel( invoiceCallback );
		
		Widget ui = DATA_BINDER.createAndBindUi(this);
		initWidget(ui);
		number.addStyleName(AON.AON_CSS.aonMarginRight5());
		
		invoice = new AccountingInvoice();
		invoiceDataPanel.setVisible(false);
		enableWithholdingIfNeeded();
		withholdingPanel.setVisible(false);
		fillSalesSeries();
		fillWithholdingTaxs();
		invoicePanelVisitor = new InvoicePanelVisitor();
	}
	
	private void enableWithholdingIfNeeded() {
		withholdingLabel.setVisible(extraPanel.isWithholding());
		withholdingTaxs.setVisible(extraPanel.isWithholding());
		withholdingPercent.setVisible(extraPanel.isWithholding());
		withholdingQuota.setVisible(extraPanel.isWithholding());
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
			int i = 0;
			for (Tax tax : callback.getConfiguration().getWithholdingTaxes()) {
				withholdingTaxs.addItem(tax.getName(),AonNumberUtils.toString( tax.getId()));
				if (callback.getConfiguration().getDefaultWithholdingPercent() != null 
					&& tax.getId() == callback.getConfiguration().getDefaultWithholdingPercent().getId() ) {
					withholdingTaxs.setSelectedIndex(i);
					DomEvent.fireNativeEvent(Document.get().createChangeEvent(), withholdingTaxs);
				}
			}
		}
	}
	@UiHandler("series")
	public void onSelectSeries(ChangeEvent event) {
		invoice.getInvoice().setSeries(series.getSelectedValue());
		paintEntry();
	}
	@UiHandler("number")
	public void onValueChangeEvent(ValueChangeEvent<Integer> event) {
		invoice.getInvoice().setNumber(number.getValue());
		paintEntry();
	}

	@UiHandler("registryBox")
	public void onSelectRegistry(SelectionEvent<AccountingRegistry> event) {
		final AccountingRegistry ar = event.getSelectedItem();
		AccountEntryModule.fiscalService.initializeInvoice(
			 AccountEntryModule.getCurrentDomainName()
			,AccountEntryModule.getCurrentDomain()
			,ar.getType().getInvoiceType()
			,ar.getId()
			,callback.getAccountEntry().getAccountEntry().getEntryDate()
			, new AsyncCallback<AccountingInvoice>() {
				
				@Override
				public void onSuccess(AccountingInvoice result) {
					invoice = result;
					extraPanel.invoiceChanged( result );
					
					Account account = new Account();
					account.setId(ar.getAccountId());
					account.setCode(ar.getAccountCode());
					account.setDescription(ar.getAccountDescription());
					
					callback.onShowBalance(account);
					invoice.getRegistry().getType().visit(invoicePanelVisitor);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					callback.onError(caught.getMessage());
				}
			});
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
		public void visitCustomer() {
			for (int i = 0; i < series.getItemCount(); i++) {
				if (AonStringUtils.equals(invoice.getInvoice().getSeries(), series.getValue(i))) {
					series.setSelectedIndex(i);
				}
			}
			number.setValue(invoice.getInvoice().getNumber());
			invoiceDataPanel.setVisible(true);
			withholdingPanel.setVisible(true);
			series.setVisible(true);
			number.setVisible(true);
			referenceCode.setVisible(false);
			series.setFocus(true);
			invoice.getInvoice().setReferenceCode(null);
			onChangeWithholdingTaxs(null);
			vatPanel.paint();
			paintEntry();
		}

		@Override
		public void visitCreditor() {
			invoiceDataPanel.setVisible(true);
			withholdingPanel.setVisible(true);
			series.setVisible(false);
			number.setVisible(false);
			referenceCode.setVisible(true);
			referenceCode.setFocus(true);
			withholdingType.setVisible(extraPanel.isWithholding());
			onChangeWithholdingTaxs(null);
			vatPanel.paint();
			paintEntry();
		}

		@Override
		public void visitSupplier() {
			invoiceDataPanel.setVisible(true);
			withholdingPanel.setVisible(true);
			series.setVisible(false);
			number.setVisible(false);
			referenceCode.setVisible(true);
			referenceCode.setFocus(true);
			onChangeWithholdingTaxs(null);
			vatPanel.paint();
			paintEntry();
		}
		
	}
	
	@UiHandler("referenceCode")
	public void onValueChangeReferenceCode(ValueChangeEvent<String> event) {
		invoice.getInvoice().setReferenceCode(referenceCode.getValue());
		paintEntry();
	}
	
	@UiHandler("invoiceTotal")
	public void onValueChangeInvoiceTotal(ValueChangeEvent<Double> event) {
		if (invoiceTotal.getValue() == null) invoiceTotal.setValue(0.0, false);
		vatPanel.invoiceTotalChanged(invoiceTotal.getValue());
	}
	@UiHandler("vatPanel")
	public void onValueChangeAccountVatPanel(ValueChangeEvent<InvoiceVAT> event) {
		paintEntry();
	}
	@UiHandler("vatPanel")
	public void onSelectionAccountVatPanel(SelectionEvent<Account> event) {
		callback.onShowBalance(event.getSelectedItem());
	}
	
	private void paintEntry() {
		AccountEntry[] entries = InvoiceRecorder.recordInvoice(callback.getAccountEntry().getAccountEntry(),invoice);
		callback.showWorkingLog(entries);
	}
	
	@UiHandler("registryBox")
	public void onKeyUp(KeyUpEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
            extraPanel.setFocus();
        }
	}
	
}
