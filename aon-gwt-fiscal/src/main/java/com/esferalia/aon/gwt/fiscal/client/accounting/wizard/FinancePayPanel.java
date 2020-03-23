package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.ErrorPanel;
import com.esferalia.aon.gwt.fiscal.client.widget.BankAccountBox;
import com.esferalia.aon.gwt.fiscal.client.widget.BankAccountBox.BankAccountBoxOptions;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

public class FinancePayPanel extends SimplePanel implements Focusable {
	
	public static interface FinancePayPanelCallback {
		void onAccept(Finance finance);
		void onCancel();
	}

	private final DateBoxEx dueDate = new DateBoxEx();
	
	public void show(final String domainName
			, final int domain
			, final String user
			, final AonConfiguration config
			, final Finance finance
			, final FinancePayPanelCallback callback) {
		setWidth("700px");
		setHeight("200px");
		
		final ListBox payMethodBox = new ListBox();
		final BankAccountBox bankAccountBox = new BankAccountBox( new BankAccountBoxOptions()
				.setBankAccount(finance.getBankAccount()));
		final DoubleBox amount = new DoubleBox();
		
		
		FlowPanel rootPanel = new FlowPanel();
		final ErrorPanel errorPanel = new ErrorPanel();
		rootPanel.add(errorPanel);
		
		FlowPanel tablePanel = new FlowPanel();
		tablePanel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		KeyUpHandler keyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					callback.onCancel();	
				}
			}
		};

		
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonPanelGrid());
		table.addStyleName(AON.AON_CSS.aonWidthAll());
		int row = 0;
		
		table.setWidget(row,0,new InlineLabel(AON.MSG.payDate()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		dueDate.setValue(finance.getDueDate());
		dueDate.getTextBox().addKeyUpHandler( keyUpHandler);
		dueDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				finance.setDueDate(event.getValue());
			}
		});
		table.setWidget(row,1,dueDate);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;

		table.setWidget(row,0,new InlineLabel(AON.MSG.payMethod()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		payMethodBox.addKeyUpHandler(keyUpHandler);
		payMethodBox.addItem(" ---- ", "");
		int i = 1;
		for (PayMethod pm : config.getPayMethods()) {
			payMethodBox.addItem(pm.getName(), AonNumberUtils.toString( pm.getId()));
			if ( AonNumberUtils.equals(pm.getId(), finance.getPayMethod()) ) {
				payMethodBox.setSelectedIndex(i);
			}
			i++;
		}
		payMethodBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int idx = payMethodBox.getSelectedIndex();
				idx = idx - 1;
				if (idx < 0 ) {
					finance.setPayMethod(null);
					finance.setPayMethodName(null);
					finance.setPayMethodType(null);
				} else {
					PayMethod pm = config.getPayMethods().get(idx);
					finance.setPayMethod(pm.getId());
					finance.setPayMethodName(pm.getName());
					finance.setPayMethodType(pm.getType());
				}
			}
		});
		table.setWidget(row,1,payMethodBox);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;

		table.setWidget(row,0,new InlineLabel(AON.MSG.bankAccount()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
//		companyBanksBox.addValueChangeHandler( new ValueChangeHandler<String>() {
//			
//			@Override
//			public void onValueChange(ValueChangeEvent<String> event) {
//				String bank = event.getValue();
//				BankAccount bankAccount = (AonStringUtils.isBlank(bank))? null : new BankAccount(bank);
//				finance.setBankAccount(bankAccount);
//			}
//		});
//		bankAccountBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
//			
//			@Override
//			public void onSelection(SelectionEvent<Suggestion> event) {
//				IbanSuggestion suggestion = (IbanSuggestion) event.getSelectedItem();
//				IIbanContainer cont = suggestion.getIbanContainer();
//				bankAccountBox.setValue(cont.getIBan());
//				BankAccount bankAccount = (AonStringUtils.isBlank(cont.getIBan()))? null : new BankAccount(cont.getIBan());
//				finance.setBankAccount(bankAccount);
//			}
//		});
		
		table.setWidget(row,1,bankAccountBox);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;

		table.setWidget(row,0,new InlineLabel(AON.MSG.amount()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		amount.setValue(finance.getAmount());
		amount.setStyleName(AON.AON_CSS.aonInputText());
		amount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				finance.setAmount(amount.getValue());
			}
		});
		table.setWidget(row,1,amount);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;

		tablePanel.add( table );
		rootPanel.add( tablePanel );
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.AON_CSS.aonTextCenter());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.AON_CSS.aonConfirmDialogOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler( keyUpHandler);
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				callback.onAccept(finance);
			}
		});
    	
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.AON_CSS.aonConfirmDialogCancelButton());
    	cancelButton.addStyleName(AON.AON_CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addKeyUpHandler( keyUpHandler);
    	cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				callback.onCancel();
			}
		});
    	buttons.add(cancelButton);
    	rootPanel.add(buttons);
		setWidget(rootPanel);
		dueDate.setFocus(true);
	}

	@Override
	public int getTabIndex() {
		return dueDate.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		dueDate.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		dueDate.getTextBox().selectAll();
		dueDate.setFocus(focused);
		dueDate.hideDatePicker();
	}

	@Override
	public void setTabIndex(int index) {
		dueDate.setTabIndex(index);
	}
}


