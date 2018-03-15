package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.FullDocument;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.InvoicePanel.IInvoicePanelCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.InvoiceRectificationDataPanel.InvoiceRectificationDataPanelCallback;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
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
import com.google.gwt.user.client.Command;
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
	
	FullDocument fullDocument;
	TextBox rName;
	DateBoxEx taxDate;
	InvoiceTransactionListBox transactionBox;
	CheckBox service;
	CheckBox investment;
	CheckBox surcharge;
	CheckBox withholding;
	CheckBox withholdingFarmer;
	CheckBox vatAccrualPayment;
	Label rectify;
	private int tabindex;
	
	public InvoiceExtraPanel() {
		accountingRegistryVisitor = new AccountingRegistryVisitor();
		setStyleName(AON.AON_CSS.aonInvoicePanelEast());
		getElement().getStyle().setBackgroundColor(InvoicePanel.BACKGROUND_COLOR);
		flexContainer = new  FlowPanel();
		flexContainer.setStyleName(AON.AON_CSS.aonFlexContainer());
		add(flexContainer);
		tabindex = InvoicePanel.EXTRA_PANEL_TAB_OFFSET;
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
		paintButtons(callback);
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
			workplaces.setTabIndex(++tabindex);
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
		
		
		if (callback.getInvoice().getInvoice().getRectificationInvoice() != null 
			&& (callback.getInvoice().getInvoice().isRectifier() 
			 || callback.getInvoice().getInvoice().isRectified())) {
			
			Label rectLabel = new Label();
			rectLabel.setStyleName(AON.AON_CSS.aonBold());
			rectLabel.addStyleName(AON.AON_CSS.aonColorRed());
			rectLabel.addStyleName(AON.AON_CSS.aonFontSmall());
			rectLabel.addStyleName(AON.AON_CSS.aonMarginLeft());
			rectLabel.addStyleName(AON.AON_CSS.aonCursorPointer());
			rectLabel.addStyleName(AON.AON_CSS.aonTextUnderline());
			rectLabel.setText( AON.MSG.seeRectInvoiceAbbr());
			rectLabel.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					getFiscalService().getAccountingInvoiceFromInvoice(callback.getCurrentDomainName()
							,callback.getCurrentDomainId()
							,callback.getInvoice().getInvoice().getRectificationInvoice()
							,new AsyncCallback<AccountingInvoice>() {
								
								@Override
								public void onSuccess(AccountingInvoice result) {
									if (result != null) {
										SelectionEvent.<AccountingInvoice>fire( InvoiceExtraPanel.this, result);
									} else {
										callback.getModule().onError(AON.MSG.invoiceNotFound());	
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
		}
		
		
		flexContainer.add(eastPanelInner);
	}
	
	private void paintDocument(final IInvoicePanelCallback callback) {
		FlowPanel panel = new  FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonInvoicePanelInner());
		
		InlineLabel label = new InlineLabel(AON.MSG.document());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		label.addStyleName(AON.AON_CSS.aonWidth70());
		panel.add(label);
		
		fullDocument = new FullDocument();
		fullDocument.setTabIndex(++tabindex);
		++tabindex;
		++tabindex;
		fullDocument.getTypeWidget().addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					callback.setFocusOnRegistry();
		        }
			}
		});
		fullDocument.addTypeChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent arg0) {
				callback.getInvoice().getInvoice().setRegistryDocumentType(fullDocument.getType());
			}
		});
		fullDocument.getCountryWidget().addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					callback.setFocusOnRegistry();
		        }
			}
		});
		fullDocument.addCountryChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent arg0) {
				callback.getInvoice().getInvoice().setRegistryDocumentCountry(fullDocument.getCountry());
			}
		});
		fullDocument.getDocumentWidget().addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					callback.setFocusOnRegistry();
		        }
			}
		});
		fullDocument.addDocumentChangeHandler(new  ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> arg0) {
				callback.getInvoice().getInvoice().setRegistryDocument(fullDocument.getDocument());
			}
		});
		panel.add(fullDocument);
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
		rName.setTabIndex(++tabindex);
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
		rName.addValueChangeHandler(new  ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> arg0) {
				callback.getInvoice().getInvoice().setRegistryName(rName.getValue());
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
		taxDate.setTabIndex(++tabindex);
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
				decorateTaxDate(callback.getInvoice());
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
		transactionBox.setTabIndex(++tabindex);
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
		service.setTabIndex(++tabindex);
		service.setStyleName(AON.AON_CSS.aonInline());
		service.addStyleName(AON.AON_CSS.aonWidth150());
		service.setEnabled(!callback.getInvoice().isExpenses()); // Si es un gasto, true.
		service.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				callback.getInvoice().getInvoice().setService(service.getValue());
				InvoiceCalculator.calculate(callback.getInvoice());
				ValueChangeEvent.fire(InvoiceExtraPanel.this, null );
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
		investment.setTabIndex(++tabindex);
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
		surcharge.setTabIndex(++tabindex);
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
		vatAccrualPayment.setTabIndex(++tabindex);
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
		withholding.setTabIndex(++tabindex);
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
		withholdingFarmer.setTabIndex(++tabindex);
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
	
	private void paintButtons(final IInvoicePanelCallback callback) {
		if (!callback.getInvoice().getInvoice().isRectifier()) {
			FlowPanel panel = new  FlowPanel();
			panel.setStyleName(AON.AON_CSS.aonInvoicePanelInner());
			panel.addStyleName(AON.AON_CSS.aonMarginTop());
			
			rectify = new Label(AON.MSG.rectifyInvoice());
			panel.add(rectify);
			rectify.addStyleName(AON.AON_CSS.aonTextUnderline());
			rectify.addStyleName(AON.AON_CSS.aonCursorPointer());
			rectify.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					final InvoiceRectificationData data = new InvoiceRectificationData();
					data.setIssueDate(callback.getInvoice().getInvoice().getIssueDate());
					data.setType(callback.getInvoice().getInvoice().getType());
					data.setRectificationtype(RectificationType.NORMAL_RECTIFIER);
					data.setSettleFinances(true);
					final CustomDialog dialog = new CustomDialog();
					dialog.setCaption(AON.MSG.rectifyInvoice());

					final InvoiceRectificationDataPanel rectPanel = new InvoiceRectificationDataPanel();
					rectPanel.show(callback.getCurrentDomainName()
						,callback.getCurrentDomainId()
						,callback.getModule().getConfiguration()
						,data
						, new InvoiceRectificationDataPanelCallback() {
							
							@Override
							public void onCancel() {
								dialog.hide();
							}
							
							@Override
							public void onAccept(InvoiceRectificationData data) {
								getFiscalService().rectifyInvoice(callback.getCurrentDomainName()
										,callback.getCurrentDomainId()
										,callback.getInvoice().getInvoice().getId()
										,data
										,new AsyncCallback<AccountingInvoice>() {
										
										@Override
										public void onSuccess(AccountingInvoice result) {
											dialog.hide();
											if (result != null) {
												SelectionEvent.<AccountingInvoice>fire( InvoiceExtraPanel.this, result);
											} else {
												callback.getModule().onError("Error al rectificar la factura.");	
											}
										}
										
										@Override
										public void onFailure(Throwable caught) {
											dialog.hide();
											callback.getModule().onError(caught.getMessage());
										}
								});	
							}
					});
					dialog.add( rectPanel );
					dialog.center();
					dialog.show();
					
					Scheduler.get().scheduleDeferred(new Command() {
				        public void execute() {
				        	rectPanel.setFocus(true);
				        }
				    });		

				}
			});
			flexContainer.add(panel);
		}
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
			Invoice inv = invoice.getInvoice();
			invoiceTypeLabel.setText( getInvoiceLabel(ar.getType().getInvoiceType(),invoice.getInvoice().getRectificationType()));
			eastPanelInner.setVisible(true);
			//workplace
			fullDocument.setValue(inv.getRegistryDocumentType(),inv.getRegistryDocumentCountry(),inv.getRegistryDocument());
			rName.setValue(inv.getRegistryName());
			taxDate.setValue(invoice.getInvoice().getTaxDate());
			transactionBox.setValue(invoice.getTransaction());
			service.setValue(invoice.isService());
			investment.setValue(invoice.isInvestment());
			surcharge.setValue(invoice.isSurcharge());
			vatAccrualPayment.setValue(invoice.isVatAccrualPayment());
			withholding.setValue(invoice.isWithholding());
			withholdingFarmer.setValue(invoice.isWithholdingFarmer());
			decorateTaxDate(invoice);
			ar.getType().visit(invoice.getRegistry(),accountingRegistryVisitor);
		}
	}
	
	private void decorateTaxDate(AccountingInvoice ai) {
		Date issue = ai.getInvoice().getIssueDate();
		Date tax = ai.getInvoice().getTaxDate();
		if(    (issue == null && tax != null)
			|| (issue != null && tax == null)
			|| (issue.compareTo(tax) != 0)) {
			taxDate.addStyleName(AON.AON_CSS.aonChanged());
		} else {
			taxDate.removeStyleName(AON.AON_CSS.aonChanged());
		}
	}

	private String getInvoiceLabel(InvoiceType invoiceType, RectificationType rt) {
		String x = "";
		if (rt == null || rt == RectificationType.NONE) {
			x = "";
		} else {
			x = rt.getDescription() + " ";
		}
		String l = "";
		if (invoiceType == InvoiceType.SALES   ) l = "Factura "+x+"de Ventas";
		if (invoiceType == InvoiceType.EXPENSES) l = "Factura "+x+"de Gastos";
		if (invoiceType == InvoiceType.PURCHASE) l = "Factura "+x+"de Compra";
		return l;
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
		fullDocument.getDocumentWidget().setFocus(true);		
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Void> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<AccountingInvoice> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public void hideButtons(boolean enabled) {
		if (rectify != null) rectify.setVisible(enabled);
	}

	
}
