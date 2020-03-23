package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AuditDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.FinancePayPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.FinancePayPanel.FinancePayPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.InvoicePanel.InvoicePanelCallback;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

public class InvoiceFinancePanel extends ScrollPanel implements HasValueChangeHandlers<Finance> {
	
	private static FinanceServiceAsync FINANCE_SERVICE;
	
	private FlowPanel container;
	private FlexTable tab;
	private LinkedList<InvoiceFinancePanelRow> rows;
	
	private DoubleBox firstAmount = new DoubleBox(12,4);
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
	
	private void updateAndRefresh(IInvoicePanelCallback callback, Finance fin) {
		for (int i = 0; i < callback.getInvoice().getInvoice().getFinances().size(); i++) {
			Finance f = callback.getInvoice().getInvoice().getFinances().get(i);
			if (AonNumberUtils.equals(f.getId() , fin.getId())) {
				callback.getInvoice().getInvoice().getFinances().set(i, fin);		
			}
		}
		paint(callback); 
	}

	void paint(IInvoicePanelCallback callback){
		container.clear();
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
					fin.setPayMethod(last.getPayMethod())
						.setDueDate(last.getDueDate())
						.setPayMethodName(last.getPayMethodName())
						.setBankAccount(last.getBankAccount())
						.setBic(last.getBic())
						.setBankAlias(last.getBankAlias())
					;
				} 
				fin.setAmount(AonMathUtils.round( amount ));
				callback.getInvoice().getInvoice().addFinance( fin );
				addRow(callback, fin, true);
				checkAmounts( callback );
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
						decorateDirty(dirty, finance );
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
						finance.setPayMethod( AonNumberUtils.toInteger( payMethod.getSelectedValue() ));
						finance.setPayMethodName( payMethod.getSelectedItemText());
						decorateDirty(dirty, finance );
					}
				});
			}
			tab.setWidget(currentRow, col, payMethod);
			++col;

			bankAccount.setStyleName(AON.AON_CSS.aonInputText());
			bankAccount.setVisibleLength(25);
			bankAccount.setMaxLength(34);
			bankAccount.setValue(finance.getBankAccountSafeValue());
			bankAccount.setEnabled(finance.isFullPending());
			if (finance.isFullPending()) {
				bankAccount.addValueChangeHandler( new ValueChangeHandler<String>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						BankAccount ba = new BankAccount( bankAccount.getValue() ); 
						finance.setBankAccount( ba );
						decorateDirty(dirty, finance );
					}
					
				});
			}
			tab.setWidget(currentRow, col, bankAccount);
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
						decorateDirty(dirty, finance );
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
			if (finance.isFullPending()) {
				status.setStyleName(AON.AON_CSS.aonColorRed());
			} else {
				status.setStyleName(AON.AON_CSS.aonColorGreen());
			}
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
						bankAccount.setEnabled(true);
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
						bankAccount.setEnabled(false);
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
				dialog.setCaption(AON.MSG.payFinance());
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
									public void onAccept(Finance finance) {
										dialog.hide();
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
			boolean canUndo = finance.isSettled() || finance.isPaid() || finance.isReturned(); 
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

			// *************************************************************************
			// *******															 *******
			// *******				RETURN BUTTON		 				 			 *******
			// *******															 *******
			// *************************************************************************
			if (finance.isPaid() && finance.getId() != null) {
				actionsPanel.add(returnButton);
				returnButton.setText( AON.MSG.toReturn() );
				returnButton.setStyleName(AON.AON_CSS.aonActionButton());
				returnButton.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						MessageDialog.error("Opci\u00F3n no implementada");
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
	
	public void invoiceDateIssueChanged(InvoicePanelCallback callback) {
		if (callback.getInvoice().getInvoice().getId() == null) {
			generateFinances( callback);
		}
	}

	public void invoiceTotalChanged(InvoicePanelCallback callback) {
		if (callback.getInvoice().getInvoice().getId() == null) {
			generateFinances( callback);
		} else {
			if (callback.getInvoice().getInvoice().hasFinances()
				&& callback.getInvoice().getInvoice().getFinances().size() == 1
				&& callback.getInvoice().getInvoice().getFinances().get(0).isFullPending()) {
					firstAmount.setValue(callback.getInvoice().getTotalInvoice(),true,true);
			}
			checkAmounts(callback);
		}
	}

	private void generateFinances(InvoicePanelCallback callback) {
		FINANCE_SERVICE.getFinancesForInvoice(callback.getCurrentDomainName()
				,callback.getCurrentDomainId()
				,callback.getCurrentUser()
				,callback.getInvoice().getInvoice(), new AsyncCallback<LinkedList<Finance>>() {
					
					@Override
					public void onSuccess(LinkedList<Finance> result) {
						callback.getInvoice().getInvoice().setFinances(result);							
						paint(callback);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Label label = new Label("Se ha producido un error al calcular los vencmientos. ["+caught.getMessage()+"]"); 
						callback.getModule().addExtraInfo(label);
					}
				});			
	}


	public static class InvoiceFinanceTrackingPanel extends ScrollPanel {
		
		public InvoiceFinanceTrackingPanel(LinkedList<FinanceTracking> list) {
			FlexTable tab = new FlexTable();
			tab.setStyleName(AON.AON_CSS.aonDataTable());
			tab.addStyleName(AON.AON_CSS.aonBlockCenter());
			tab.addStyleName(AON.AON_CSS.aonWidthAutoImportant());
			int row = 0;
			int col = 0;
			
			tab.setWidget(row,col, new Label(AON.MSG.date()));
			tab.getCellFormatter().addStyleName(row, col,AON.AON_CSS.aonDataTableHeader());
			tab.getColumnFormatter().setWidth(col, "100px");
			++col;
			tab.setWidget(row,col, new Label(AON.MSG.action()));
			tab.getCellFormatter().addStyleName(row, col,AON.AON_CSS.aonDataTableHeader());
			tab.getColumnFormatter().setWidth(col, "200px");
			++col;
			tab.setWidget(row,col, new Label(AON.MSG.description()));
			tab.getCellFormatter().addStyleName(row, col,AON.AON_CSS.aonDataTableHeader());
			tab.getColumnFormatter().setWidth(col, "300px");
			++col;
			tab.setWidget(row,col, new Label(AON.MSG.amount()));
			tab.getCellFormatter().setStyleName(row, col,AON.AON_CSS.aonTextRight());
			tab.getCellFormatter().addStyleName(row, col,AON.AON_CSS.aonDataTableHeader());
			tab.getColumnFormatter().setWidth(col, "100px");
			++col;
			tab.setWidget(row,col, new Label());
			tab.getFlexCellFormatter().addStyleName(row, col,AON.AON_CSS.aonDataTableHeader());
			tab.getColumnFormatter().setWidth(col, "40px");
			++col;
			++row;
			
			for (FinanceTracking ft : list) {
				col = 0;
				tab.setWidget(row,col, new Label( AON.DATE_FORMAT.format(ft.getTrackingDate())));
				++col;
				Label typeLabel = new Label( ft.getType().getDescription() );
				if (ft.getType() == FinanceTrackingType.RETURNED) {
					typeLabel.setStyleName(AON.AON_CSS.aonColorRed());
				} else  if (ft.getType() == FinanceTrackingType.FRACTIONED) {
					typeLabel.setStyleName(AON.AON_CSS.aonColoRoyalblue());
				} else {
					typeLabel.setStyleName(AON.AON_CSS.aonColorGreen());
				}
				tab.setWidget(row,col, typeLabel);
				++col;
				tab.setWidget(row,col, new Label( ft.getDescription()));
				++col;
				tab.setWidget(row,col, new Label(AON.FMT.format( ft.getAmount()) ));
				tab.getCellFormatter().setStyleName(row, col,AON.AON_CSS.aonTextRight());
				++col;
				FlowPanel buttons = new FlowPanel();
				
				Button auditInfo = new Button();
				buttons.add(auditInfo);
				auditInfo.setTitle( AON.MSG.tracking() );
				auditInfo.setStyleName(AON.AON_CSS.aonIconAudit());
				auditInfo.addStyleName(AON.AON_CSS.aonIconCommandButton());
				auditInfo.addStyleName(AON.AON_CSS.aonMarginLeft());
				auditInfo.addStyleName(AON.AON_CSS.aonMarginLeft5());
				auditInfo.addClickHandler( new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						AuditDialog dialog = new AuditDialog();
						dialog.show( ft );
					}
				});
				tab.setWidget(row,col, buttons);
				++col;
				++row;
			}
			setWidget(tab);
		}
	}
}
