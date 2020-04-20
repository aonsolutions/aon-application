package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.FinanceBankPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.FinanceBankPanel.FinanceBankPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.FinancePayPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.FinancePayPanel.FinancePayPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.FinanceReturnPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.FinanceReturnPanel.FinanceReturnPanelCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFinanceStatusVisitor;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

public class InvoiceFinancePanel extends ScrollPanel implements HasValueChangeHandlers<Finance>,HasAccountEntrySelectionHandlers {
	
	private static FinanceServiceAsync FINANCE_SERVICE;
	
	private FlowPanel container;
	private FlexTable tab;
	private LinkedList<InvoiceFinancePanelRow> rows;
	
	private DoubleBox firstAmount = new DoubleBox(12,4);
	private CheckBox authFinanceCalculation;
	private AccountBox payAccount;
	private Button addButton;
	private InlineLabel errorLabel;

	public InvoiceFinancePanel(IInvoicePanelCallback callback) {
		setStyleName(AON.AON_CSS.aonWidthAll());
		getElement().getStyle().setBackgroundColor(EditableInvoicePanel.INNER_BACKGROUND_COLOR);
		
		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);

		container = new FlowPanel();
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

	private void paint(IInvoicePanelCallback callback){
		container.clear();
		
		
		if (callback.getInvoice().getAccountEntry().getId() == null) {
			FlexTable payOptionsPanel = new FlexTable();
			authFinanceCalculation = new CheckBox("Calcular vtos. autom\u00E1ticamente");
			authFinanceCalculation.setTabIndex(-1);
			authFinanceCalculation.setStyleName(AON.AON_CSS.aonMarginRight());
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
			payOptionsPanel.setWidget(0, 0, authFinanceCalculation);
			
			InlineLabel accountBoxLabel = new InlineLabel("Contabilizar los pagos contra la cuenta:");
			accountBoxLabel.setStyleName(AON.AON_CSS.aonMarginRight());
			payOptionsPanel.setWidget(0, 1, accountBoxLabel);
			payOptionsPanel.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonPadding2Top()); 

			payAccount = new AccountBox(callback.getCurrentDomainName(),callback.getCurrentDomainId(), callback.getCurrentUser());
			payAccount.setValue(callback.getInvoice().getPayAccountId()
					, callback.getInvoice().getPayAccountCode()
					, callback.getInvoice().getPayAccountDescription(), false);
			payAccount.addSelectionHandler(new SelectionHandler<Account>() {
				@Override
				public void onSelection(SelectionEvent<Account> event) {
					callback.getModule().onBalance(event.getSelectedItem());
				}
			});
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
			payOptionsPanel.setWidget(0, 2, payAccount);
			
			container.add(payOptionsPanel);
		}
		
		
		tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonWidthAll());
		tab.addStyleName(AON.AON_CSS.aonMarginTop());
		container.add(tab);
		paintHeader();
		paintRows(callback);
		
		
		FlowPanel lastLinePanel = new FlowPanel();
		lastLinePanel.setStyleName(AON.AON_CSS.aonPadding2Top());
		addButton = new Button();
		addButton.setAccessKey( 'L' );
		addButton.setStyleName(AON.AON_CSS.aonIconReset());
		addButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		addButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
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
				authFinanceCalculation.setValue(false);
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
		lastLinePanel.add(addButton);
		
		errorLabel = new InlineLabel();
		errorLabel.setWidth("350px");
		errorLabel.setStyleName(AON.AON_CSS.aonBold());
		errorLabel.addStyleName(AON.AON_CSS.aonColorRed());
		errorLabel.addStyleName(AON.AON_CSS.aonTextRight());
		errorLabel.getElement().getStyle().setMarginLeft(100, Unit.PX);
		lastLinePanel.add(errorLabel);
		
		container.add(lastLinePanel);
		
		checkAmounts( callback );
	}

	private void checkAmounts(IInvoicePanelCallback callback) {
		double total = callback.getInvoice().getTotalInvoice();
		if (callback.getInvoice().getInvoice().hasFinances()) {
			double sum = 0;
//			boolean negAmount = false;
			for (Finance f : callback.getInvoice().getInvoice().getFinances() ) {
				if (!f.isRemoved()) {
					sum = AonMathUtils.round( sum + f.getAmount());
//					if (!AonMathUtils.isGreatherThanZero(f.getAmount() )) {
//						negAmount = true;						
//					}
				}
			}
//			if (negAmount) {
//				errorLabel.setText(AON.MSG.invoiceFinancesNegativeAmount());
//				errorLabel.setVisible(true);
//			} else 
			if (!AonMathUtils.equals(sum, total)) {
				errorLabel.setText(AON.MSG.invoiceFinancesAmountNotFit());
				errorLabel.setVisible(true);
			} else {
				errorLabel.setText("");
				errorLabel.setVisible(false);
			}
		} else {
			errorLabel.setText(AON.MSG.invoiceFinancesEmpty());
			errorLabel.setVisible(true);
		}
	}

	private void paintHeader() {
		int row = 0;
		int col = 0;
		
		Label label = new Label();
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, label);
		decorateHeader(row, col, "1%");
		++col;

		label = new Label(AON.MSG.dueDate());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, label);
		decorateHeader(row, col, "1%");
		++col;
		
		label = new Label(AON.MSG.payMethod());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, label);
		decorateHeader(row, col, "1%");
		++col;
		
		label = new Label(AON.MSG.bankAccount());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, label);
		decorateHeader(row, col, "1%");
		++col;
		
		label = new Label(AON.MSG.amount());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, label);
		decorateHeader(row, col, "1%");
		++col;

		label = new Label(AON.MSG.status());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, label);
		decorateHeader(row, col, "1%");
		++col;
		
		label = new Label();
		tab.setWidget(row, col, label);
		decorateHeader(row, col, "1%");
		++col;

		label = new Label();
		tab.setWidget(row, col, label);
		decorateHeader(row, col, "1%");
		++col;

		label = new Label(AON.MSG.actions());
		tab.setWidget(row, col, label);
		decorateHeader(row, col, "1%");
		++col;

		label = new Label();
		tab.setWidget(row, col, label);
		decorateHeader(row, col, "auto");
		++col;
	}
	
	private void decorateHeader(int row, int col, String width) {
		tab.getCellFormatter().setWidth(row, col, width);
		tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextCenter());
		tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontMedium());
		tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
		tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
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
		InvoiceFinancePanelRow invoiceFinanceRow = new InvoiceFinancePanelRow(callback, finance, tab, focus);
		rows.add(invoiceFinanceRow);
	}


	private class InvoiceFinancePanelRow  {
		
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
		
		private InvoiceFinancePanelRow(IInvoicePanelCallback callback, final Finance finance, FlexTable tab, boolean focus) {
			int currentRow = tab.getRowCount();
			int col = 0;
			
			Label dirty = new Label();
			DateBoxEx dueDate = new DateBoxEx();
			ListBox payMethod = new ListBox();
			TextBox bankAccount = new TextBox();
			DoubleBox amount = currentRow==1?firstAmount:new DoubleBox(12,4);
			Label status = new Label();
			
			dirty.setStyleName(AON.AON_CSS.aonColoRoyalblue());
			decorateDirty(dirty, finance );
			tab.setWidget(currentRow, col, dirty);
			++col;
			
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
			tab.setWidget(currentRow, col, dueDate);
			++col;

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
			tab.setWidget(currentRow, col, payMethod);
			++col;

			FlowPanel bankAccountPanel = new FlowPanel();
			bankAccountPanel.setStyleName(AON.AON_CSS.aonNowrap());
			bankAccount.setStyleName(AON.AON_CSS.aonInputText());
			bankAccount.setVisibleLength(25);
			bankAccount.setMaxLength(34);
			bankAccount.setValue(finance.getBankAccountSafeValue());
			bankAccount.setReadOnly(true);
			bankAccountPanel.add(bankAccount);
			if (finance.isFullPending()) {
				Button editBank = new Button();
				editBank.setTitle("Editar banco");
				editBank.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
				editBank.addStyleName(AON.AON_CSS.aonIconEdit());
				editBank.addStyleName(AON.AON_CSS.aonClickable());
				editBank.addStyleName(AON.AON_CSS.aonBorderNoneImportant());
				editBank.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						final CustomDialog dialog = new CustomDialog();
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
			
			tab.setWidget(currentRow, col, bankAccountPanel);
			++col;

			amount.setStyleName(AON.AON_CSS.aonInputText());
			amount.addStyleName(AON.AON_CSS.aonTextRight());
			amount.setValue(finance.getAmount());
			amount.setEnabled(finance.isFullPending());
			if (finance.isFullPending()) {
				amount.addKeyUpHandler( new KeyUpHandler() {
					public void onKeyUp(KeyUpEvent event) {
						if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
							double total = callback.getInvoice().getTotalInvoice();
							for (int i = 0; i < callback.getInvoice().getInvoice().getFinances().size() ; i++) {
								Finance f = callback.getInvoice().getInvoice().getFinances().get(i);
								if (i != (currentRow-1) && !f.isRemoved()) {
									total = AonMathUtils.round(total - f.getAmount());
								}
							}
							if ((total > 0 && callback.getInvoice().getTotalInvoice() > 0) 
							  || (total < 0 && callback.getInvoice().getTotalInvoice() < 0)) {
								amount.setValue(total,true,true);
							} else {
								MessageDialog.error("No se puede cuadrar la suma de importes de los "
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
			tab.setWidget(currentRow, col, amount);
			++col;

			status.setText(finance.getFinanceStatus() == null?"":finance.getFinanceStatus().getDescription());
			tab.setWidget(currentRow, col, status);
			tab.getCellFormatter().setStyleName(currentRow, col, AON.AON_CSS.aonPadding2Left());
			tab.getCellFormatter().addStyleName(currentRow, col, AON.AON_CSS.aonPadding2Right());
			tab.getCellFormatter().addStyleName(currentRow, col, AON.AON_CSS.aonSimpleBorder());
			finance.getFinanceStatus().visit( new IFinanceStatusVisitor() {
				@Override
				public void visitSettled() {
					status.setStyleName(AON.AON_CSS.aonColoRoyalblue());
				}
				
				@Override
				public void visitReturned() {
					status.setStyleName(AON.AON_CSS.aonColorRed());
					status.addStyleName(AON.AON_CSS.aonBold());
				}
				
				@Override
				public void visitPending() {
					status.setStyleName(AON.AON_CSS.aonColorRed());
				}
				
				@Override
				public void visitPaid() {
					status.setStyleName(AON.AON_CSS.aonColorGreen());
				}
				
				@Override
				public void visitBatched() {
					status.setStyleName(AON.AON_CSS.aonColorGreen());
				}
			});
			++col;

			// **************************************************
			// Botón de borrado de un vencimiento en estado nuevo
			// **************************************************
			Button trackingButton = new Button();
			Button payButton = new Button();
			Button settleButton = new Button();
			Button undoButton = new Button();
			Button returnButton = new Button();
			
			if (finance.isPending() && currentRow > 1) {
				
				// *************************************************************************
				// *******															 *******
				// ******* 					RESTORE BUTTON							 *******
				// *******															 *******
				// *************************************************************************
				FlowPanel buttonsPanel = new FlowPanel();
				Button restoreButton = new Button();
				Button removeButton = new Button();
				buttonsPanel.add(restoreButton);
				buttonsPanel.add(removeButton);
				tab.setWidget(currentRow, col, buttonsPanel);
				++col;
				
				restoreButton.setVisible(finance.isRemoved());
				removeButton.setVisible(!finance.isRemoved());

				restoreButton.setTitle( AON.MSG.restoreAction() );
				restoreButton.setStyleName(AON.AON_CSS.aonIconUndo());
				restoreButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
				restoreButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
				restoreButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						restoreButton.setVisible(false);
						removeButton.setVisible(true);		
						finance.setRemoved(false);
						dueDate.removeStyleName(AON.AON_CSS.aonTextLineThrough());
						dueDate.setEnabled(true);
						payMethod.removeStyleName(AON.AON_CSS.aonTextLineThrough());
						payMethod.setEnabled(true);
						bankAccount.removeStyleName(AON.AON_CSS.aonTextLineThrough());
						amount.removeStyleName(AON.AON_CSS.aonTextLineThrough());
						amount.setEnabled(true);
						status.removeStyleName(AON.AON_CSS.aonTextLineThrough());

						trackingButton.setVisible(!finance.isPending() && finance.getId() != null);
						payButton.setVisible(finance.isFullPending() && finance.getId() != null);
						settleButton.setVisible(finance.isFullPending() && finance.getId() != null);
						undoButton.setVisible(finance.isFullPending() && finance.getId() != null);
						returnButton.setVisible(finance.isPaid() && finance.getId() != null);

						checkAmounts(callback);
					}
				});

				// *************************************************************************
				// *******															 *******
				// ******* 					DELETE BUTTON							 *******
				// *******															 *******
				// *************************************************************************
				removeButton.setTitle( AON.MSG.deleteAction() );
				removeButton.setStyleName(AON.AON_CSS.aonIconDelete());
				removeButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
				removeButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
				removeButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						restoreButton.setVisible(true);
						removeButton.setVisible(false);		
						finance.setRemoved(true);
						dueDate.addStyleName(AON.AON_CSS.aonTextLineThrough());
						dueDate.setEnabled(false);
						payMethod.addStyleName(AON.AON_CSS.aonTextLineThrough());
						payMethod.setEnabled(false);
						bankAccount.addStyleName(AON.AON_CSS.aonTextLineThrough());
						amount.addStyleName(AON.AON_CSS.aonTextLineThrough());
						amount.setEnabled(false);
						status.addStyleName(AON.AON_CSS.aonTextLineThrough());
						trackingButton.setVisible(false);
						payButton.setVisible(false);
						settleButton.setVisible(false);
						undoButton.setVisible(false);
						returnButton.setVisible(false);
						checkAmounts(callback);
					}
				});
			} else {
				tab.setWidget(currentRow, col, new Label());
				++col;
			}
		
			// *************************************************************************
			// *******															 *******
			// *******				TRACKING INFO BUTTON		 				 *******
			// *******															 *******
			// *************************************************************************
			if (finance.getId() != null) {
				tab.setWidget(currentRow, col, trackingButton);
				++col;
				
				trackingButton.setTitle( AON.MSG.tracking() );
				trackingButton.setStyleName(AON.AON_CSS.aonIconInfo());
				trackingButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
				trackingButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
				trackingButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						FINANCE_SERVICE.getFinanceTracking(callback.getCurrentDomainName()
								,callback.getCurrentDomainId()
								,callback.getCurrentUser(), finance.getId()
								,new AsyncCallback<LinkedList<FinanceTracking>>() {

									@Override
									public void onFailure(Throwable caught) {
										Label label = new Label("Se ha producido un error al recuperar el historial del vencimiento. ["+caught.getMessage()+"]"); 
										callback.getModule().addExtraInfo(label);
									}

									@Override
									public void onSuccess(LinkedList<FinanceTracking> list) {
										if (list == null || list.size() == 0) {
											Label label = new Label("No existen movimientos registrados del vencimiento.");
											label.setStyleName(AON.AON_CSS.aonInfoMessageBlock());
											callback.getModule().addExtraInfo(label);
										} else {
											InvoiceFinanceTrackingPanel trackingPanel = new InvoiceFinanceTrackingPanel(list);
											trackingPanel.addSelectionHandler(new AccountEntrySelectionHandler() {
												@Override
												public void onSelection(AccountEntrySelectionEvent event) {
													AccountEntrySelectionEvent.fire( InvoiceFinancePanel.this, event.getSelectedItem(), null);
												}
											});
											callback.getModule().addExtraInfo(trackingPanel);
										}
									}
							
						});						
					}
				});
			} else {
				tab.setWidget(currentRow, col, new Label());
				++col;
			}
			// *************************************************************************
			// *******															 *******
			// *******				PAY BUTTON		 				 			 *******
			// *******															 *******
			// *************************************************************************
			FlowPanel actionsPanel = new FlowPanel();
			if (finance.isFullPending() && finance.getId() != null) {
				actionsPanel.add(payButton);
				payButton.setText(AON.MSG.toPay());
				payButton.setStyleName(AON.AON_CSS.aonActionButton());
				final CustomDialog dialog = new CustomDialog();
				String suffix = ( finance.isPayment()?" [PAGO":" [COBRO");
				if (callback.getInvoice().isSales()) suffix += "DE UN CLIENTE]";
				if (callback.getInvoice().isPurchase()) suffix += " A UN PROVEEDOR]";
				if (callback.getInvoice().isExpenses() || callback.getInvoice().isUndeductible()) suffix += " A UN ACREEDOR]";
				dialog.setCaption(AON.MSG.payFinance() + suffix );
				payButton.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						FinancePayPanel payPanel = new FinancePayPanel();
						payPanel.show(callback.getCurrentDomainName(), callback.getCurrentDomainId(),
								callback.getCurrentUser(), callback.getConfiguration(), finance,
								new FinancePayPanelCallback() {

									@Override
									public void onCancel() {
										dialog.hide();
									}

									@Override
									public void onAccept(FinanceTracking tracking) {
										dialog.hide();
										FINANCE_SERVICE.payFinance(callback.getCurrentDomainName()
												,callback.getCurrentDomainId()
												,callback.getCurrentUser()
												, tracking
												,new AsyncCallback<FinanceTracking>() {

													@Override
													public void onFailure(Throwable caught) {
														MessageDialog.error("Se ha producido un error al pagar el vencimiento. ["+caught.getMessage()+"]");
													}

													@Override
													public void onSuccess(FinanceTracking tracking) {
														updateAndRefresh( callback , tracking.getFinance() );
													}
												});
									}
								});
						dialog.setWidget(payPanel);
						dialog.center();
						dialog.show();
					}
				});
			}
			
			// *************************************************************************
			// *******															 *******
			// *******				SETTLE BUTTON		 				 			 *******
			// *******															 *******
			// *************************************************************************
			if (finance.isFullPending() && finance.getId() != null) {
				actionsPanel.add(settleButton);
				settleButton.setText( AON.MSG.toSettle() );
				settleButton.setStyleName(AON.AON_CSS.aonActionButton());
				settleButton.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						ConfirmDialog cd = new ConfirmDialog();
						cd.confirm(AON.MSG.settleFinanceAction(), new ConfirmDialogCallback(){

							@Override
							public void onAccept() {
								FINANCE_SERVICE.settleFinance(callback.getCurrentDomainName()
										,callback.getCurrentDomainId()
										,callback.getCurrentUser()
										, finance.getId()
										,new AsyncCallback<Finance>() {

									@Override
									public void onFailure(Throwable caught) {
										MessageDialog.error("Se ha producido un error al saldar el vencimiento. ["+caught.getMessage()+"]");
									}

									@Override
									public void onSuccess(Finance fin) {
										updateAndRefresh( callback ,fin );
									}
								});						
							}

							@Override
							public void onCancel() {
							}
						});
					}
				});
			}
			
			// *************************************************************************
			// *******															 *******
			// *******				UNSETTLE BUTTON		 				 		 *******
			// *******															 *******
			// *************************************************************************
			boolean canUndo = (finance.isSettled() && finance.getFinanceGroup() == null) || finance.isPaid() || finance.isReturned(); 
			if (canUndo && finance.getId() != null) {
				actionsPanel.add(undoButton);
				undoButton.setText( AON.MSG.undo() );
				undoButton.setStyleName(AON.AON_CSS.aonActionButton());
				undoButton.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						ConfirmDialog cd = new ConfirmDialog();
						cd.confirm(AON.MSG.undoFinanceAction(), new ConfirmDialogCallback(){

							@Override
							public void onAccept() {
								FINANCE_SERVICE.undoFinance(callback.getCurrentDomainName()
										,callback.getCurrentDomainId()
										,callback.getCurrentUser()
										, finance.getId()
										,new AsyncCallback<Finance>() {

									@Override
									public void onFailure(Throwable caught) {
										MessageDialog.error("Se ha producido un error al marcar el vencimiento como pendiente. ["+caught.getMessage()+"]"); 
									}

									@Override
									public void onSuccess(Finance fin) {
										updateAndRefresh( callback ,fin );
									}
								});						
							}

							@Override
							public void onCancel() {
							}
						});
					}
				});
			}
			if (finance.isSettled() && finance.getFinanceGroup() != null) {
				Button groupedButton = new Button();
				groupedButton.setTitle( AON.MSG.financeGrouped() );
				groupedButton.setStyleName(AON.AON_CSS.aonIconRoot());
				groupedButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
				groupedButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
				groupedButton.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						MessageDialog.show("No se puede deshacer.","El vencimiento pertence a una agrupaci\u00F3n de vencimientos");
					}
				});
				actionsPanel.add(groupedButton);
			}

			// *************************************************************************
			// *******															 *******
			// *******				RETURN BUTTON		 				 			 *******
			// *******															 *******
			// *************************************************************************
			if (finance.isPaid() && finance.getId() != null) {
				actionsPanel.add(returnButton);
				returnButton.setText( AON.MSG.toReturn() );
				returnButton.setStyleName(AON.AON_CSS.aonActionButton());
				final CustomDialog dialog = new CustomDialog();
				dialog.setCaption(AON.MSG.returnFinance());
				returnButton.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						FinanceReturnPanel returnPanel = new FinanceReturnPanel();
						returnPanel.show(callback.getCurrentDomainName(), callback.getCurrentDomainId(),
								callback.getCurrentUser(), callback.getConfiguration(), finance,
								new FinanceReturnPanelCallback() {

									@Override
									public void onCancel() {
										dialog.hide();
									}

									@Override
									public void onAccept(FinanceTracking tracking) {
										dialog.hide();
										FINANCE_SERVICE.returnFinance(callback.getCurrentDomainName()
												,callback.getCurrentDomainId()
												,callback.getCurrentUser()
												, tracking
												,new AsyncCallback<FinanceTracking>() {

													@Override
													public void onFailure(Throwable caught) {
														MessageDialog.error("Se ha producido un devolver el pagar del vencimiento. ["+caught.getMessage()+"]");
													}

													@Override
													public void onSuccess(FinanceTracking tracking) {
														updateAndRefresh( callback ,tracking.getFinance() );
													}
												});
									}
								});
						dialog.setWidget(returnPanel);
						dialog.center();
						dialog.show();
					}
				});
			}

			tab.setWidget(currentRow, col, actionsPanel);
			tab.getCellFormatter().setWidth(currentRow, col, "auto");
			tab.getCellFormatter().setStyleName(currentRow, col, AON.AON_CSS.aonNowrap());
			++col;
			
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
