package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;


import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.esferalia.aon.occam.api.model.type.AmortizationDetailStatus;
import com.esferalia.aon.occam.api.model.type.Period;
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


class AccountingAmortizationFilterPanel extends SimpleLayoutPanel implements HasValueChangeHandlers<AmortizationParams>{

	private static final String ALL = "-- Todos --";
	
	private AonIntegerBox yearBox;
	private PeriodListBox periodBox;
	private AonDateBox fromDateBox;
	private AonDateBox toDateBox;
	private AonAccountBox accountBox;
	private ListBox statusBox;
	
	AccountingAmortizationFilterPanel( AmortizationModuleOptions opts ) {
		initYearBox(opts);
		initPeriodBox(opts);
		initFromDateBox(opts);
		initToDateBox(opts);
		initAccountBox(opts);
		initStatusBox(opts);
		
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

		addPair(container, AON.MSG.fiscalYear(), yearBox);
		addPair(container,AON.MSG.period(),periodBox);
		addPair(container,AON.MSG.date() ,datePanel);
		addPair(container,AON.MSG.investment() ,statusBox);
		addPair(container,AON.MSG.titular() ,accountBox);
		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.CSS.aonScrollArea());
		scrollPanel.addStyleName(AON.CSS.aonWidthAll());
		scrollPanel.setWidget(container);
		setWidget( scrollPanel );
		
	}

	private void addPair(FlowPanel container, String label, Widget widget) {
		FlowPanel blockContainer = new FlowPanel();
		blockContainer.getElement().getStyle().setProperty("display", "flex");
		blockContainer.getElement().getStyle().setProperty("flex-grow", "0");
		Label l = new Label(label);
		l.setStyleName(AON.CSS.aonSearchPanelLabel());
		l.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		widget.addStyleName(AON.CSS.aonCustomTextBoxInput());
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

	private void initAccountBox(AmortizationModuleOptions opts) {
		accountBox = new AonAccountBox(opts.getOccam());
		accountBox.setRequired(false);
		accountBox.addSelectionHandler(event -> fire(opts));
	}
	
	private void initStatusBox(AmortizationModuleOptions opts) {
		statusBox = new ListBox();
		statusBox.addItem(ALL);
		statusBox.addItem(AmortizationDetailStatus.PENDING.getDescription());
		statusBox.addItem(AmortizationDetailStatus.SCORED.getDescription());
		statusBox.addItem(AmortizationDetailStatus.BLOCKED.getDescription());
		statusBox.addChangeHandler(event -> fire(opts));
	}

	AmortizationParams getWidgetParams(AmortizationModuleOptions opts) {
		AmortizationParams params = new AmortizationParams()
			.setDomain(opts.getDomain())
			.setInitialDate(fromDateBox.getValue())
			.setDeadline(toDateBox.getValue())
		;
		if (accountBox.getId() != null) {
			params.setAllocationAccount( accountBox.getId() );
		}
		
		params.setLimit( 50 ); 
		return params;
	}


	private void fire(final AmortizationModuleOptions opts) {
		ValueChangeEvent.<AmortizationParams>fire( AccountingAmortizationFilterPanel.this, getWidgetParams( opts ) ); 
	}

	void initialize(AmortizationModuleOptions opts) {
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
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<AmortizationParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
}
