package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;


import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.InvoiceRegistryNameBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


class AmortizationInvoiceSelectionFilterPanel extends SimpleLayoutPanel implements HasValueChangeHandlers<InvoiceConsoleParams>{

	private static final String TODAS = "-- Todas --";
	private static final String PX100 = "100px";
	
	private AonIntegerBox yearBox;
	private PeriodListBox periodBox;
	private AonDateBox fromDateBox;
	private AonDateBox toDateBox;
	private AonTextBox referenceCodeBox;
	private InvoiceRegistryNameBox registryBox;
	private ListBox outputBox;
	
	private ListBox investmentBox;
	private ListBox alreadyBindedBox;
	
	AmortizationInvoiceSelectionFilterPanel( AmortizationModuleOptions opts ) {
		initYearBox(opts);
		initPeriodBox(opts);
		initFromDateBox(opts);
		initToDateBox(opts);
		initReferenceCodeBox(opts);
		initRegistryBox(opts);
		initOutputBox(opts);
		initInvestmentBox(opts);
		initAlreadyBindedBox(opts);
		
		paint( opts);
	}
	
	private void paint(AmortizationModuleOptions opts) {
		FlowPanel container = new FlowPanel();
		container.getElement().getStyle().setProperty("display", "flex");
		container.getElement().getStyle().setProperty("flexWrap", "wrap");
		container.getElement().getStyle().setProperty("alignItems", "flex-start");
		container.getElement().getStyle().setProperty("gap", "8px");
		
		FlowPanel datePanel = new FlowPanel();
		datePanel.addStyleName(AON.CSS.aonNowrap());
		datePanel.add(fromDateBox);
		InlineLabel toDateLabel = new InlineLabel( AON.MSG.to());
		toDateLabel.setStyleName(AON.CSS.aonItalic());
		toDateLabel.addStyleName(AON.CSS.aonMarginLeft());
		toDateLabel.addStyleName(AON.CSS.aonMarginRight());
		datePanel.add(toDateLabel);
		datePanel.add(toDateBox);

		addPair(container, AON.MSG.invoices(), outputBox);
		addPair(container, AON.MSG.fiscalYear(), yearBox);
		addPair(container,AON.MSG.period(),periodBox);
		addPair(container,AON.MSG.date() ,datePanel);
		addPair(container,AON.MSG.reference() ,referenceCodeBox);
		addPair(container,AON.MSG.titular() ,registryBox);
		addPair(container,AON.MSG.investment() ,investmentBox);
		addPair(container,AON.MSG.status() ,alreadyBindedBox);
		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.CSS.aonScrollArea());
		scrollPanel.addStyleName(AON.CSS.aonWidthAll());
		scrollPanel.setWidget(container);
		setWidget( scrollPanel );
		
	}
	private void addPair(FlowPanel container, String label, Widget widget) {
		FlowPanel blockContainer = new FlowPanel();
		blockContainer.setStyleName(AON.CSS.aonAlignItemsCenter());
		blockContainer.getElement().getStyle().setProperty("display", "flex");
		blockContainer.getElement().getStyle().setProperty("flex-grow", "0");
		Label l = new Label(label);
		l.setStyleName(AON.CSS.aonNowrap());
		l.addStyleName(AON.CSS.aonBold());
		l.addStyleName(AON.CSS.aonMarginRight());
		widget.setWidth("100%");
		widget.getElement().getStyle().setProperty("padding", "0");
		blockContainer.add(l);
		blockContainer.add(widget);
		container.add(blockContainer);
	}

	private void initYearBox(AmortizationModuleOptions opts) {
		yearBox = new AonIntegerBox();
		yearBox.addStyleName(AON.CSS.aonMarginLeft());
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(5);
		yearBox.addValueChangeHandler(event -> {
			fillDates();
			fire(opts);
		});
	}

	private void initPeriodBox(AmortizationModuleOptions opts) {
		periodBox = new PeriodListBox();
		periodBox.addStyleName(AON.CSS.aonMarginLeft());
		periodBox.addChangeHandler(event -> {
			fillDates();
			fire(opts);
		});
	}

	private void fillDates() {
		Integer y = yearBox.getValue();
		Period p = periodBox.getValue();
		if ( y == null) {
			fromDateBox.setValue(null,false);
			toDateBox.setValue(null,false);
		} else {
			if (p == null) {
				fromDateBox.setValue(DateUtils.getFirstDayOfYear(y - 1900),false);
				toDateBox.setValue(DateUtils.getLastDayOfYear(y - 1900),false);
			} else {
				fromDateBox.setValue(DateUtils.getDate(p.getStartMonth(), y),false);
				toDateBox.setValue(DateUtils.getLastDayOfMonth(DateUtils.getDate(p.getDueMonth(), y)),false);
			}
		}
	}
	
	private void checkDatesAndFire(AmortizationModuleOptions opts) {
		Date from = fromDateBox.getValue();
		Date to = toDateBox.getValue();
		if (from != null && to != null) {
			int fromYear = DateUtils.getYear( from );
			int toYear = DateUtils.getYear( to );
			if ( fromYear == toYear ) {
				yearBox.setValue(fromYear,false);
			} else {
				yearBox.setValue(null,false);
				periodBox.setSelectedIndex(0);
			}
		} else {
			yearBox.setValue(null,false);
			periodBox.setSelectedIndex(0);
		}
		fire(opts);
	}
	
	private void initFromDateBox(AmortizationModuleOptions opts) {
		fromDateBox = new AonDateBox();
		fromDateBox.addValueChangeHandler(event -> checkDatesAndFire(opts));
	}
	
	private void initToDateBox(AmortizationModuleOptions opts) {
		toDateBox = new AonDateBox();
		toDateBox.addValueChangeHandler(event -> checkDatesAndFire(opts));
	}

	private void initReferenceCodeBox(AmortizationModuleOptions opts) {
		referenceCodeBox = new AonTextBox();
		referenceCodeBox.addStyleName(AON.CSS.aonMarginLeft());
		referenceCodeBox.setMaxLength(15);
		referenceCodeBox.setVisibleLength(18);
		referenceCodeBox.addValueChangeHandler(event -> fire(opts));	
	}

	private void initOutputBox(AmortizationModuleOptions opts) {
		outputBox = new ListBox();
		outputBox.addItem(TODAS);
		outputBox.addItem(AON.MSG.inputInvoices());
		outputBox.addItem(AON.MSG.outputInvoices());
		outputBox.addChangeHandler(event -> fire(opts));
	}

	private void initRegistryBox(AmortizationModuleOptions opts) {
		registryBox = new InvoiceRegistryNameBox(opts);
		registryBox.setVisibleLength(50);
		registryBox.setMaxLength(50);
		registryBox.setRequired(false);
		registryBox.addSelectionHandler(event -> fire(opts));
	}
	
	private void initInvestmentBox(AmortizationModuleOptions opts) {
		investmentBox = new ListBox();
		investmentBox.setWidth(PX100);
		investmentBox.addItem(TODAS);
		investmentBox.addItem(AON.MSG.commonAsset());
		investmentBox.addItem(AON.MSG.investAsset());
		investmentBox.addChangeHandler(event -> fire(opts));
	}
	
	private void initAlreadyBindedBox(AmortizationModuleOptions opts) {
		alreadyBindedBox = new ListBox();
		alreadyBindedBox.setWidth(PX100);
		alreadyBindedBox.addItem(TODAS);
		alreadyBindedBox.addItem(AON.MSG.alreadyLinked());
		alreadyBindedBox.addItem(AON.MSG.notYetLinked());
		alreadyBindedBox.setSelectedIndex(2); 
		alreadyBindedBox.addChangeHandler(event -> fire(opts));
	}

	InvoiceConsoleParams getWidgetParams(AmortizationModuleOptions opts) {
		InvoiceConsoleParams params = new InvoiceConsoleParams()
			.setDomain(opts.getDomain())
			.setFromDate(fromDateBox.getValue())
			.setToDate(toDateBox.getValue())
		;
		if (registryBox.getValue() != null) {
			params.setRegistry( registryBox.getValue().getId() );
		}
		
		if (AonStringUtils.isNotBlank(referenceCodeBox.getValue())) {
			params.setReferenceCode( referenceCodeBox.getValue() );
		}
		
		if (outputBox.getSelectedIndex() == 1) params.setOutput(false);
		if (outputBox.getSelectedIndex() == 2) params.setOutput(true);
			
		if (investmentBox.getSelectedIndex() == 1) params.setInvestment(false);
		if (investmentBox.getSelectedIndex() == 2) params.setInvestment(true);
		
		if (alreadyBindedBox.getSelectedIndex() == 1) params.setAmortizationBinded(true);
		if (alreadyBindedBox.getSelectedIndex() == 2) params.setAmortizationBinded(false);

		params.setLimit( 15 ); 
		return params;
	}


	private void fire(final AmortizationModuleOptions opts) {
		ValueChangeEvent.<InvoiceConsoleParams>fire( AmortizationInvoiceSelectionFilterPanel.this, getWidgetParams( opts ) ); 
	}

	void initialize(AmortizationModuleOptions opts) {
		outputBox.setSelectedIndex(2); // Output invoices - Emitidas
		Date toDate = new Date();
		Date fromDate = DateUtils.addMonths2Date(new Date(), -4);
		fromDateBox.setValue(fromDate,false);
		toDateBox.setValue(toDate,false);
		int fromYear = DateUtils.getYear( fromDate );
		int toYear = DateUtils.getYear( toDate );
		if ( fromYear == toYear ) {
			yearBox.setValue(fromYear,false);
		}
		fire(opts);
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<InvoiceConsoleParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
}
