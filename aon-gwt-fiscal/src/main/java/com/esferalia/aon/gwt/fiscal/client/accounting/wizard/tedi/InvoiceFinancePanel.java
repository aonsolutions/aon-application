package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.gwt.fiscal.client.ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.finance.FinanceActionsPanel;
import com.esferalia.aon.gwt.fiscal.client.finance.FinanceBankPanel;
import com.esferalia.aon.gwt.fiscal.client.finance.FinanceBankPanel.FinanceBankPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.finance.FinanceModuleCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFinanceStatusVisitor;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
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
import com.google.gwt.user.client.ui.Widget;

public class InvoiceFinancePanel extends ScrollPanel implements HasValueChangeHandlers<Finance>,HasAccountEntrySelectionHandlers {
	
	private static FinanceServiceAsync FINANCE_SERVICE;
	
	private FlowPanel container;
	private FlowPanel tab;
	private LinkedList<InvoiceFinancePanelRow> rows;
	
	private AonDoubleBox firstAmount = null;
	private CheckBox authFinanceCalculation;
	private AonAccountBox payAccount;
	private AonTableButton addButton;

	public InvoiceFinancePanel(IInvoicePanelCallback callback) {
		setStyleName(AON.CSS.aonWidthAll());
		getElement().getStyle().setBackgroundColor(EditableInvoicePanel.INNER_BACKGROUND_COLOR);
		
		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);
		container = new FlowPanel();
		container.setStyleName(AON.CSS.aonWidthAll());
		add(container);
		paint(callback);
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(AccountEntrySelectionHandler handler) {
		return super.addHandler(handler, AccountEntrySelectionEvent.getType());
	}
	
	private void updateAndRefresh(IInvoicePanelCallback callback, Finance fin) {
		AccountEntrySelectionEvent.fire( InvoiceFinancePanel.this, callback.getInvoice().getAccountEntry(), null);		
	}

	private FlowPanel getRow() {
		FlowPanel row = new FlowPanel();
		row.setStyleName(AON.CSS.aonDisplayTableRow());
		row.addStyleName(AON.CSS.aonNowrap());
		return row;
	}
	
	private FlowPanel getHeaderCell(Widget widget) {
		FlowPanel cell = new FlowPanel();
		cell.setStyleName(AON.CSS.aonDisplayGridHeaderCell());
		cell.addStyleName(AON.CSS.aonNowrap());
		cell.add(widget);
		return cell;
	}

	private FlowPanel getCell(Widget widget) {
		FlowPanel cell = new FlowPanel();
		cell.setStyleName(AON.CSS.aonDisplayTableCell());
		cell.addStyleName(AON.CSS.aonNowrap());
		cell.add(widget);
		return cell;
	}

	private void paint(IInvoicePanelCallback callback){
		container.clear();
		firstAmount = null;
		
		if (callback.getInvoice().getAccountEntry().getId() == null) {
			FlowPanel financeOptionsPanel = new FlowPanel();
			container.add(financeOptionsPanel);

			FlowPanel financeOptionsPanelRow = getRow();
			financeOptionsPanel.add(financeOptionsPanelRow);
			
			authFinanceCalculation = new CheckBox("Calcular vtos. autom\u00E1ticamente");
			authFinanceCalculation.setTabIndex(-1);
			authFinanceCalculation.setStyleName(AON.CSS.aonMarginRight());
			authFinanceCalculation.setValue(callback.getInvoice().isAuthFinanceCalculation());
			authFinanceCalculation.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					callback.getInvoice().setAuthFinanceCalculation(authFinanceCalculation.getValue());
					if (callback.getInvoice().isAuthFinanceCalculation()) {
						generateFinances( callback);
					}
				}
			});
			financeOptionsPanelRow.add(getCell(authFinanceCalculation));
			
			InlineLabel accountBoxLabel = new InlineLabel("Contabilizar los pagos contra la cuenta:");
			accountBoxLabel.setStyleName(AON.CSS.aonMarginRight());
			financeOptionsPanelRow.add(getCell(accountBoxLabel));

			payAccount = new AonAccountBox(callback.getCurrentDomainName(),callback.getCurrentDomainId(), callback.getCurrentUser());
			payAccount.setValue(callback.getInvoice().getPayAccountId()
					, callback.getInvoice().getPayAccountCode()
					, callback.getInvoice().getPayAccountDescription(), false);
			payAccount.addSelectionHandler(new SelectionHandler<Account>() {
				@Override
				public void onSelection(SelectionEvent<Account> event) {
						if (event.getSelectedItem() != null) {
							for (Finance f : callback.getInvoice().getInvoice().getFinances()) {
								f.setRecordable(true);
							}
							callback.getInvoice().setPayAccountId(event.getSelectedItem().getId());
							callback.getInvoice().setPayAccountCode(event.getSelectedItem().getCode());
							callback.getInvoice().setPayAccountDescription(event.getSelectedItem().getDescription());
						} else {
							for (Finance f : callback.getInvoice().getInvoice().getFinances()) {
								f.setRecordable(false);
							}
							callback.getInvoice().setPayAccountId(null);
							callback.getInvoice().setPayAccountCode(null);
							callback.getInvoice().setPayAccountDescription(null);
						}
						callback.paintEntry();
						callback.getInvoice().getAccountEntry().setDirty(true);
						callback.getModule().refreshIdLabel();
					}
			});
			financeOptionsPanelRow.add(getCell(payAccount));
		}
		
		tab = new FlowPanel();
		tab.setStyleName(AON.CSS.aonDisplayGrid());
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonMarginTopSep());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.getElement().getStyle().setBackgroundColor(EditableInvoicePanel.INNER_BACKGROUND_COLOR);
		container.add(tab);
		paintHeader();
		paintRows(callback);
		
		FlowPanel buttonsRow = new FlowPanel();
		buttonsRow.setStyleName(AON.CSS.aonDisplayGridFooterRow());
		tab.add(buttonsRow);
		addButton = new AonTableButton(AON.MSG.newAction(), AON.CSS.aonIconAdd(), 'w' );
		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Finance fin = new Finance()
						.setPayment(!callback.getInvoice().isSales())
						.setFinanceStatus(FinanceStatus.PENDING);
				double amount = 0.0;
				if (callback.getInvoice().getInvoice().getFinances() != null && callback.getInvoice().getInvoice().getFinances().size() > 0) {
					for (Finance f : callback.getInvoice().getInvoice().getFinances()) {
						if (!f.isRemoved()) {
							amount = AonMathUtils.round( amount + f.getAmount() );
						}
					}
					amount = AonMathUtils.round( callback.getInvoice().getTotalInvoice() - amount ); 
					// amount = AonMathUtils.isLessThanZero(amount)?0.0:amount;
					
					int idx = callback.getInvoice().getInvoice().getFinances().size() - 1;
					Finance last = callback.getInvoice().getInvoice().getFinances().get( idx );
					BankAccount ba = new BankAccount( last.getBankAccount()==null?null:last.getBankAccount().getIban()  );
					fin.setPayMethod(last.getPayMethod())
						.setPayMethodName(last.getPayMethodName())
						.setPayMethodType(last.getPayMethodType())
						.setDueDate(last.getDueDate())
						.setBankAccount(ba)
						.setBic(last.getBic())
						.setBankAlias(last.getBankAlias())
						.setChequeNumber(last.getChequeNumber())
						.setRegistry(last.getRegistry())
						.setRegistryDocument(last.getRegistryDocument())
						.setRegistryDocumentType(last.getRegistryDocumentType())
						.setRegistryDocumentCountry(last.getRegistryDocumentCountry())
						.setRegistryName(last.getRegistryName())
						.setRegistryAccountId(last.getRegistryAccountId())
						.setRegistryAccountCode(last.getRegistryAccountCode())
						.setRegistryAccountDescription(last.getRegistryAccountDescription())
					;
				}
				if (authFinanceCalculation != null) {
					authFinanceCalculation.setValue(false);
				}
				callback.getInvoice().setAuthFinanceCalculation(false);
				fin.setAmount(AonMathUtils.round( amount ));
				callback.getInvoice().getInvoice().addFinance( fin );
				addRow(callback, fin, true);
				checkAmounts( callback );
				if (callback.getInvoice().getPayAccountId() != null) {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						public void execute() {
							callback.paintEntry();
						}
					});
				}
			}
		});
		buttonsRow.add(getCell(addButton));
		checkAmounts( callback );
	}

	private void checkAmounts(IInvoicePanelCallback callback) {
		double total = callback.getInvoice().getTotalInvoice();
		if (callback.getInvoice().getInvoice().hasFinances()) {
			double sum = 0;
			for (Finance f : callback.getInvoice().getInvoice().getFinances() ) {
				if (!f.isRemoved()) {
					sum = AonMathUtils.round( sum + f.getAmount());
				}
			}
			if (!AonMathUtils.equals(sum, total)) {
				callback.getModule().onError(AON.MSG.invoiceFinancesAmountNotFit());
			} else {
				callback.getModule().onHideMessages();
			}
		} else {
			callback.getModule().onError(AON.MSG.invoiceFinancesEmpty());
		}
	}

	private void paintHeader() {
		FlowPanel headerRow = new FlowPanel();
		headerRow.setStyleName(AON.CSS.aonDisplayGridHeaderRow());
		tab.add(headerRow);
		
		headerRow.add(getHeaderCell(new Label()));
		headerRow.add(getHeaderCell(new Label(AON.MSG.dueDate())));
		headerRow.add(getHeaderCell(new Label(AON.MSG.payMethod())));
		headerRow.add(getHeaderCell(new Label(AON.MSG.bankAccount())));
		headerRow.add(getHeaderCell(new Label(AON.MSG.amount())));
		headerRow.add(getHeaderCell(new Label(AON.MSG.status())));
		headerRow.add(getHeaderCell(new Label()));
		headerRow.add(getHeaderCell(new Label(AON.MSG.actions())));
		Label l = new Label("");
		FlowPanel cell = new FlowPanel();
		cell.setStyleName(AON.CSS.aonDisplayTableCell());
		cell.addStyleName(AON.CSS.aonWidthAll());
		cell.add(l);
		headerRow.add(cell);
	}

	private void paintRows(IInvoicePanelCallback callback) {
		rows = new LinkedList<InvoiceFinancePanelRow>();
		if (callback.getInvoice().getInvoice().getFinances() != null && callback.getInvoice().getInvoice().getFinances().size() > 0) {
			for (Finance finance : callback.getInvoice().getInvoice().getFinances()) {
				addRow(callback, finance, false);
			}
		} 
	}

	private void addRow(IInvoicePanelCallback callback, Finance finance, boolean focus) {
		InvoiceFinancePanelRow invoiceFinanceRow = new InvoiceFinancePanelRow(callback, finance, focus);
		tab.add(invoiceFinanceRow);
		rows.add(invoiceFinanceRow);
	}


	private class InvoiceFinancePanelRow  extends FlowPanel {
		
		private void decorateDirty(Label dirty, Finance finance ) {
			dirty.setText( finance.isDirty()?"*":"" );
		}
		
		private void somethingChanged(IInvoicePanelCallback callback, Label dirty, Finance finance ) {
			decorateDirty(dirty, finance);
			if (callback.getInvoice().getPayAccountId() != null) {
				callback.paintEntry();
			}
			if (authFinanceCalculation != null) {
				callback.getInvoice().setAuthFinanceCalculation(false);
				authFinanceCalculation.setValue(false);
			}
		}
		
		private InvoiceFinancePanelRow(IInvoicePanelCallback callback, final Finance finance, boolean focus) {
			setStyleName(AON.CSS.aonDisplayGridRow());
			
			Label dirty = new Label();
			dirty.setStyleName(AON.CSS.aonColorBlue());
			decorateDirty(dirty, finance );
			add(getCell(dirty));
			
			AonDateBox dueDate = new AonDateBox();
			dueDate.setValue(finance.getDueDate());
			dueDate.setEnabled(finance.isFullPending());
			if (finance.isFullPending()) {
				dueDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<Date> event) {
						finance.setDueDate(dueDate.getValue());
						somethingChanged(callback, dirty, finance );
					}
				});
			}
			add(getCell(dueDate));

			ListBox payMethod = new ListBox();
			payMethod.setWidth("120px");
			payMethod.addItem(" ---- ");
			if (callback.getConfiguration().getPayMethods() != null) {
				int i = 1;
				for (PayMethod pm : callback.getConfiguration().getPayMethods() ) {
					payMethod.addItem( pm.getName(), AonNumberUtils.toString(pm.getId()));
					if ( AonNumberUtils.equals(pm.getId(), finance.getPayMethod() )) {
						payMethod.setSelectedIndex(i);
					}
					i++;
				}
			}
			payMethod.setEnabled(finance.isFullPending());
			if (finance.isFullPending()) {
				payMethod.addChangeHandler( new ChangeHandler() {
					
					@Override
					public void onChange(ChangeEvent event) {
						if (payMethod.getSelectedIndex() > 0 ) {
							PayMethod pm = callback.getConfiguration().getPayMethods().get(payMethod.getSelectedIndex()-1);
							finance.setPayMethod( pm.getId() );
							finance.setPayMethodName( pm.getName() );
							finance.setPayMethodType( pm.getType() );
						} else {
							finance.setPayMethod( null );
							finance.setPayMethodName( null );
							finance.setPayMethodType( null );
						}
						somethingChanged(callback, dirty, finance );
					}
				});
			}
			add(getCell(payMethod));

			FlowPanel bankAccountPanel = new FlowPanel();
			bankAccountPanel.setStyleName(AON.CSS.aonDisplayGridCellInner());
			bankAccountPanel.addStyleName(AON.CSS.aonNowrap());
			
			TextBox bankAccount = new TextBox();
			bankAccount.setStyleName(AON.CSS.aonInputText());
			bankAccount.setVisibleLength(25);
			bankAccount.setMaxLength(34);
			bankAccount.setValue(finance.getBankAccountSafeValue());
			bankAccount.setReadOnly(true);
			bankAccountPanel.add(bankAccount);
			AonTableButton editBank = new AonTableButton("Editar banco", AON.CSS.aonIconEdit()); 
			if (finance.isFullPending()) {
				editBank.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						final AonCustomDialog dialog = new AonCustomDialog();
						dialog.setCaption(AON.MSG.bankAccount());
						FinanceBankPanel bankPanel = new FinanceBankPanel();
						bankPanel.show(callback.getCurrentDomainName(), callback.getCurrentDomainId(),
								callback.getCurrentUser(), callback.getConfiguration(), finance,
								new FinanceBankPanelCallback() {

									@Override
									public void onCancel() {
										dialog.hide();
									}

									@Override
									public void onAccept(Finance fin) {
										dialog.hide();
										bankAccount.setValue(fin.getBankAccountSafeValue());
										finance.setBankAccount(fin.getBankAccount());
										finance.setPayMethod(fin.getPayMethod());
										finance.setPayMethodName(fin.getPayMethodName());
										finance.setPayMethodType(fin.getPayMethodType());
										if (fin.getPayMethod() == null) {
											payMethod.setSelectedIndex(0);
										} else {
											if ( payMethod.getItemCount() > 1) {
												for (int i = 1; i < payMethod.getItemCount(); i++) {
													if ( AonNumberUtils.equals(fin.getPayMethod(), AonNumberUtils.toInteger( payMethod.getValue(i)) )) {
														payMethod.setSelectedIndex(i);
													}
												}
											} else {
												payMethod.setSelectedIndex(0);
											}
										}
										somethingChanged(callback, dirty, finance );
									}
								});
						dialog.setWidget(bankPanel);
						dialog.center();
						dialog.show();
					}
				});
				bankAccountPanel.add(editBank);
			}
			add(getCell(bankAccountPanel));
			
			AonDoubleBox amount = new AonDoubleBox(12,4);
			if (firstAmount == null) {
				firstAmount = amount;
			}
			amount.setValue(finance.getAmount());
			amount.setEnabled(finance.isFullPending());
			if (finance.isFullPending()) {
				amount.addKeyUpHandler( new KeyUpHandler() {
					public void onKeyUp(KeyUpEvent event) {
						if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
							double total = callback.getInvoice().getTotalInvoice();
							for (int i = 0; i < callback.getInvoice().getInvoice().getFinances().size() ; i++) {
								Finance f = callback.getInvoice().getInvoice().getFinances().get(i);
								// TODO [ERROR]
								total = AonMathUtils.round(total - f.getAmount());
							}
							if ((total > 0 && callback.getInvoice().getTotalInvoice() > 0) 
							  || (total < 0 && callback.getInvoice().getTotalInvoice() < 0)) {
								amount.setValue(total,true,true);
							} else {
								AonMessageDialog.error("No se puede cuadrar la suma de importes de los "
									+"vencimientos con el total factura porque para hacerlo, el "
									+"importe del vencimiento resultante ser\u00EDa negativo o cero");
							}
						}
					}
				});
				amount.addValueChangeHandler( new ValueChangeHandler<Double>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<Double> event) {
						finance.setAmount( amount.getValue() );
						checkAmounts(callback);
						somethingChanged(callback, dirty, finance );
					}
				});
			}
			add(getCell(amount));

			Label status = new Label();
			status.setText(finance.getFinanceStatus() == null?"":finance.getFinanceStatus().getDescription());
			
			finance.getFinanceStatus().visit( new IFinanceStatusVisitor() {
				@Override
				public void visitSettled() {
					status.setStyleName(AON.CSS.aonColorBlue());
				}
				
				@Override
				public void visitReturned() {
					status.setStyleName(AON.CSS.aonColorRed());
					status.addStyleName(AON.CSS.aonBold());
				}
				
				@Override
				public void visitPending() {
					status.setStyleName(AON.CSS.aonColorRed());
				}
				
				@Override
				public void visitPaid() {
					status.setStyleName(AON.CSS.aonColorGreen());
				}
				
				@Override
				public void visitBatched() {
					status.setStyleName(AON.CSS.aonColorGreen());
				}
			});
			add(getCell(status));
			
			// ------------------ INSERT HERE
			FinanceActionsPanel actionsPanel0 = null;
			if (finance.getId() != null) {
				actionsPanel0 = new FinanceActionsPanel(finance, new FinanceModuleCallback() {
					@Override
					public void updateAndRefresh(Finance finance) {
						InvoiceFinancePanel.this.updateAndRefresh(callback,finance);
					}
					
					@Override
					public ModuleOptions<?> getOptions() {
						return callback.getModuleOptions();
					}
					
					@Override
					public FinanceServiceAsync getFinanceService() {
						return FINANCE_SERVICE;
					}
					
					@Override
					public void addExtraInfo(Widget widget) {
						callback.getModule().addExtraInfo(widget);					
					}

					@Override
					public void refresh() {
						InvoiceFinancePanel.this.updateAndRefresh(callback,finance);
					}
				});
			}			
			final FinanceActionsPanel actionsPanel = actionsPanel0;
			
			AonTableButton restoreButton = new AonTableButton( AON.MSG.restoreAction(), AON.CSS.aonIconRestoreDeleted() );
			AonTableButton removeButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete() );
			if (finance.isPending() && amount != firstAmount) {
				
				// *************************************************************************
				// *******															 *******
				// ******* 					RESTORE BUTTON							 *******
				// *******															 *******
				// *************************************************************************
				FlowPanel buttonsPanel = new FlowPanel();
				buttonsPanel.setStyleName(AON.CSS.aonDisplayTableCell());
				buttonsPanel.addStyleName(AON.CSS.aonNowrap());
				buttonsPanel.add(restoreButton);
				buttonsPanel.add(removeButton);
				add(buttonsPanel);
				
				restoreButton.setVisible(finance.isRemoved());
				removeButton.setVisible(!finance.isRemoved());
				restoreButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						
						restoreButton.setVisible(false);
						removeButton.setVisible(true);		
						finance.setRemoved(false);
						dueDate.removeStyleName(AON.CSS.aonTextLineThrough());
						dueDate.setEnabled(true);
						payMethod.removeStyleName(AON.CSS.aonTextLineThrough());
						payMethod.setEnabled(true);
						bankAccount.removeStyleName(AON.CSS.aonTextLineThrough());
						amount.removeStyleName(AON.CSS.aonTextLineThrough());
						amount.setEnabled(true);
						status.removeStyleName(AON.CSS.aonTextLineThrough());
						if ( actionsPanel != null) {
							actionsPanel.setVisible(!finance.isPending() && finance.getId() != null);
						}
						checkAmounts(callback);
					}
				});

				// *************************************************************************
				// *******															 *******
				// ******* 					DELETE BUTTON							 *******
				// *******															 *******
				// *************************************************************************
				removeButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						restoreButton.setVisible(true);
						removeButton.setVisible(false);		
						finance.setRemoved(true);
						dueDate.addStyleName(AON.CSS.aonTextLineThrough());
						dueDate.setEnabled(false);
						payMethod.addStyleName(AON.CSS.aonTextLineThrough());
						payMethod.setEnabled(false);
						bankAccount.addStyleName(AON.CSS.aonTextLineThrough());
						editBank.setEnabled(false);
						amount.addStyleName(AON.CSS.aonTextLineThrough());
						amount.setEnabled(false);
						status.addStyleName(AON.CSS.aonTextLineThrough());
						if ( actionsPanel != null) {
							actionsPanel.setVisible(false);
						}
						checkAmounts(callback);
					}
				});
			} else {
				add(getCell(new Label()));
			}

			if (finance.getId() != null) {
				add(getCell(actionsPanel));
			} else {
				add(getCell(new Label()));
			}

			
			Label l = new Label("");
			FlowPanel cell = new FlowPanel();
			cell.setStyleName(AON.CSS.aonDisplayTableCell());
			cell.addStyleName(AON.CSS.aonWidthAll());
			cell.add(l);
			add(cell);
			// ------------------ SO FAR
			
			if ( finance.isRemoved()) {
				restoreButton.setVisible(true);
				removeButton.setVisible(false);		
				finance.setRemoved(true);
				dueDate.addStyleName(AON.CSS.aonTextLineThrough());
				dueDate.setEnabled(false);
				payMethod.addStyleName(AON.CSS.aonTextLineThrough());
				payMethod.setEnabled(false);
				bankAccount.addStyleName(AON.CSS.aonTextLineThrough());
				editBank.setEnabled(false);
				amount.addStyleName(AON.CSS.aonTextLineThrough());
				amount.setEnabled(false);
				status.addStyleName(AON.CSS.aonTextLineThrough());
				if ( actionsPanel != null) {
					actionsPanel.setVisible(false);
				}
				checkAmounts(callback);
			}
			
			if (focus) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						dueDate.setFocus(true);
					}
				});
			}
		}
		
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Finance> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
	
	public void invoiceDateIssueChanged(IInvoicePanelCallback callback) {
		if (callback.getInvoice().getInvoice().getId() == null) {
			generateFinances( callback);
		}
	}

	public void invoiceTotalChanged(IInvoicePanelCallback callback) {
		if (callback.getInvoice().getInvoice().getId() == null) {
			if (callback.getInvoice().isAuthFinanceCalculation()) {
				generateFinances( callback);
			}
		} else {
			if (callback.getInvoice().getInvoice().hasFinances()
				&& callback.getInvoice().getInvoice().getFinances().size() == 1
				&& callback.getInvoice().getInvoice().getFinances().get(0).isFullPending()) {
					firstAmount.setValue(callback.getInvoice().getTotalInvoice(),true,true);
			}
		}
		checkAmounts(callback);
	}

	private void generateFinances(IInvoicePanelCallback callback) {
		FINANCE_SERVICE.getFinancesForInvoice(callback.getCurrentDomainName()
				,callback.getCurrentDomainId()
				,callback.getCurrentUser()
				,callback.getInvoice().getInvoice(), new AsyncCallback<LinkedList<Finance>>() {
					
					@Override
					public void onSuccess(LinkedList<Finance> result) {
						callback.getInvoice().getInvoice().setFinances(result);							
						paint(callback);
						callback.paintEntry();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Label label = new Label("Se ha producido un error al calcular los vencmientos. ["+caught.getMessage()+"]"); 
						callback.getModule().addExtraInfo(label);
					}
				});			
	}

}
