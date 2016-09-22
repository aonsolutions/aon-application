package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Country2ListBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DocumentTypeListBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.InvoicePanel.IInvoicePanelCallback;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

public class InvoiceExtraPanel extends ScrollPanel  {
	
	private IInvoicePanelCallback callback;
	
	private AccountingRegistryVisitor accountingRegistryVisitor;
	
	FlowPanel flexContainer;
	
	FlowPanel eastPanelInner;
	Label invoiceTypeLabel;
	
	DocumentTypeListBox rDocumentType;
	Country2ListBox rDocumentCountry;
	DocumentTextBox rDocument;
	TextBox rName;
	DateBoxEx taxDate;
	InvoiceTransactionListBox transactionBox;
	CheckBox service;
	CheckBox investment;
	CheckBox surcharge;
	CheckBox withholding;
	CheckBox withholdingFarmer;
	CheckBox vatAccrualPayment;
	
	public InvoiceExtraPanel(IInvoicePanelCallback callback) {
		setCallback(callback);
		accountingRegistryVisitor = new AccountingRegistryVisitor();
		setStyleName(AON.AON_CSS.aonWizardPanelEast());
		flexContainer = new  FlowPanel();
		flexContainer.setStyleName(AON.AON_CSS.aonFlexContainer());
		add(flexContainer);
	}
	public void setCallback(IInvoicePanelCallback invoiceCallback) {
		this.callback = invoiceCallback;
	}
	
	void paint() {
		paintLabel();
		paintDocument();
		paintName();
		paintDate();
		paintTransaction();
		paintChecks1();
		paintChecks2();
		paintChecks3();
			
	}

	private void paintLabel() {
		flexContainer.clear();
		eastPanelInner = new  FlowPanel();
		eastPanelInner.setStyleName(AON.AON_CSS.aonWizardPanelInner());
		eastPanelInner.setVisible(false);
		invoiceTypeLabel = new InlineLabel();
		invoiceTypeLabel.setStyleName(AON.AON_CSS.aonWidthAll());
		invoiceTypeLabel.addStyleName(AON.AON_CSS.aonMarginAuto());
		invoiceTypeLabel.addStyleName(AON.AON_CSS.aonWizardLabel());
		invoiceTypeLabel.addStyleName(AON.AON_CSS.aonTextCenter());
		eastPanelInner.add(invoiceTypeLabel);
		flexContainer.add(eastPanelInner);
	}
	
	private void paintDocument() {
		FlowPanel panel = new  FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonWizardPanelInner());
		InlineLabel label = new InlineLabel(AON.MSG.document());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		label.addStyleName(AON.AON_CSS.aonWidth70());
		panel.add(label);
		
		rDocumentType = new DocumentTypeListBox();
		rDocumentType.setTabIndex(Integer.MAX_VALUE);
		rDocumentType.setStyleName(AON.AON_CSS.aonMarginRight5());
		panel.add(rDocumentType);
		
		rDocumentCountry = new Country2ListBox();
		rDocumentCountry.setTabIndex(Integer.MAX_VALUE);
		rDocumentCountry.setStyleName(AON.AON_CSS.aonMarginRight5());
		panel.add(rDocumentCountry);
		
		rDocument = new DocumentTextBox();
		rDocument.setTabIndex(Integer.MAX_VALUE);
		rDocument.addStyleName(AON.AON_CSS.aonMarginRight5());
		rDocument.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					service.setFocus(true);
		        }
			}
		});
		panel.add(rDocument);
		
		flexContainer.add(panel);
	}

	private void paintName() {
		FlowPanel panel = new  FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonWizardPanelInner());
		InlineLabel label = new InlineLabel(AON.MSG.name());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		label.addStyleName(AON.AON_CSS.aonWidth70());
		panel.add(label);
		
		rName = new TextBox();
		rName.setStyleName(AON.AON_CSS.aonInputText());
		rName.setTabIndex(Integer.MAX_VALUE);
		rName.setVisibleLength(40);
		rName.setMaxLength(40);
		panel.add(rName);
		flexContainer.add(panel);		
	}

	private void paintDate() {
		FlowPanel panel = new  FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonWizardPanelInner());
		InlineLabel label = new InlineLabel(AON.MSG.taxDate());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		label.addStyleName(AON.AON_CSS.aonWidth70());
		panel.add(label);
		
		taxDate = new DateBoxEx();
		taxDate.setTabIndex(Integer.MAX_VALUE);
		panel.add(taxDate);
		flexContainer.add(panel);		
	}
		
	private void paintTransaction() {
		FlowPanel panel = new  FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonWizardPanelInner());
		InlineLabel label = new InlineLabel(AON.MSG.transaction());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		label.addStyleName(AON.AON_CSS.aonWidth70());
		panel.add(label);
		
		transactionBox = new InvoiceTransactionListBox();
		transactionBox.setTabIndex(Integer.MAX_VALUE);
		transactionBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				callback.getInvoice().getInvoice().setTransaction(transactionBox.getValue());
				callback.transactionChanged();
			}
		});
		panel.add(transactionBox);
		flexContainer.add(panel);		
	}
	
	private void paintChecks1() {
		FlowPanel panel = new  FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonWizardPanelInner());
		
		service = new CheckBox(AON.MSG.service());
		service.setTabIndex(Integer.MAX_VALUE);
		service.setStyleName(AON.AON_CSS.aonInline());
		service.addStyleName(AON.AON_CSS.aonWidth150());
		service.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					callback.setFocusOnRegistry();
		        }
			}
		});

		panel.add(service);
		
		investment = new CheckBox(AON.MSG.investAsset());
		investment.setTabIndex(Integer.MAX_VALUE);
		investment.setStyleName(AON.AON_CSS.aonInline());
		investment.addStyleName(AON.AON_CSS.aonWidthAuto());
		panel.add(investment);
		
		
		flexContainer.add(panel);
	}

	private void paintChecks2() {
		FlowPanel panel = new  FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonWizardPanelInner());
		
		surcharge = new CheckBox(AON.MSG.surcharge());
		surcharge.setTabIndex(Integer.MAX_VALUE);
		surcharge.setStyleName(AON.AON_CSS.aonInline());
		surcharge.addStyleName(AON.AON_CSS.aonWidth150());
		surcharge.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				callback.getInvoice().getInvoice().setSurcharge(surcharge.getValue());
				callback.surchargeChanged();
			}
		});
		panel.add(surcharge);
		
		vatAccrualPayment = new CheckBox(AON.MSG.vatAccrualPayment());
		vatAccrualPayment.setTabIndex(Integer.MAX_VALUE);
		vatAccrualPayment.setStyleName(AON.AON_CSS.aonInline());
		vatAccrualPayment.addStyleName(AON.AON_CSS.aonWidthAuto());
		panel.add(vatAccrualPayment);
		
		flexContainer.add(panel);
	}

	private void paintChecks3() {
		FlowPanel panel = new  FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonWizardPanelInner());
		
		withholding = new CheckBox(AON.MSG.withholding());
		withholding.setTabIndex(Integer.MAX_VALUE);
		withholding.setStyleName(AON.AON_CSS.aonInline());
		withholding.addStyleName(AON.AON_CSS.aonWidth150());
		withholding.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				callback.getInvoice().getInvoice().setWithholding(withholding.getValue());
				callback.withholdingChanged();
			}
		});
		panel.add(withholding);
		
		withholdingFarmer = new CheckBox(AON.MSG.withholdingFarmer());
		withholdingFarmer.setTabIndex(Integer.MAX_VALUE);
		withholdingFarmer.setStyleName(AON.AON_CSS.aonInline());
		withholdingFarmer.addStyleName(AON.AON_CSS.aonWidthAuto());
		panel.add(withholdingFarmer);
		
		flexContainer.add(panel);
	}

	public boolean isWithholding() {
		return withholding.getValue();
	}

	public boolean isSurcharge() {
		return surcharge.getValue();
	}

	public void invoiceChanged(AccountingInvoice invoice) {
		AccountingRegistry ar = invoice.getRegistry();
		invoiceTypeLabel.setText( AON.MSG.accountEntryType( ar.getType().getAccountEntryType() ));
		eastPanelInner.setVisible(true);
		
		rDocumentType.setValue(invoice.getRegistry().getDocumentType());
		rDocumentCountry.setValue(invoice.getRegistry().getDocumentCountry());
		rDocument.setValue(invoice.getRegistry().getDocument());
		rName.setValue(invoice.getRegistry().getName());
		surcharge.setValue(invoice.isSurcharge());
		withholding.setValue(invoice.isWithholding());
		callback.withholdingChanged();
		withholdingFarmer.setValue(invoice.isWithholdingFarmer());
		vatAccrualPayment.setValue(invoice.isVatAccrualPayment());
		transactionBox.setValue(invoice.getTransaction());
		taxDate.setValue(invoice.getInvoice().getIssueDate());
		invoice.getRegistry().getType().visit(invoice.getRegistry(),accountingRegistryVisitor);
	}
	
	
	private class AccountingRegistryVisitor implements IAccountingRegistryTypeVisitor {

		@Override
		public void visitCustomer(AccountingRegistry reg) {
			investment.setVisible(false);
			service.setVisible(true);
		}

		@Override
		public void visitCreditor(AccountingRegistry reg) {
			investment.setVisible(true);
		}

		@Override
		public void visitSupplier(AccountingRegistry reg) {
			investment.setVisible(true);
		}
		
	}

	public void setFocus() {
		rDocument.setFocus(true);		
	}

	
}
