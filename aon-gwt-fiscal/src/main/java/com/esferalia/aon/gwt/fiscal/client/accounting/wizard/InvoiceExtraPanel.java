package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Country2ListBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DocumentTypeListBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.InvoicePanel.IInvoicePanelCallback;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

public class InvoiceExtraPanel extends ScrollPanel implements HasValueChangeHandlers<Void>,HasSelectionHandlers<AccountingInvoice> {
	
	private AccountingRegistryVisitor accountingRegistryVisitor;
	
	static FiscalServiceAsync fiscalService;
	protected IAccountEntryModuleCallback callback;
	
	protected static FiscalServiceAsync getFiscalService() {
		if (fiscalService == null) {
			FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
			fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		}
		return fiscalService;
	}
	
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
		setStyleName(AON.AON_CSS.aonInvoicePanelEast());
		getElement().getStyle().setBackgroundColor(InvoicePanel.BACKGROUND_COLOR);
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
	}


	private void paintWorkplace(final IInvoicePanelCallback callback) {
		
		final LinkedList<Workplace> list = callback.getModule().getConfiguration().getWorkplaces();
		if (list != null && list.size() > 1) {
			FlowPanel panel = new  FlowPanel();
			panel.setStyleName(AON.AON_CSS.aonInvoicePanelInner());
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
		eastPanelInner.setStyleName(AON.AON_CSS.aonInvoicePanelInner());
		eastPanelInner.addStyleName(AON.AON_CSS.aonNowrap());
		eastPanelInner.setVisible(false);
		invoiceTypeLabel = new InlineLabel();
		invoiceTypeLabel.setStyleName(AON.AON_CSS.aonWidthAll());
		invoiceTypeLabel.addStyleName(AON.AON_CSS.aonMarginAuto());
		invoiceTypeLabel.addStyleName(AON.AON_CSS.aonInvoiceLabel());
		invoiceTypeLabel.addStyleName(AON.AON_CSS.aonTextCenter());
		eastPanelInner.add(invoiceTypeLabel);
		
		Label rectLabel = new Label();
		rectLabel.setStyleName(AON.AON_CSS.aonBold());
		rectLabel.addStyleName(AON.AON_CSS.aonColorRed());
		rectLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		rectLabel.addStyleName(AON.AON_CSS.aonMarginLeft());
		rectLabel.addStyleName(AON.AON_CSS.aonCursorPointer());
		rectLabel.addStyleName(AON.AON_CSS.aonTextUnderline());
		if (callback.getInvoice().getInvoice().isRectifier()) {
			rectLabel.setText("Factura rectificativa");
		}
		if (callback.getInvoice().getInvoice().isRectified()) {
			rectLabel.setText( "Factura rectificada");
		}
		rectLabel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				getFiscalService().getAccountingInvoiceFromInvoice(AccountEntryModule.getCurrentDomainName()
						,AccountEntryModule.getCurrentDomain()
						,callback.getInvoice().getInvoice().getRectificationInvoice()
						,new AsyncCallback<AccountingInvoice>() {
							
							@Override
							public void onSuccess(AccountingInvoice result) {
								if (result != null) {
									SelectionEvent.<AccountingInvoice>fire( InvoiceExtraPanel.this, result);
								} else {
									callback.getModule().onError("No se ha encontrado la factura.");	
								}
							}
							
							@Override
							public void onFailure(Throwable caught) {
								callback.getModule().onError(caught.getMessage());
							}
						});								
			}
		});
		eastPanelInner.add(rectLabel);
		
		
		
		flexContainer.add(eastPanelInner);
	}
	
	private void paintDocument(final IInvoicePanelCallback callback) {
		FlowPanel panel = new  FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonInvoicePanelInner());
		
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
		panel.setStyleName(AON.AON_CSS.aonInvoicePanelInner());
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
		panel.setStyleName(AON.AON_CSS.aonInvoicePanelInner());
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
		taxDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				callback.getInvoice().getInvoice().setTaxDate(event.getValue());
			}
		});
		
		panel.add(taxDate);
		flexContainer.add(panel);		
	}
		
	private void paintTransaction(final IInvoicePanelCallback callback) {
		FlowPanel panel = new  FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonInvoicePanelInner());
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
				InvoiceCalculator.calculate(callback.getInvoice());
				ValueChangeEvent.fire(InvoiceExtraPanel.this, null );
				// callback.transactionChanged();
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
		panel.setStyleName(AON.AON_CSS.aonInvoicePanelInner());
		
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
		panel.setStyleName(AON.AON_CSS.aonInvoicePanelInner());
		
		surcharge = new CheckBox(AON.MSG.surcharge());
		surcharge.setTabIndex(Integer.MAX_VALUE);
		surcharge.setStyleName(AON.AON_CSS.aonInline());
		surcharge.addStyleName(AON.AON_CSS.aonWidth150());
		surcharge.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				callback.getInvoice().getInvoice().setSurcharge(surcharge.getValue());
				InvoiceCalculator.calculate(callback.getInvoice());
				ValueChangeEvent.fire(InvoiceExtraPanel.this, null );
				//callback.surchargeChanged();
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
		panel.setStyleName(AON.AON_CSS.aonInvoicePanelInner());
		
		withholding = new CheckBox(AON.MSG.withholding());
		withholding.setTabIndex(Integer.MAX_VALUE);
		withholding.setStyleName(AON.AON_CSS.aonInline());
		withholding.addStyleName(AON.AON_CSS.aonWidth150());
		withholding.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				callback.getInvoice().getInvoice().setWithholding(withholding.getValue());
				InvoiceCalculator.calculate(callback.getInvoice());
				ValueChangeEvent.fire(InvoiceExtraPanel.this, null );
				//callback.withholdingChanged();
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
				InvoiceCalculator.calculate(callback.getInvoice());
				ValueChangeEvent.fire(InvoiceExtraPanel.this, null );
				//callback.withholdingFarmerChanged();
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

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Void> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<AccountingInvoice> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	
}
