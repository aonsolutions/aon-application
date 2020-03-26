package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.ErrorPanel;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.widget.BankAccountBox;
import com.esferalia.aon.gwt.fiscal.client.widget.BankAccountBox.BankAccountBoxOptions;
import com.esferalia.aon.gwt.fiscal.client.widget.RegistryBankListBox;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

public class FinancePayPanel extends SimplePanel implements Focusable {
	
	public static interface FinancePayPanelCallback {
		void onAccept(Finance finance);
		void onCancel();
	}

	private static FinanceServiceAsync FINANCE_SERVICE;
	private final DateBoxEx dueDate = new DateBoxEx();
	private InlineLabel registryBankIcon = new InlineLabel();
	private Label banksLabel = new Label(AON.MSG.banks());
	private Label bankAccountLabel = new Label(AON.MSG.bankAccount());
	
	public void show(final String domainName
			, final int domain
			, final String user
			, final AonConfiguration config
			, final Finance finance
			, final FinancePayPanelCallback callback) {
		setWidth("700px");
		setHeight("200px");
		
		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);
		
		final ListBox payMethodBox = new ListBox();
		final RegistryBankListBox registryBankListBox = new RegistryBankListBox();
		final BankAccountBox bankAccountBox = new BankAccountBox( new BankAccountBoxOptions()
				.setBankAccount(finance.getBankAccount())
				);
		final DoubleBox amount = new DoubleBox();
		final AccountBox payAccount = new AccountBox(domainName,domain, user);
		
		
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
		table.getColumnFormatter().setWidth(0, "50px");
		table.getColumnFormatter().setWidth(1, "auto");
		
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
					refreshBankAccountPanel(domainName, domain, user,config, finance,registryBankListBox,bankAccountBox,payAccount);
				}
			}
		});
		table.setWidget(row,1,payMethodBox);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;
		
		table.setWidget(row,0,banksLabel);
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		FlowPanel registryBankPanel = new FlowPanel();
		registryBankPanel.add(registryBankListBox);
		registryBankIcon.setStyleName(AON.AON_CSS.aonPaddingLeft20());
		registryBankIcon.addStyleName(AON.AON_CSS.aonMarginLeft());
		registryBankPanel.add(registryBankIcon);
		registryBankListBox.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				RegistryBank rbank = registryBankListBox.getValue();
				if (rbank == null) {
					finance.setBankAccount( null );
					finance.setBankAlias(null);
					finance.setBic(null);
					bankAccountBox.setValue(null);
				} else {
					BankAccount ba = new BankAccount(rbank.getBankAccount() );
					finance.setBankAccount( ba );
					finance.setBankAlias(rbank.getAlias());
					finance.setBic(rbank.getBic());
					bankAccountBox.setValue(ba);
					payAccount.setValue(rbank.getAccount(),rbank.getAccountCode(),rbank.getAccountDescription(),true);
				}
			}
		});
		table.setWidget(row,1,registryBankPanel);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;
		
		
		table.setWidget(row,0,bankAccountLabel);
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		bankAccountBox.addValueChangeHandler( new ValueChangeHandler<BankAccount>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<BankAccount> event) {
				finance.setBankAccount(event.getValue());
			}
		});
		table.setWidget(row,1,bankAccountBox);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;
		
		refreshBankAccountPanel(domainName, domain, user,config,finance,registryBankListBox,bankAccountBox,payAccount);

		table.setWidget(row,0,new InlineLabel(AON.MSG.account()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		payAccount.setValue(finance.getPayAccountId(),finance.getPayAccountCode(),finance.getPayAccountDescription());
		payAccount.addSelectionHandler( new SelectionHandler<Account>() {
			
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				Account account = event.getSelectedItem();
				if (account == null) {
					finance.setPayAccountId(null);
					finance.setPayAccountCode(null);
					finance.setPayAccountDescription(null);
				} else {
					finance.setPayAccountId(account.getId());
					finance.setPayAccountCode(account.getCode());
					finance.setPayAccountDescription(account.getDescription());
				}
				
			}
		});
		table.setWidget(row,1,payAccount);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;

		table.setWidget(row,0,new InlineLabel(AON.MSG.amount()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		amount.setValue(finance.getAmount());
		amount.setStyleName(AON.AON_CSS.aonInputText());
		amount.setEnabled(false); // TODO ENABLE FRACTION
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
				if (finance.getPayAccountId() == null) {
					MessageDialog.error("No se ha indicado la cuenta contable del banco o caja.");
					payAccount.setFocus(true);
				} else if (finance.getPayMethod() == null) { 
					MessageDialog.error("No se ha indicado forma de pago.");
					payMethodBox.setFocus(true);
				} else {
					okButton.setEnabled(false);
					callback.onAccept(finance);
				}
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
	
	private void refreshBankAccountPanel(String domainName, int domain, String user
			,AonConfiguration config
			,Finance finance
			,RegistryBankListBox registryBankListBox
			,BankAccountBox bankAccountBox
			,AccountBox payAccount
			) {
		
		if (finance.getPayMethodType() == PayMethodType.CASH_BASIS) {
			registryBankListBox.setValue(null); 
			registryBankListBox.setVisible(false);
			bankAccountBox.setVisible(false);
			registryBankIcon.setVisible(false);
			banksLabel.setVisible(false);
			bankAccountLabel.setVisible(false);
			
			bankAccountBox.setValue(null);
			Account cashAccount = config.getDefaultCashAccount();
			payAccount.setAccount(cashAccount,true);
		} else {
			registryBankListBox.setVisible(true);
			bankAccountBox.setVisible(true);
			registryBankIcon.setVisible(true);
			banksLabel.setVisible(true);
			bankAccountLabel.setVisible(true);
			
			boolean mustShowRegistryBanks = false;
			if (finance.isPayment() && finance.getPayMethodType() == PayMethodType.BANK_TRANSFER) {
				mustShowRegistryBanks = true;	
			}
			if (!finance.isPayment() && finance.getPayMethodType() == PayMethodType.NEGOTIABLE_DOCUMENT) {
				mustShowRegistryBanks = true;	
			}

			if (mustShowRegistryBanks) {
				registryBankIcon.removeStyleName(AON.AON_CSS.aonIconCompany());
				registryBankIcon.addStyleName(AON.AON_CSS.aonIconEmployee());
				FINANCE_SERVICE.getRegistryBanks(domainName, domain, user, finance.getRegistry().getId(), new AsyncCallback<LinkedList<RegistryBank>>() {
					
					@Override
					public void onSuccess(LinkedList<RegistryBank> result) {
						registryBankListBox.setBanks( result );
						registryBankListBox.setValue(finance.getBankAccount()==null?null:finance.getBankAccount().getIban());
						RegistryBank selected = registryBankListBox.getValue();
						if (selected == null) {
							payAccount.setValue(null,true);
						} else {
							payAccount.setValue(selected.getAccount(),selected.getAccountCode(),selected.getAccountDescription(),true);
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						MessageDialog.error("No se han podido recuperar los bancos del titular del vencimiento. [" + caught.getMessage() + "]");								
					}
				});
			} else {
				registryBankIcon.addStyleName(AON.AON_CSS.aonIconCompany());
				registryBankIcon.removeStyleName(AON.AON_CSS.aonIconEmployee());
				FINANCE_SERVICE.getCompanyBanks(domainName, domain, user, new AsyncCallback<LinkedList<RegistryBank>>() {
					
					@Override
					public void onSuccess(LinkedList<RegistryBank> result) {
						registryBankListBox.setBanks( result );
						registryBankListBox.setValue(finance.getBankAccount()==null?null:finance.getBankAccount().getIban());
						RegistryBank selected = registryBankListBox.getValue();
						if (selected == null) {
							payAccount.setValue(null,true);
						} else {
							payAccount.setValue(selected.getAccount(),selected.getAccountCode(),selected.getAccountDescription(),true);
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						MessageDialog.error("No se han podido recuperar los bancos de la empresa. [" + caught.getMessage() + "]");
					}
				});
			}
		}
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


