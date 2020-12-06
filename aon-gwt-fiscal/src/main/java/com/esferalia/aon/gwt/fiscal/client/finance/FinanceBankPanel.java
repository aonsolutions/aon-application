package com.esferalia.aon.gwt.fiscal.client.finance;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBankAccountBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.widget.RegistryBankListBox;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

public class FinanceBankPanel extends SimplePanel implements Focusable {
	
	public static interface FinanceBankPanelCallback {
		void onAccept(Finance finance);
		void onCancel();
	}

	private static FinanceServiceAsync FINANCE_SERVICE;
	
	private String domainName;
	private int domain;
	private String user;
	private AonConfiguration config;
	private InlineLabel registryBankIcon = new InlineLabel();
	private RegistryBankListBox registryBankListBox;
	private Label bankAccountLabel = new Label();
	private AonBankAccountBox bankAccountBox;
	private ListBox payMethodBox;
	private Finance financeData;	
	
	public void show(final String domainName
			, final int domain
			, final String user
			, final AonConfiguration config
			, final Finance oriData
			, final FinanceBankPanelCallback callback) {
		setWidth("700px");
		setHeight("200px");
		
		this.financeData = new Finance();
		if ( oriData != null) {
			this.financeData.setPayment(oriData.isPayment());
			this.financeData.setPayment(oriData.isPayment());
			this.financeData.setRegistry(oriData.getRegistry());
			this.financeData.setRegistryDocument(oriData.getRegistryDocument());
			this.financeData.setRegistryDocumentType(oriData.getRegistryDocumentType());
			this.financeData.setRegistryDocumentCountry(oriData.getRegistryDocumentCountry());
			this.financeData.setRegistryName(oriData.getRegistryName());
			this.financeData.setPayMethod(oriData.getPayMethod());
			this.financeData.setPayMethodName(oriData.getPayMethodName());
			this.financeData.setPayMethodType(oriData.getPayMethodType());
			this.financeData.setBankAccount(oriData.getBankAccount());
			this.financeData.setBankAlias(oriData.getBankAlias());
			this.financeData.setBic(oriData.getBic());
		}
		
		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
		this.config = config;
		this.registryBankListBox = new RegistryBankListBox();
		this.bankAccountBox = new AonBankAccountBox( financeData.getBankAccount() );
		this.payMethodBox = new ListBox();
		
		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);

		FlowPanel rootPanel = new FlowPanel();
		
		FlowPanel tablePanel = new FlowPanel();
		tablePanel.setStyleName(AON.CSS.aonScrollArea());
		
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
		
		table.setStyleName(AON.CSS.aonTable());
		table.addStyleName(AON.CSS.aonWidthAll());
		int row = 0;
		
		table.setWidget(row,0,new InlineLabel(AON.MSG.payMethod()));
		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		payMethodBox.addKeyUpHandler(keyUpHandler);
		payMethodBox.addItem(" ---- ", "");
		for (PayMethod pm : config.getPayMethods()) {
			payMethodBox.addItem(pm.getName(), AonNumberUtils.toString( pm.getId()));
			if ( AonNumberUtils.equals(pm.getId(), this.financeData.getPayMethod()) ) {
				payMethodBox.setSelectedIndex( payMethodBox.getItemCount() - 1);
			}
		}
		payMethodBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int idx = payMethodBox.getSelectedIndex();
				idx = idx - 1;
				if (idx > 0 ) {
					PayMethod payMethod = config.getPayMethods().get(idx);
					FinanceBankPanel.this.financeData.setPayMethod(payMethod.getId());
					FinanceBankPanel.this.financeData.setPayMethodName(payMethod.getName());
					FinanceBankPanel.this.financeData.setPayMethodType(payMethod.getType());
					refreshBankAccountPanel(payMethod.getType());
				} else {
					FinanceBankPanel.this.financeData.setPayMethod(null);
					FinanceBankPanel.this.financeData.setPayMethodName(null);
					FinanceBankPanel.this.financeData.setPayMethodType(null);
					refreshBankAccountPanel(null);
				}
			}
		});
		table.setWidget(row,1,payMethodBox);
		row++;
		
		table.setWidget(row,0,bankAccountLabel);
		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		
		FlowPanel banksPanel = new FlowPanel();
		
		FlowPanel row1= new FlowPanel();
		row1.setStyleName(AON.CSS.aonDisplayGridCellInner());
		banksPanel.add(row1);
		InlineLabel banksLabel = new InlineLabel("Bancos:");
		banksLabel.setStyleName(AON.CSS.aonInnerLabel());
		banksLabel.getElement().getStyle().setWidth(50, Unit.PX);
		row1.add(banksLabel); 
		
		row1.add(registryBankListBox);
		registryBankIcon.setStyleName(AON.CSS.aonTabIcon());
		row1.add(registryBankIcon);
		registryBankListBox.addKeyUpHandler(keyUpHandler);
		registryBankListBox.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				RegistryBank rbank = registryBankListBox.getValue();
				if (rbank == null) {
					FinanceBankPanel.this.financeData.setBankAccount(null);
					bankAccountBox.setValue(null);
				} else {
					FinanceBankPanel.this.financeData.setBankAccount(rbank.getBankAccount());
					bankAccountBox.setValue(rbank.getBankAccount());
				}
			}
		});
		bankAccountBox.addKeyUpHandler(keyUpHandler);
		bankAccountBox.addStyleName(AON.CSS.aonMarginTopSep());
		banksPanel.add(bankAccountBox);
		table.setWidget(row,1,banksPanel);
		row++;
		
		refreshBankAccountPanel( this.financeData.getPayMethodType() );
		
		tablePanel.add( table );
		rootPanel.add( tablePanel );
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
    	final Button okButton = new Button(AON.MSG.accept());
    	okButton.setStyleName( AON.CSS.aonOkButton() );
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler( keyUpHandler);
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (FinanceBankPanel.this.financeData.getPayMethod() == null) { 
					AonMessageDialog.error("No se ha indicado forma de pago.");
					payMethodBox.setFocus(true);
				} else {
					okButton.setEnabled(false);
					callback.onAccept(FinanceBankPanel.this.financeData);
				}
			}
		});
    	
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button(AON.MSG.cancelAction());
    	cancelButton.setStyleName( AON.CSS.aonOkButton() );
    	cancelButton.addStyleName( AON.CSS.aonMarginLeft() );
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
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				payMethodBox.setFocus(true);
			}
		});

	}
	
	private void refreshBankAccountPanel(final PayMethodType payMethodType) {
			bankAccountLabel.setText( AON.MSG.bankAccount() );
			boolean mustShowRegistryBanks = false;
			if (this.financeData.isPayment() && payMethodType == PayMethodType.BANK_TRANSFER) {
				mustShowRegistryBanks = true;	
			}
			if (!this.financeData.isPayment() && payMethodType == PayMethodType.NEGOTIABLE_DOCUMENT) {
				mustShowRegistryBanks = true;	
			}

			if (mustShowRegistryBanks) {
				registryBankIcon.removeStyleName(AON.CSS.aonIconHome());
				registryBankIcon.addStyleName(AON.CSS.aonIconEmployee());
				registryBankIcon.setTitle("Bancos definidos de \"" + this.financeData.getRegistryName() + "\"");
				FINANCE_SERVICE.getRegistryBanks(domainName, domain, user, this.financeData.getRegistry().getId(), new AsyncCallback<LinkedList<RegistryBank>>() {
					
					@Override
					public void onSuccess(LinkedList<RegistryBank> result) {
						registryBankListBox.setBanks( result );
						registryBankListBox.setValue(FinanceBankPanel.this.financeData.getBankAccount()==null?null:FinanceBankPanel.this.financeData.getBankAccount().getIban());
					}
					
					@Override
					public void onFailure(Throwable caught) {
						AonMessageDialog.error("No se han podido recuperar los bancos del titular del vencimiento. [" + caught.getMessage() + "]");								
					}
				});
			} else {
				registryBankIcon.addStyleName(AON.CSS.aonIconHome());
				registryBankIcon.setTitle("Bancos definidos de \"" + config.getCompany().getName() + "\"");
				registryBankIcon.removeStyleName(AON.CSS.aonIconEmployee());
				FINANCE_SERVICE.getCompanyBanks(domainName, domain, user, new AsyncCallback<LinkedList<RegistryBank>>() {
					
					@Override
					public void onSuccess(LinkedList<RegistryBank> result) {
						registryBankListBox.setBanks( result );
						registryBankListBox.setValue(FinanceBankPanel.this.financeData.getBankAccount()==null?null:FinanceBankPanel.this.financeData.getBankAccount().getIban());
					}
					
					@Override
					public void onFailure(Throwable caught) {
						AonMessageDialog.error("No se han podido recuperar los bancos de la empresa. [" + caught.getMessage() + "]");
					}
				});
			}
	}

	@Override
	public int getTabIndex() {
		return payMethodBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		payMethodBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		payMethodBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		payMethodBox.setTabIndex(index);
	}
}


