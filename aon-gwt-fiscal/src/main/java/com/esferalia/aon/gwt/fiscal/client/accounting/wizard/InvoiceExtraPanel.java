package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.Country2ListBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DocumentTypeListBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.InvoicePanel.IInvoicePanelCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

public class InvoiceExtraPanel extends ScrollPanel  {
	
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
	
	public InvoiceExtraPanel() {
		accountingRegistryVisitor = new AccountingRegistryVisitor();
		setStyleName(AON.AON_CSS.aonWizardPanelEast());
		flexContainer = new  FlowPanel();
		flexContainer.setStyleName(AON.AON_CSS.aonFlexContainer());
		add(flexContainer);
	}
	
	void paint(final IInvoicePanelCallback callback) {
		flexContainer.clear();
		paintLabel(callback);
		paintWorkplace(callback);
		paintDocument(callback);
		paintName(callback);
		paintDate(callback);
		paintTransaction(callback);
		paintChecks1(callback);
		paintChecks2(callback);
		paintChecks3(callback);
		paintPayment(callback);
	}

	private void paintWorkplace(final IInvoicePanelCallback callback) {
		
		final LinkedList<Workplace> list = callback.getConfiguration().getWorkplaces();
		if (list != null && list.size() > 1) {
			FlowPanel panel = new  FlowPanel();
			panel.setStyleName(AON.AON_CSS.aonWizardPanelInner());
			InlineLabel label = new InlineLabel(AON.MSG.workplace());
			label.setStyleName(AON.AON_CSS.aonInnerLabel());
			label.addStyleName(AON.AON_CSS.aonWidth70());
			panel.add(label);
			final ListBox workplaces = new ListBox();
			workplaces.setStyleName(AON.AON_CSS.aonMarginRight5());
			panel.add(workplaces);
			flexContainer.add(panel);
			
			int i = 0;
			workplaces.addItem("----------", (String) null);
			for (Workplace workplace : list ) {
				i++;
				workplaces.addItem(workplace.getDescription(), AonNumberUtils.toString(workplace.getId()));
				if (AonNumberUtils.equals(callback.getInvoice().getWorkplace(), workplace.getId())) {
					workplaces.setSelectedIndex(i);
				}
			}
			workplaces.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					Integer workplaceId = AonNumberUtils.toInteger(workplaces.getSelectedValue());
					callback.getInvoice().setWorkplace(workplaceId);
				}
			});
			workplaces.addKeyUpHandler(new KeyUpHandler() {
				
				@Override
				public void onKeyUp(KeyUpEvent event) {
					if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
						callback.setFocusOnRegistry();
			        }
				}
			});
			
		}
		
	}

	private void paintLabel(final IInvoicePanelCallback callback) {
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
	
	private void paintDocument(final IInvoicePanelCallback callback) {
		FlowPanel panel = new  FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonWizardPanelInner());
		
		InlineLabel label = new InlineLabel(AON.MSG.document());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		label.addStyleName(AON.AON_CSS.aonWidth70());
		panel.add(label);
		
		rDocumentType = new DocumentTypeListBox();
		rDocumentType.setTabIndex(Integer.MAX_VALUE);
		rDocumentType.setStyleName(AON.AON_CSS.aonMarginRight5());
		rDocumentType.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					callback.setFocusOnRegistry();
		        }
			}
		});
		panel.add(rDocumentType);
		
		rDocumentCountry = new Country2ListBox();
		rDocumentCountry.setTabIndex(Integer.MAX_VALUE);
		rDocumentCountry.setStyleName(AON.AON_CSS.aonMarginRight5());
		rDocumentCountry.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					callback.setFocusOnRegistry();
		        }
			}
		});
		panel.add(rDocumentCountry);
		
		rDocument = new DocumentTextBox();
		rDocument.setTabIndex(Integer.MAX_VALUE);
		rDocument.addStyleName(AON.AON_CSS.aonMarginRight5());
		rDocument.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					callback.setFocusOnRegistry();
		        }
			}
		});
		panel.add(rDocument);
		
		flexContainer.add(panel);
	}

	private void paintName(final IInvoicePanelCallback callback) {
		FlowPanel panel = new  FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonWizardPanelInner());
		InlineLabel label = new InlineLabel(AON.MSG.name());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		label.addStyleName(AON.AON_CSS.aonWidth70());
		panel.add(label);
		
		rName = new TextBox();
		rName.setStyleName(AON.AON_CSS.aonInputText());
		rName.setTabIndex(Integer.MAX_VALUE);
		rName.setVisibleLength(30);
		rName.setMaxLength(40);
		rName.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					callback.setFocusOnRegistry();
		        }
			}
		});
		panel.add(rName);
		flexContainer.add(panel);		
	}

	private void paintDate(final IInvoicePanelCallback callback) {
		FlowPanel panel = new  FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonWizardPanelInner());
		InlineLabel label = new InlineLabel(AON.MSG.taxDate());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		label.addStyleName(AON.AON_CSS.aonWidth70());
		panel.add(label);
		
		taxDate = new DateBoxEx();
		taxDate.setTabIndex(Integer.MAX_VALUE);
		taxDate.getTextBox().addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					taxDate.hideDatePicker();
					callback.setFocusOnRegistry();
		        }
			}
		});
		panel.add(taxDate);
		flexContainer.add(panel);		
	}
		
	private void paintTransaction(final IInvoicePanelCallback callback) {
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
		transactionBox.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					callback.setFocusOnRegistry();
		        }
			}
		});
		panel.add(transactionBox);
		flexContainer.add(panel);		
	}
	
	private void paintChecks1(final IInvoicePanelCallback callback) {
		FlowPanel panel = new  FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonWizardPanelInner());
		
		service = new CheckBox(AON.MSG.service());
		service.setTabIndex(Integer.MAX_VALUE);
		service.setStyleName(AON.AON_CSS.aonInline());
		service.addStyleName(AON.AON_CSS.aonWidth150());
		service.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				callback.getInvoice().getInvoice().setService(service.getValue());
			}
		});
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
		investment.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				callback.getInvoice().getInvoice().setInvestment(investment.getValue());
			}
		});
		investment.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					callback.setFocusOnRegistry();
		        }
			}
		});
		panel.add(investment);
		
		
		flexContainer.add(panel);
	}

	private void paintChecks2(final IInvoicePanelCallback callback) {
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
		surcharge.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					callback.setFocusOnRegistry();
		        }
			}
		});
		panel.add(surcharge);
		
		vatAccrualPayment = new CheckBox(AON.MSG.vatAccrualPayment());
		vatAccrualPayment.setTabIndex(Integer.MAX_VALUE);
		vatAccrualPayment.setStyleName(AON.AON_CSS.aonInline());
		vatAccrualPayment.addStyleName(AON.AON_CSS.aonWidthAuto());
		vatAccrualPayment.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				callback.getInvoice().getInvoice().setVatAccrualPayment(vatAccrualPayment.getValue());
			}
		});
		vatAccrualPayment.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					callback.setFocusOnRegistry();
		        }
			}
		});
		panel.add(vatAccrualPayment);
		
		flexContainer.add(panel);
	}

	private void paintChecks3(final IInvoicePanelCallback callback) {
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
		withholding.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					callback.setFocusOnRegistry();
		        }
			}
		});
		panel.add(withholding);
		
		withholdingFarmer = new CheckBox(AON.MSG.withholdingFarmer());
		withholdingFarmer.setTabIndex(Integer.MAX_VALUE);
		withholdingFarmer.setStyleName(AON.AON_CSS.aonInline());
		withholdingFarmer.addStyleName(AON.AON_CSS.aonWidthAuto());
		withholdingFarmer.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				callback.getInvoice().getInvoice().setWithholdingFarmer(withholdingFarmer.getValue());
				callback.withholdingChanged();
			}
		});
		withholdingFarmer.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					callback.setFocusOnRegistry();
		        }
			}
		});
		panel.add(withholdingFarmer);
		
		flexContainer.add(panel);
	}

	private void paintPayment(final IInvoicePanelCallback callback) {
		FlowPanel payPanel = new  FlowPanel();
		payPanel.setStyleName(AON.AON_CSS.aonMargin());
		payPanel.addStyleName(AON.AON_CSS.aonPadding());
		payPanel.addStyleName(AON.AON_CSS.aonBorderTop());
		payPanel.addStyleName(AON.AON_CSS.aonBorderBottom());
		
		FlowPanel panel1 = new  FlowPanel();
		panel1.setStyleName(AON.AON_CSS.aonWizardPanelInner());
		InlineLabel label1 = new InlineLabel(AON.MSG.dueDate());
		label1.setStyleName(AON.AON_CSS.aonInnerLabel());
		label1.addStyleName(AON.AON_CSS.aonWidth120());
		panel1.add(label1);
		
		final DateBoxEx payDate = new DateBoxEx();
		payDate.setValue(callback.getInvoice().getPayDate());
		payDate.setTabIndex(Integer.MAX_VALUE);
		payDate.getTextBox().addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					payDate.hideDatePicker();
					callback.setFocusOnRegistry();
		        }
			}
		});
		panel1.add(payDate);
		payPanel.add(panel1);

		FlowPanel panel0 = new  FlowPanel();
		panel0.setStyleName(AON.AON_CSS.aonWizardPanelInner());

		InlineLabel label0 = new InlineLabel(AON.MSG.account());
		label0.setStyleName(AON.AON_CSS.aonInnerLabel());
		label0.addStyleName(AON.AON_CSS.aonWidth120());
		panel0.add(label0);
		final AccountBox payAccount = new AccountBox(callback.getDomainName(),callback.getDomainId() );
		payAccount.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					callback.setFocusOnRegistry();
		        }
			}
		});
		payAccount.addSelectionHandler(new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				callback.onBalance(event.getSelectedItem());
			}
		});
		payAccount.addSelectionHandler(new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				if (event.getSelectedItem() != null) {
					callback.getInvoice().setPayAccountId(event.getSelectedItem().getId());
					callback.getInvoice().setPayAccountCode(event.getSelectedItem().getCode());
					callback.getInvoice().setPayAccountDescription(event.getSelectedItem().getDescription());
					callback.paintEntry();
				} else {
					callback.getInvoice().setPayAccountId(null);
					callback.getInvoice().setPayAccountCode(null);
					callback.getInvoice().setPayAccountDescription(null);
					callback.paintEntry();
				}
			}
		});
		panel0.add(payAccount);
		payPanel.add(panel0);

		flexContainer.add(payPanel);		
	}

	public boolean isWithholding() {
		return withholding.getValue();
	}

	public boolean isSurcharge() {
		return surcharge.getValue();
	}

	public void invoiceChanged(AccountingInvoice invoice) {
		AccountingRegistry ar = invoice.getRegistry();
		if (ar == null || ar.getId() == null) {
			flexContainer.clear();
		} else {
			invoiceTypeLabel.setText( AON.MSG.accountEntryType( ar.getType().getAccountEntryType() ));
			eastPanelInner.setVisible(true);
			rDocumentType.setValue(ar.getDocumentType());
			rDocumentCountry.setValue(ar.getDocumentCountry());
			rDocument.setValue(ar.getDocument());
			rName.setValue(ar.getName());
			surcharge.setValue(invoice.isSurcharge());
			withholding.setValue(invoice.isWithholding());
			withholdingFarmer.setValue(invoice.isWithholdingFarmer());
			vatAccrualPayment.setValue(invoice.isVatAccrualPayment());
			service.setValue(invoice.isService());
			transactionBox.setValue(invoice.getTransaction());
			taxDate.setValue(invoice.getInvoice().getIssueDate());
			ar.getType().visit(invoice.getRegistry(),accountingRegistryVisitor);
		}
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
