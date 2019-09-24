package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class TicketDetailPanel extends ScrollPanel implements HasValueChangeHandlers<InvoiceVAT>, HasSelectionHandlers<Account>, Focusable {
	
	private FlowPanel container;
	private FlexTable tab;
	private IInvoicePanelCallback callback;
	private LinkedList<InvoicePanelRow> rows;
	private LinkedList<Account> suggestedAccounts;
	
	private Button addButton;
	private Button saveButton;
	private int tabindex = InvoicePanel.VAT_PANEL_TAB_OFFSET;
	
	public TicketDetailPanel(IInvoicePanelCallback callback) {
		setStyleName(AON.AON_CSS.aonWidthAll());
		setCallback(callback);
		container = new FlowPanel();
		add(container);
	}
	
	public void setCallback(IInvoicePanelCallback invoiceCallback) {
		this.callback = invoiceCallback;
	}

	public void setSuggestedAccounts(LinkedList<Account> suggestedAccounts) {
		this.suggestedAccounts = suggestedAccounts;
	}

	void paint() {
		container.clear();
		tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonWidthAll());
		tab.addStyleName(AON.AON_CSS.aonMarginTop());
		container.add(tab);
		paintHeader();
		paintRows();
		paintButtons();
	}

	private void paintHeader() {
		int row = 0;
		int col = 0;
		
		Label label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, label);
		decorateHeader(row, col, "1%");
		++col;
		label = new Label(AON.MSG.amount());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, label);
		decorateHeader(row, col, "1%");
		++col;
		label = new Label("X");
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
		tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
		tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderTop());
		tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
		tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
		tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontSmall());
	}

	private void paintButtons() {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonBorderTop());
		panel.addStyleName(AON.AON_CSS.aonPadding2Top());
		
		addButton = new Button();
		addButton.setTabIndex(InvoicePanel.VAT_PANEL_TAB_OFFSET + 50000);
		addButton.setAccessKey( 'L' );
		addButton.setStyleName(AON.AON_CSS.aonIconReset());
		addButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		addButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int idx = callback.getInvoice().getVats().size() - 1;
				InvoiceVAT last = callback.getInvoice().getVats().get( idx );
				InvoiceVAT vat = new InvoiceVAT()
					.setExpAccountId(last.getExpAccountId())
					.setExpAccountCode(last.getExpAccountCode())
					.setExpAccountDescription(last.getExpAccountDescription())
					.setPercentage(last.getPercentage())
					.setSurcharge(last.getSurcharge())
					.setWithholding(last.isWithholding())
					.setInputAccountId(last.getInputAccountId())
					.setInputAccountCode(last.getInputAccountCode())
					.setInputAccountDescription(last.getInputAccountDescription())
					.setOutputAccountId(last.getOutputAccountId())
					.setOutputAccountCode(last.getOutputAccountCode())
					.setOutputAccountDescription(last.getOutputAccountDescription())
					.setAdjAccountId(last.getAdjAccountId())
					.setAdjAccountCode(last.getAdjAccountCode())
					.setAdjAccountDescription(last.getAdjAccountDescription());
				callback.getInvoice().addVat( vat );
				addRow(vat, true);
			}
		});
		panel.add(addButton);
		
		saveButton = new Button();
		saveButton.setTabIndex(InvoicePanel.VAT_PANEL_TAB_OFFSET + 50001);
		saveButton.setTitle( AON.MSG.saveAction() );
		saveButton.setStyleName(AON.AON_CSS.aonIconSave());
		saveButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		saveButton.addStyleName(AON.AON_CSS.aonMarginLeft());
		saveButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				callback.getModule().onAccept(event);
			}
		});
		saveButton.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				if (callback.getInvoice().isWithholding() ) {
					callback.setFocusOnWithholding();
				} else {
					callback.setFocusOnPayDate();
				}
			}
		});

		panel.add(saveButton);
		
		container.add(panel);
	}
	
	private void paintRows() {
		rows = new LinkedList<InvoicePanelRow>();
		if (callback.getInvoice().getVats() != null && callback.getInvoice().getVats().size() > 0) {
			for (InvoiceVAT vat : callback.getInvoice().getVats()) {
				addRow(vat, false);
			}
		} 
	}

	private void addRow(InvoiceVAT vat, boolean focus) {
		if (suggestedAccounts != null && !suggestedAccounts.isEmpty() && suggestedAccounts.size() > rows.size()) {
			Account a = suggestedAccounts.get(rows.size());
			vat.setExpAccountId(a.getId());
			vat.setExpAccountCode(a.getCode());
			vat.setExpAccountDescription(a.getDescription());
		}
		InvoicePanelRow invoiceRow = new InvoicePanelRow(vat, tab, focus);
		rows.add(invoiceRow);
		if (rows.size() > 1) {
			callback.enableInvoiceTotal( false );
		}
	}


	private class InvoicePanelRow implements Focusable {
		private final AccountBox expAccount = new AccountBox(callback.getCurrentDomainName(), callback.getCurrentDomainId(), false);
		private final DoubleBox taxableBase = new DoubleBox(12,4);
		private final DoubleBox vatPercent = new DoubleBox(6);
		private final DoubleBox vatQuota = new DoubleBox(8);
		private final Button removeButton = new Button();
		
		private InvoicePanelRow(final InvoiceVAT vat, FlexTable tab, boolean focus) {
			int currentRow = tab.getRowCount();
			int col = 0;
			expAccount.setTabIndex(++tabindex);
			expAccount.setValue(vat.getExpAccountId(),vat.getExpAccountCode(),vat.getExpAccountDescription(),
					(callback.getInvoice().getRegistry() != null && callback.getInvoice().getRegistry().getId() != null));
			expAccount.addSelectionHandler( new SelectionHandler<Account>() {
				@Override
				public void onSelection(SelectionEvent<Account> event) {
					Account a = event.getSelectedItem();
					if (a != null) {
						vat.setExpAccountId(a.getId());
						vat.setExpAccountCode(a.getCode());
						vat.setExpAccountDescription(a.getDescription());
					} else {
						vat.setExpAccountId(null);
						vat.setExpAccountCode(null);
						vat.setExpAccountDescription(null);
					}
					SelectionEvent.<Account>fire(TicketDetailPanel.this, a);
				}
			});
			tab.setWidget(currentRow, col, expAccount);
			++col;
			
			taxableBase.setTabIndex(++tabindex);
			taxableBase.setStyleName(AON.AON_CSS.aonInputText());
			taxableBase.addStyleName(AON.AON_CSS.aonTextRight());
			taxableBase.setValue(vat.getBase());
			taxableBase.addKeyUpHandler( new KeyUpHandler() {
				public void onKeyUp(KeyUpEvent event) {
					if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
						InvoiceCalculator.reverseCalculate(callback.getInvoice(),vat, taxableBase.getValue());
						populate(vat);
						ValueChangeEvent.fire(TicketDetailPanel.this, vat );
					}
				}
			});

			taxableBase.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					vat.setBase( event.getValue() );
					checkCalculate(vat, vatPercent);
				}
			});
			tab.setWidget(currentRow, col, taxableBase);
			++col;

			removeButton.setTabIndex(Integer.MAX_VALUE);
			removeButton.setTitle( AON.MSG.deleteAction() );
			removeButton.setStyleName(AON.AON_CSS.aonIconDelete());
			removeButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
			removeButton.addStyleName(AON.AON_CSS.aonMarginLeft());
			removeButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
			removeButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (callback.getInvoice().getVats().size() > 1 ) {
						ConfirmDialog cd = new ConfirmDialog();
						cd.confirm(AON.MSG.confirmDeleteAction(), AON.MSG.deleteAction(),new ConfirmDialogCallback() {
							
							@Override
							public void onCancel() {
							}
							
							@Override
							public void onAccept() {
								tab.removeRow(currentRow);
								rows.remove(currentRow-1);
								callback.getInvoice().getVats().remove(currentRow-1);
								paint();
								ValueChangeEvent.fire(TicketDetailPanel.this, vat );
							}
						});
					} else {
						callback.getModule().onError("Al menos debe haber una l\u00EDnea.");
					}
					
				}
			});
			tab.setWidget(currentRow, col,removeButton);
			++col;

			tab.setWidget(currentRow, col, new Label());
			tab.getCellFormatter().setWidth(currentRow, col, "auto");
			++col;
			
			if (focus) {
				expAccount.setFocus(true);
			}
		}

		private void decorateVatQuota(InvoiceVAT vat) {
			double quota = InvoiceCalculator.getQuota(vat);
			double gap = InvoiceCalculator.getQuotaGap(vat, vatQuota.getValue());
			decorateEditableQuota(gap, quota, vatQuota);
		}
		private void decorateEditableQuota(double gap, double quota, DoubleBox editableBox) {
			editableBox.removeStyleName(AON.AON_CSS.aonChanged());
			editableBox.removeStyleName(AON.AON_CSS.aonInputError());
			if (AonMathUtils.isNotZero( gap )) {
				if (gap > 1) {
					editableBox.addStyleName(AON.AON_CSS.aonInputError());
					editableBox.setTitle( AON.MSG.editedValueWarning(quota, gap) );
				} else {
					editableBox.addStyleName(AON.AON_CSS.aonChanged());
					editableBox.setTitle( AON.MSG.editedValue(quota) );
				}
			} else {
				editableBox.setTitle(null);
			}
		}

		private void checkCalculate(final InvoiceVAT vat, DoubleBox toFocus) {
			if (vat.isAnyquotaEdited()) {
				ConfirmDialog cd = new ConfirmDialog();
				cd.confirm(AON.MSG.manualChangeConfirm(),new ConfirmDialogCallback() {
					
					@Override
					public void onCancel() {
						if (toFocus != null) {
							toFocus.selectAll();
							toFocus.setFocus(true);
						}
						ValueChangeEvent.fire(TicketDetailPanel.this, vat );
					}
					
					@Override
					public void onAccept() {
						vat.setQuotaEdited(false);
						vat.setSurchargeQuotaEdited(false);
						vat.setDeductibleQuotaEdited(false);
						calculate(vat);
						decorateVatQuota(vat);
						if (toFocus != null) {
							toFocus.selectAll();
							toFocus.setFocus(true);
						}
						ValueChangeEvent.fire(TicketDetailPanel.this, vat );
					}
				});
			} else {
				calculate(vat);
				ValueChangeEvent.fire(TicketDetailPanel.this, vat );
			}
		}
		private void calculate(InvoiceVAT vat) {
			InvoiceCalculator.calculate(callback.getInvoice(),vat);
			populate(vat);
		}

		private void populate(InvoiceVAT vat) {
			taxableBase.setValue( vat.getBase() , false);
			vatQuota.setValue( vat.getQuota() , false);
		}

		@Override
		public int getTabIndex() {
			return expAccount.getTabIndex();
		}

		@Override
		public void setAccessKey(char key) {
			expAccount.setAccessKey(key);
			
		}

		@Override
		public void setFocus(boolean focused) {
			expAccount.setFocus(focused);
		}

		@Override
		public void setTabIndex(int index) {
			expAccount.setTabIndex(index);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<InvoiceVAT> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	public void populateFirstVat() {
		if (rows != null &&  rows.size() == 1) {
			rows.get(0).populate( callback.getInvoice().getFirstVat() );
		}
	}
	
	public void extraInfoChanged() {
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Account> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public void enableElements(boolean canRemove, boolean canEdit) {
		if (addButton != null) addButton.setVisible(canEdit);
		if (saveButton != null) saveButton.setVisible(canEdit);
	}

	@Override
	public int getTabIndex() {
		if (rows != null &&  rows.size() > 0) return rows.get(0).getTabIndex();
		return 0;
	}

	@Override
	public void setAccessKey(char key) {
		if (rows != null &&  rows.size() > 0) rows.get(0).setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		if (rows != null &&  rows.size() > 0) rows.get(0).setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		if (rows != null &&  rows.size() > 0) rows.get(0).setTabIndex(index);
	}

	public void hideButtons(boolean enabled) {
		addButton.setVisible(enabled);
		saveButton.setVisible(enabled);
	}

}
