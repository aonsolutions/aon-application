package com.esferalia.aon.gwt.fiscal.client.finance;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.PayMethodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
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

	public static final double HEIGHT = 130;
	
	private FlowPanel paymentPanel; 
	
	private AonDateBox fromInvoiceDate;
	private AonDateBox toInvoiceDate;
	
	private AonDateBox fromDueDate;
	private AonDateBox toDueDate;
	
	private ListBox confidential;
	private ListBox payment;
	private AonAccountingRegistryBox registryBox;
	private AonDoubleBox amount;
	private CheckBox nearbyNumbers;
	private TextBox concept;
	private TextBox referenceCode;
	private PayMethodListBox payMethod;
	
	private CheckBox pending;
	private CheckBox batched;
	private CheckBox returned;
	private CheckBox paid;
	private CheckBox settled;
	
	private AonSearchPanelButton cleanButton;
	private AonSearchPanelButton refreshButton;

	public static interface IFinancePanelCallback {
		boolean isSelected( Finance finance);
	}
	
	public FinanceModuleSearchPanel(final FinanceModuleOptions opt) {
		
		setStyleName(AON.CSS.aonSearchPanel());
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());
		addStyleName(AON.CSS.aonMarginLeft());
		addStyleName(AON.CSS.aonMarginRight());
		addStyleName(AON.CSS.aonBlockCenter());

		amount = new AonDoubleBox();
		amount.setVisibleLength(6);
		amount.setValue(null,false);
		amount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> arg0) {
				search(opt);
			}
		});
		
		nearbyNumbers = new CheckBox(AON.MSG.nearbyNumbers());
		nearbyNumbers.setStyleName(AON.CSS.aonPaddingLeft());
		nearbyNumbers.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				search(opt);
			}
		});

		concept = new TextBox();
		concept.setStyleName(AON.CSS.aonInputText());
		concept.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> arg0) {
				search(opt);
			}
		});

		fromInvoiceDate = new AonDateBox();
		fromInvoiceDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> arg0) {
				search(opt);
			}
		});
		
		toInvoiceDate = new AonDateBox();
		toInvoiceDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> arg0) {
				search(opt);
			}
		});
		
		fromDueDate = new AonDateBox();
		fromDueDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> arg0) {
				search(opt);
			}
		});
		
		toDueDate = new AonDateBox();
		toDueDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> arg0) {
				search(opt);
			}
		});

		confidential = new ListBox();
		confidential.setStyleName(AON.CSS.aonMarginLeft());
		confidential.setWidth("100px");
		confidential.addItem( "NO confidenciales" );
		confidential.addItem( "Confidenciales" );
		confidential.addItem(" Todos ");
		confidential.setSelectedIndex(2);
		confidential.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				search(opt);
			}
		});
		
		referenceCode = new TextBox();
		referenceCode.setStyleName(AON.CSS.aonInputText());
		referenceCode.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> arg0) {
				search(opt);
			}
		});
		
		payMethod = new PayMethodListBox();
		payMethod.fill(opt.getConfiguration().getPayMethods());
		payMethod.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				search(opt);
			}
		});
		
		pending = new CheckBox( FinanceStatus.PENDING.getDescription()) ;
		pending.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				search(opt);
			}
		});
		
		batched  = new CheckBox( FinanceStatus.BATCHED.getDescription()) ;
		batched.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				search(opt);
			}
		});
		
		returned  = new CheckBox( FinanceStatus.RETURNED.getDescription()) ;
		returned.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				search(opt);
			}
		});
		
		paid  = new CheckBox( FinanceStatus.PAID.getDescription()) ;
		paid.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				search(opt);
			}
		});
		
		settled  = new CheckBox( FinanceStatus.SETTLED.getDescription()) ;
		settled.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				search(opt);
			}
		});
		

		registryBox = new AonAccountingRegistryBox(opt, true);
		registryBox.setRequired(false);
		
		registryBox.addSelectionHandler(new SelectionHandler<AccountingRegistry>() {
			
			@Override
			public void onSelection(SelectionEvent<AccountingRegistry> arg0) {
				search(opt);
			}
		});

		payment = new ListBox();
		payment.addItem(" --- "," --- ");
		payment.addItem("Pago" ,"Pago");
		payment.addItem("Cobro", "Cobro");
		payment.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				search(opt);
			}
		});
		
		cleanButton = new AonSearchPanelButton(AON.MSG.clean(), AON.CSS.aonIconClear());
		cleanButton.addStyleName(AON.CSS.aonMarginLeft());
		cleanButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				initialize(opt);
			}
		});

		refreshButton = new AonSearchPanelButton(AON.MSG.refresh(), AON.CSS.aonIconSearch());
		refreshButton.addStyleName(AON.CSS.aonMarginLeft());
		refreshButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				search(opt);
			}
		});

		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.CSS.aonGrid());
		tab.setWidth("100%");
		
		tab.getColumnFormatter().setWidth(0, "110px");
		tab.getColumnFormatter().setWidth(1, "250px");
		tab.getColumnFormatter().setWidth(2, "110px");
		tab.getColumnFormatter().setWidth(3, "250px");
		tab.getColumnFormatter().setWidth(4, "auto");
		
		int row = 0;
		int col = 0;
		
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.amount()));
		++col;
		FlowPanel amountPanel = new FlowPanel();
		amountPanel.setStyleName(AON.CSS.aonNowrap());
		amountPanel.add(amount);
		amountPanel.add(nearbyNumbers);
		tab.setWidget(row, col, amountPanel);
		++col;


		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.titular()));
		++col;
		tab.setWidget(row, col, registryBox);
		++col;
		
		tab.setWidget(row, col, new InlineLabel());
		++col;
		
		++row;
		col = 0;
		
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.invoiceNumberAbbr()));
		++col;
		tab.setWidget(row, col, referenceCode);
		++col;
		
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.concept()));
		++col;
		tab.setWidget(row, col, concept);
		++col;

		tab.setWidget(row, col, new InlineLabel());
		++col;

		//TODO Buscar sin FACTURA
		
		++row;
		col = 0;

		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.invoiceDate()));
		++col;
		FlowPanel invoiceDatePanel = new FlowPanel();
		invoiceDatePanel.setStyleName(AON.CSS.aonNowrap());
		invoiceDatePanel.add(fromInvoiceDate);
		InlineLabel to = new InlineLabel(AON.MSG.to());
		to.setStyleName(AON.CSS.aonItalic());
		to.addStyleName(AON.CSS.aonMarginRight());
		to.addStyleName(AON.CSS.aonMarginLeft());
		invoiceDatePanel.add(to);
		invoiceDatePanel.add(toInvoiceDate);
		tab.setWidget(row, col, invoiceDatePanel);
		++col;
		
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.dueDate()));
		++col;
		FlowPanel dueDatePanel = new FlowPanel();
		dueDatePanel.setStyleName(AON.CSS.aonNowrap());
		dueDatePanel.add(fromDueDate);
		InlineLabel dueTo = new InlineLabel(AON.MSG.to());
		dueTo.setStyleName(AON.CSS.aonItalic());
		dueTo.addStyleName(AON.CSS.aonMarginRight());
		dueTo.addStyleName(AON.CSS.aonMarginLeft());
		dueDatePanel.add(dueTo);
		dueDatePanel.add(toDueDate);
		tab.setWidget(row, col, dueDatePanel);
		++col;
		tab.setWidget(row, col, new InlineLabel());
		++col;
		
		++row;
		col = 0;
		paymentPanel = new FlowPanel();
		paymentPanel.add(payment);
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.type()));
		++col;
		tab.setWidget(row, col, paymentPanel);
		++col;
		// TODO
		if (opt.getConfiguration() != null && opt.getConfiguration().getUser() != null && opt.getConfiguration().getUser().hasConfidentialityRole()) {
			paymentPanel.add(confidential);
		}
		
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.payMethod()));
		++col;
		tab.setWidget(row, col, payMethod);
		++col;
		tab.setWidget(row, col, new InlineLabel());
		++col;
		
		++row;
		col = 0;
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.status()));
		++col;
		FlowPanel statusPanel = new FlowPanel();
		statusPanel.add(pending);
		statusPanel.add(batched);
		statusPanel.add(returned);
		statusPanel.add(paid);
		statusPanel.add(settled);
		tab.setWidget(row, col, statusPanel);
		tab.getFlexCellFormatter().setColSpan(row, col, 3);
		++col;
		
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonNowrap());
		buttonsPanel.add( cleanButton );
		buttonsPanel.add( refreshButton );
		tab.setWidget(row, col, buttonsPanel);
		++col;
		setWidget(tab);
		
		initialize(opt);
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
		fromInvoiceDate.setTabIndex(index);
	}
	
	private void search(final FinanceModuleOptions opt) {
		ValueChangeEvent.<FinanceParams>fire( FinanceModuleSearchPanel.this, getParams( opt ) ); 
	}

	public void initialize(final FinanceModuleOptions opt) {
		amount.setValue(null);
		nearbyNumbers.setValue(false);
		fromInvoiceDate.setValue(null);
		toInvoiceDate.setValue(null);
		fromDueDate.setValue(null);
		toDueDate.setValue(null);
		payMethod.setSelectedIndex(0);
		confidential.setSelectedIndex(2);
		registryBox.setValue( (AccountingRegistry) null, false);
		concept.setValue(null);
		pending.setValue(true);
		batched.setValue(false);
		returned.setValue(true);
		paid.setValue(false);
		settled.setValue(false);
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<FinanceParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	public FinanceParams getParams( final FinanceModuleOptions opt) {
		boolean confidentiality = opt.getConfiguration() != null 
				&& opt.getUser() != null 
				&& opt.getConfiguration().getUser().hasConfidentialityRole();
		return new FinanceParams()
			.setDomain(opt.getDomain())
			.setFromInvoiceDate(fromInvoiceDate.getValue())
			.setToInvoiceDate(toInvoiceDate.getValue())
			.setFromDueDate(fromDueDate.getValue())
			.setToDueDate(toDueDate.getValue())
			.setRegistry(registryBox.getId())
			.setAmount((amount.getValue() != null && amount.getValue()!=0)?amount.getValue():null)
			.setNearbyNumbers(nearbyNumbers.getValue())
			.setConcept(concept.getValue())
			.setReferenceCode(referenceCode.getValue())
			.setPayMethod(payMethod.getValue())
			.setPayment((payment.getSelectedIndex() == 0)?null:(payment.getSelectedIndex() == 1))
			.setPending(pending.getValue())
			.setBatched(batched.getValue())
			.setReturned(returned.getValue())
			.setPaid(paid.getValue())
			.setSettled(settled.getValue())
			.setSecurityLevel(confidential!=null?SecurityLevel.safeValueOf(confidential.getSelectedIndex()):SecurityLevel.OFFICIAL)
			.setHasConfidentialityRole(confidentiality)
			;
	}
	
}
