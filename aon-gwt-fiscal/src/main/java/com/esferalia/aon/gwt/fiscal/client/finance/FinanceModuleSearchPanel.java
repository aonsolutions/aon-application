package com.esferalia.aon.gwt.fiscal.client.finance;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;


public class FinanceModuleSearchPanel extends SimpleLayoutPanel implements Focusable, HasValueChangeHandlers<FinanceParams>{

	private String domainName;
	private int domainId;
	private String currentUser;
	private AonConfiguration configuration;
	
	private FlowPanel container;
	private FlowPanel datePanel;
	private FlowPanel amountPanel; 
	private FlowPanel paymentPanel; 
	
	private DateBoxEx fromDate;
	private DateBoxEx toDate;
	
	private CheckBox confidential;
	private ListBox payment;
	private AccountingRegistryBox registryBox;
	private DoubleBox amount;
	private CheckBox nearbyNumbers;
	private TextBox concept;
	private TextBox referenceCode;
	
	public static interface IFinancePanelCallback {
		boolean isSelected( Finance finance);
	}
	
	public FinanceModuleSearchPanel(String domainName,int domainId, String currentUser, AonConfiguration config) {
		this.domainName = domainName;
		this.domainId = domainId;
		this.currentUser = currentUser;
		this.configuration = config;
		
		addStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonMarginBottom());

		amount = new DoubleBox();
		amount.setVisibleLength(6);
		amount.setValue(null,false);
		amount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> arg0) {
				search();
			}
		});
		
		nearbyNumbers = new CheckBox(AON.MSG.nearbyNumbers());
		nearbyNumbers.setStyleName(AON.AON_CSS.aonPadding2Left());
		nearbyNumbers.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				search();
			}
		});

		concept = new TextBox();
		concept.setStyleName(AON.AON_CSS.aonInputText());
		concept.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> arg0) {
				search();
			}
		});

		fromDate = new DateBoxEx();
		fromDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> arg0) {
				search();
			}
		});
		
		toDate = new DateBoxEx();
		toDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> arg0) {
				search();
			}
		});
		
		confidential = new CheckBox( AON.MSG.confidential());
		confidential.addStyleName(AON.AON_CSS.aonMarginLeft());
		confidential.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				search();
			}
		});
		
		referenceCode = new TextBox();
		referenceCode.setStyleName(AON.AON_CSS.aonInputText());
		referenceCode.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> arg0) {
				search();
			}
		});

		registryBox = new AccountingRegistryBox(this.domainName,this.domainId,this.currentUser, null, false);
		registryBox.setRequired(false);
		
		registryBox.addSelectionHandler(new SelectionHandler<AccountingRegistry>() {
			
			@Override
			public void onSelection(SelectionEvent<AccountingRegistry> arg0) {
				search();
			}
		});

		payment = new ListBox();
		payment.addItem(" --- "," --- ");
		payment.addItem("Pago" ,"Pago");
		payment.addItem("Cobro", "Cobro");
		payment.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				search();
			}
		});
		
		FlowPanel flowNorthPanel = new FlowPanel();
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonPanelGridSearch());
		tab.addStyleName(AON.AON_CSS.aonWidthAll());
		
		tab.getColumnFormatter().setWidth(0, "60px");
		tab.getColumnFormatter().setWidth(1, "180px");
		tab.getColumnFormatter().setWidth(2, "85px");
		tab.getColumnFormatter().setWidth(3, "auto");
		
		tab.setWidget(0, 0, new Label(AON.MSG.amount()));
		tab.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonBold());
		amountPanel = new FlowPanel();
		amountPanel.setStyleName(AON.AON_CSS.aonNowrap());
		amountPanel.add(amount);
		amountPanel.add(nearbyNumbers);
		tab.setWidget(0, 1, amountPanel);


		tab.setWidget(0, 2, new Label(AON.MSG.concept()));
		tab.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonBold());
		tab.setWidget(0, 3, concept);
		
		tab.setWidget(1, 0, new Label(AON.MSG.date()));
		tab.getCellFormatter().setStyleName(1,0, AON.AON_CSS.aonBold());
		
		datePanel = new FlowPanel();
		datePanel.setStyleName(AON.AON_CSS.aonNowrap());
		datePanel.add(fromDate);
		InlineLabel to = new InlineLabel(AON.MSG.to());
		to.setStyleName(AON.AON_CSS.aonItalic());
		to.addStyleName(AON.AON_CSS.aonMarginRight());
		to.addStyleName(AON.AON_CSS.aonMarginLeft());
		datePanel.add(to);
		datePanel.add(toDate);
		tab.setWidget(1, 1, datePanel);

		tab.setWidget(1, 2, new Label(AON.MSG.invoiceNumberAbbr()));
		tab.getCellFormatter().setStyleName(1,2, AON.AON_CSS.aonBold());
		tab.setWidget(1, 3, referenceCode);

		tab.setWidget(2, 0, new Label(AON.MSG.titular()));
		tab.getCellFormatter().setStyleName(2,0, AON.AON_CSS.aonBold());
		tab.setWidget(2, 1, registryBox);
		
		paymentPanel = new FlowPanel();
		paymentPanel.add(payment);
		tab.setWidget(2, 2, new Label(AON.MSG.type()));
		tab.getCellFormatter().setStyleName(2,2, AON.AON_CSS.aonBold());
		tab.setWidget(2, 3, paymentPanel);

		// TODO
		if (configuration != null && configuration.getUser() != null && configuration.getUser().hasConfidentialityRole()) {
			paymentPanel.add(confidential);
		}
		
		flowNorthPanel.add(tab);
		setWidget(flowNorthPanel);
	}
	
	@Override
	public int getTabIndex() {
		return amount.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		amount.setAccessKey(key);;
	}

	@Override
	public void setFocus(boolean focused) {
		amount.setFocus(true);
		amount.selectAll();
	}

	@Override
	public void setTabIndex(int index) {
		fromDate.setTabIndex(index);
	}
	
	private void search() {
		ValueChangeEvent.<FinanceParams>fire( FinanceModuleSearchPanel.this, getParams() ); 
	}

	public void initialize() {
		container.clear();
		fromDate.setValue(null);
		toDate.setValue(null);
		confidential.setValue(false);
		registryBox.setValue( (AccountingRegistry) null, false);
		amount.setValue(null);
		concept.setValue(null);
		search();
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<FinanceParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	public FinanceParams getParams() {
		boolean confidentiality = this.configuration != null 
				&&  this.configuration.getUser() != null 
				&& this.configuration.getUser().hasConfidentialityRole();
		return new FinanceParams()
			.setDomain(this.domainId)
			.setFrom(fromDate.getValue())
			.setTo(toDate.getValue())
			.setRegistry(registryBox.getId())
			.setAmount((amount.getValue() != null && amount.getValue()!=0)?amount.getValue():null)
			.setNearbyNumbers(nearbyNumbers.getValue())
			.setConcept(concept.getValue())
			.setReferenceCode(referenceCode.getValue())
			.setPayment((payment.getSelectedIndex() == 0)?null:(payment.getSelectedIndex() == 1))
			.setConfidential(confidential.getValue())
			.setHasConfidentialityRole(confidentiality)
			;
	}
	
}
