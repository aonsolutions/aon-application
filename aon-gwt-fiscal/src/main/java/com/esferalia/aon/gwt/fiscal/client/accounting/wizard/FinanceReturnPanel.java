package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.widget.BankAccountBox;
import com.esferalia.aon.gwt.fiscal.client.widget.RegistryBankListBox;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.PayMethodTypeDetail;
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
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

public class FinanceReturnPanel extends SimplePanel implements Focusable {
	
	public static interface FinanceReturnPanelCallback {
		void onAccept(FinanceTracking tracking);
		void onCancel();
	}

	private static FinanceServiceAsync FINANCE_SERVICE;
	
	private DeckPanel panels = new DeckPanel();
	
	private String domainName;
	private int domain;
	private String user;
	private AonConfiguration config;
	private final DateBoxEx returnDate = new DateBoxEx();
	private ListBox cashListBox;
	private ListBox otherListBox;
	private InlineLabel registryBankIcon = new InlineLabel();
	private RegistryBankListBox registryBankListBox;
	private Label bankAccountLabel = new Label();
	private BankAccountBox bankAccountBox;
	private AccountBox payAccount;
	private ListBox payMethodTypeBox;
	private DoubleBox amount;
	private FinanceTracking tracking;	
	
	public void show(final String domainName
			, final int domain
			, final String user
			, final AonConfiguration config
			, final Finance finance 
			, final FinanceReturnPanelCallback callback) {
		setWidth("700px");
		setHeight("200px");
		
		this.tracking = new FinanceTracking()
				.setDomain(domain)
				.setFinance(finance)
				.setTrackingDate( new Date() )
				.setAmount(finance.getAmount() );
		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
		this.config = config;
		this.payMethodTypeBox  = new ListBox();
		this.cashListBox = new ListBox();
		this.otherListBox = new ListBox();
		this.registryBankListBox = new RegistryBankListBox();
		this.bankAccountBox = new BankAccountBox( tracking.getFinance().getBankAccount() );
		this.payAccount = new AccountBox(domainName,domain, user);
		this.amount = new DoubleBox();
		
		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);

		FlowPanel rootPanel = new FlowPanel();
		
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
		returnDate.setValue(finance.getDueDate());
		returnDate.getTextBox().addKeyUpHandler( keyUpHandler);
		returnDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				tracking.setTrackingDate(event.getValue());
			}
		});
		table.setWidget(row,1,returnDate);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;

		table.setWidget(row,0,new InlineLabel(AON.MSG.deposit()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		payMethodTypeBox.addKeyUpHandler(keyUpHandler);
		for (PayMethodType payMethodType : PayMethodType.values()) {
			payMethodTypeBox.addItem(payMethodType.getDescription());
		}
		if (finance.getPayMethodType() != null) {
			payMethodTypeBox.setSelectedIndex(finance.getPayMethodType().ordinal());
		} else {
			payMethodTypeBox.setSelectedIndex(PayMethodType.NEGOTIABLE_DOCUMENT.ordinal());
		}
		payMethodTypeBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				PayMethodType pm = PayMethodType.values()[payMethodTypeBox.getSelectedIndex()];
				refreshBankAccountPanel( pm );
			}
		});
		table.setWidget(row,1,payMethodTypeBox);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;
		
		table.setWidget(row,0,bankAccountLabel);
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		
		FlowPanel banksPanel = new FlowPanel();
		panels.add(banksPanel);
		
		InlineLabel banksLabel = new InlineLabel("Bancos:");
		banksLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		banksLabel.addStyleName(AON.AON_CSS.aonWidth50());
		banksPanel.add(banksLabel); 
		
		banksPanel.add(registryBankListBox);
		registryBankIcon.setStyleName(AON.AON_CSS.aonPaddingLeft20());
		registryBankIcon.addStyleName(AON.AON_CSS.aonMarginLeft());
		banksPanel.add(registryBankIcon);
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
					finance.setBankAccount( rbank.getBankAccount()  );
					finance.setBankAlias(rbank.getAlias());
					finance.setBic(rbank.getBic());
					bankAccountBox.setValue(rbank.getBankAccount() );
					payAccount.setValue(rbank.getAccount(),rbank.getAccountCode(),rbank.getAccountDescription(),true);
				}
			}
		});
		registryBankListBox.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				RegistryBank rbank = registryBankListBox.getValue();
				if (rbank == null) {
					tracking.setRegistryBank(null);
					bankAccountBox.setValue(null);
				} else {
					tracking.setRegistryBank(rbank);
					bankAccountBox.setValue(rbank.getBankAccount());
					payAccount.setValue(rbank.getAccount(),rbank.getAccountCode(),rbank.getAccountDescription(),true);
				}
			}
		});
		banksPanel.add(bankAccountBox);
		
		FlowPanel cashPanel = new FlowPanel();
		cashListBox.addItem(" ---- ", "");
		for (PayMethodTypeDetail pm : config.getPayMethodTypeDetails()) {
			if ( pm.getType() == PayMethodType.CASH_BASIS) {
				cashListBox.addItem(pm.getDescription(), AonNumberUtils.toString( pm.getId()));
			}
		}
		cashListBox.addKeyUpHandler(keyUpHandler);
		cashListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int idx = cashListBox.getSelectedIndex();
				idx = idx - 1;
				if (idx < 0 ) {
					tracking.setPayMethodTypeDetail(null);
					payAccount.setAccount(null,true);
				} else {
					Integer selected = AonNumberUtils.toInteger(cashListBox.getSelectedValue());					
					for (PayMethodTypeDetail pm : config.getPayMethodTypeDetails()) {
						if (AonNumberUtils.equals(pm.getId() , selected)) {
							tracking.setPayMethodTypeDetail(pm);
							payAccount.setAccount(pm.getAccount(),true);
						}
					};
				}
			}
		});
		cashPanel.add(cashListBox);
		panels.add(cashPanel);
		
		FlowPanel otherPanel = new FlowPanel();
		otherListBox.addItem(" ---- ", "");
		for (PayMethodTypeDetail pm : config.getPayMethodTypeDetails()) {
			if ( pm.getType() == PayMethodType.OTHER) {
				otherListBox.addItem(pm.getDescription(), AonNumberUtils.toString( pm.getId()));
			}
		}
		otherListBox.addKeyUpHandler(keyUpHandler);
		otherListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int idx = otherListBox.getSelectedIndex();
				idx = idx - 1;
				if (idx < 0 ) {
					tracking.setPayMethodTypeDetail(null);
					payAccount.setAccount(null,true);
				} else {
					Integer selected = AonNumberUtils.toInteger(otherListBox.getSelectedValue());					
					for (PayMethodTypeDetail pm : config.getPayMethodTypeDetails()) {
						if (AonNumberUtils.equals(pm.getId() , selected)) {
							tracking.setPayMethodTypeDetail(pm);
							payAccount.setAccount(pm.getAccount(),true);
						}
					};
				}
			}
		});
		otherPanel.add(otherListBox);
		panels.add(otherPanel);
		

		table.setWidget(row,1,panels);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;
		
		refreshBankAccountPanel( finance.getPayMethodType() );

		table.setWidget(row,0,new InlineLabel(AON.MSG.account()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		payAccount.setAccount( tracking.getPayAccount());
		payAccount.addValueChangeHandler( new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if (payAccount.getId() == null) {
					tracking.setPayAccount(null);
				} else {
					tracking.setPayAccount( new Account()
						.setId(payAccount.getId())
						.setCode(payAccount.getCode())
						.setDescription(payAccount.getDescription()));
				}
			}
		});
		payAccount.addSelectionHandler( new SelectionHandler<Account>() {
			
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				if (payAccount.getId() == null) {
					tracking.setPayAccount(null);
				} else {
					tracking.setPayAccount(event.getSelectedItem());
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
				if (tracking.getPayAccount() == null || tracking.getPayAccount().getId() == null ) {
					MessageDialog.error("No se ha indicado la cuenta contable del banco o caja.");
					payAccount.setFocus(true);
				} else {
					okButton.setEnabled(false);
					callback.onAccept(tracking);
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
		returnDate.setFocus(true);
	}
	private int getPanelIndex(final PayMethodType payMethodType) {
		if (payMethodType == PayMethodType.CASH_BASIS) return 1;
		if (payMethodType == PayMethodType.OTHER) return 2;
		return 0;
	}
	
	private void refreshBankAccountPanel(final PayMethodType payMethodType) {
		
		final Finance finance = tracking.getFinance();
		panels.showWidget( getPanelIndex(payMethodType) );
		if (payMethodType == PayMethodType.CASH_BASIS) {
			bankAccountLabel.setText( PayMethodType.CASH_BASIS.getDescription() );
			registryBankListBox.setValue(null);
			bankAccountBox.setValue(null);
			cashListBox.setSelectedIndex(0);
			Account cashAccount = config.getDefaultCashAccount();
			payAccount.setAccount(cashAccount);
			tracking.setRegistryBank(null);
			tracking.setPayAccount(cashAccount);
		} else if (payMethodType == PayMethodType.OTHER) {
			bankAccountLabel.setText( "Otras formas de pago" );
			registryBankListBox.setValue(null); 
			bankAccountBox.setValue(null);
			otherListBox.setSelectedIndex(0);
			payAccount.setAccount(null);
			tracking.setRegistryBank(null);
			tracking.setPayAccount(null);
		} else {
			bankAccountLabel.setText( AON.MSG.bankAccount() );
			boolean mustShowRegistryBanks = false;
			tracking.setRegistryBank(null);
			payAccount.setAccount(null,true);
			if (finance.isPayment() && payMethodType == PayMethodType.BANK_TRANSFER) {
				mustShowRegistryBanks = true;	
			}
			if (!finance.isPayment() && payMethodType == PayMethodType.NEGOTIABLE_DOCUMENT) {
				mustShowRegistryBanks = true;	
			}

			if (mustShowRegistryBanks) {
				registryBankIcon.removeStyleName(AON.AON_CSS.aonIconCompany());
				registryBankIcon.addStyleName(AON.AON_CSS.aonIconEmployee());
				registryBankIcon.setTitle("Bancos definidos de \"" + finance.getRegistryName() + "\"");
				FINANCE_SERVICE.getRegistryBanks(domainName, domain, user, finance.getRegistry().getId(), new AsyncCallback<LinkedList<RegistryBank>>() {
					
					@Override
					public void onSuccess(LinkedList<RegistryBank> result) {
						registryBankListBox.setBanks( result );
						registryBankListBox.setValue(finance.getBankAccount()==null?null:finance.getBankAccount().getIban());
						RegistryBank selected = registryBankListBox.getValue();
						if (selected == null) {
							tracking.setRegistryBank(null);
							tracking.setPayAccount(null);
							payAccount.setValue(null);
						} else {
							tracking.setRegistryBank(selected);
							tracking.setPayAccount(new Account()
									.setId(selected.getAccount())
									.setCode(selected.getAccountCode())
									.setDescription(selected.getAccountDescription())
									);
							payAccount.setAccount( tracking.getPayAccount() );
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						MessageDialog.error("No se han podido recuperar los bancos del titular del vencimiento. [" + caught.getMessage() + "]");								
					}
				});
			} else {
				registryBankIcon.addStyleName(AON.AON_CSS.aonIconCompany());
				registryBankIcon.setTitle("Bancos definidos de \"" + config.getCompany().getName() + "\"");
				registryBankIcon.removeStyleName(AON.AON_CSS.aonIconEmployee());
				FINANCE_SERVICE.getCompanyBanks(domainName, domain, user, new AsyncCallback<LinkedList<RegistryBank>>() {
					
					@Override
					public void onSuccess(LinkedList<RegistryBank> result) {
						registryBankListBox.setBanks( result );
						registryBankListBox.setValue(finance.getBankAccount()==null?null:finance.getBankAccount().getIban());
						RegistryBank selected = registryBankListBox.getValue();
						if (selected == null) {
							tracking.setRegistryBank(null);
							tracking.setPayAccount(null);
							payAccount.setValue(null);
						} else {
							tracking.setRegistryBank(selected);
							tracking.setPayAccount(new Account()
									.setId(selected.getAccount())
									.setCode(selected.getAccountCode())
									.setDescription(selected.getAccountDescription())
									);
							payAccount.setAccount( tracking.getPayAccount() );
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
		return returnDate.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		returnDate.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		returnDate.getTextBox().selectAll();
		returnDate.setFocus(focused);
		returnDate.hideDatePicker();
	}

	@Override
	public void setTabIndex(int index) {
		returnDate.setTabIndex(index);
	}
}


