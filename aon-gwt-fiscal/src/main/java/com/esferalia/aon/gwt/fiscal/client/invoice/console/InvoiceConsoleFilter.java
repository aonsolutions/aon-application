package com.esferalia.aon.gwt.fiscal.client.invoice.console;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.InvoiceCommunicationStatusBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceCommunicationTypeBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceConsoleOrderByBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceRegistryNameBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceSourceBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceModuleOptions;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.SelectElement;
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


class InvoiceConsoleFilter extends SimpleLayoutPanel implements HasValueChangeHandlers<InvoiceConsoleParams>{

	private static final String TODAS = "-- Todas --";
	private static final String PX100 = "100px";
	
	private AonIntegerBox yearBox;
	private PeriodListBox periodBox;
	private AonDateBox fromDateBox;
	private AonDateBox toDateBox;
	private ListBox confidentialBox;
	private ListBox activityBox;
	private AonTextBox seriesBox;
	private AonIntegerBox fromNumberBox;
	private AonIntegerBox toNumberBox;
	private AonTextBox referenceCodeBox;
	private InvoiceRegistryNameBox registryBox;
	private ListBox outputBox;
	private InvoiceTransactionListBox transactionBox;
	
	private ListBox withholdingBox;
	private ListBox investmentBox;
	private ListBox serviceBox;
	private ListBox accrualRegimeBox;
	private ListBox farmerRegimeBox;
	private ListBox surchargeBox;
	private ListBox rectificationTypeBox;
	
	private ListBox recordedBox;
	
	private InvoiceCommunicationTypeBox communicationTypeBox;
	private InvoiceCommunicationStatusBox communicationStatusBox;
	private InvoiceSourceBox sourceBox;

	private InvoiceConsoleOrderByBox orderByBox;
	private ListBox orderBox;
	
	InvoiceConsoleFilter( InvoiceModuleOptions opts ) {
		initYearBox(opts);
		initPeriodBox(opts);
		initFromDateBox(opts);
		initToDateBox(opts);
		initConfidentialBox(opts);
		initActivityBox(opts);
		
		initSeriesBox(opts);
		initFromNumberBox(opts);
		initToNumberBox(opts);
		initReferenceCodeBox(opts);
		
		initRecordedBox(opts);
		initRegistryBox(opts);
		initOutputBox(opts);
		initTransactionOutputBox(opts);
		initWithholdingBox(opts);
		initInvestmentBox(opts);
		initServiceBox(opts);
		initAccrualRegimeBox(opts);
		initFarmerRegimeBox(opts);
		initSurchargeBox(opts);
		initRectificationTypeBox(opts);
		
		initCommunicationTypeBox(opts);
		initCommunicationStatusBox(opts);
		initSourceBox(opts);
		
		initOrderByBox(opts);
		initOrderBox(opts);
		
		paint( opts);
	}
	
	private void paint(InvoiceModuleOptions opts) {
		AonDisplayTable mainTab = new AonDisplayTable(
				 AON.CSS.aonSearchPanel()
				,AON.CSS.aonBlockCenter()
				,AON.CSS.aonWidthAlmostAll()
			);
		
		boolean hasActivities = (opts.getConfiguration() != null && opts.getConfiguration().hasActivities());

		FlowPanel datePanel = new FlowPanel();
		datePanel.addStyleName(AON.CSS.aonNowrap());
		datePanel.add(fromDateBox);
		InlineLabel toDateLabel = new InlineLabel( AON.MSG.to());
		toDateLabel.setStyleName(AON.CSS.aonItalic());
		datePanel.add(toDateLabel);
		datePanel.add(toDateBox);

		FlowPanel numberPanel = new FlowPanel();
		numberPanel.addStyleName(AON.CSS.aonNowrap());
		numberPanel.add(fromNumberBox);
		Label toNumberLabel = new InlineLabel( AON.MSG.to());
		toNumberLabel.setStyleName(AON.CSS.aonItalic());
		numberPanel.add(toNumberLabel);
		numberPanel.add(toNumberBox);

		addPair(mainTab, AON.MSG.invoices(), outputBox);
		addPair(mainTab, AON.MSG.fiscalYear(), yearBox);
		addPair(mainTab,AON.MSG.period(),periodBox);
		addPair(mainTab,AON.MSG.date() ,datePanel);
		addPair(mainTab,AON.MSG.confidential(),confidentialBox);
		addPair(mainTab,AON.MSG.series(),seriesBox);
		addPair(mainTab,AON.MSG.number() ,numberPanel);
		addPair(mainTab,AON.MSG.reference() ,referenceCodeBox);
		addPair(mainTab,AON.MSG.titular() ,registryBox);
		if ( hasActivities ) {
			addPair(mainTab,AON.MSG.activity(),activityBox);
		}
		addPair(mainTab,AON.MSG.transaction() ,transactionBox);
		addPair(mainTab,AON.MSG.withholding() ,withholdingBox);
		addPair(mainTab,AON.MSG.rectified() ,rectificationTypeBox);
		addPair(mainTab,AON.MSG.communicated() ,communicationTypeBox);
		addPair(mainTab,AON.MSG.withStatus() ,communicationStatusBox);

		addPair(mainTab,AON.MSG.investment() ,investmentBox);
		addPair(mainTab,AON.MSG.service() ,serviceBox);
		addPair(mainTab,AON.MSG.vatAccrualPaymentAbbr() ,accrualRegimeBox);
		addPair(mainTab,AON.MSG.withholdingFarmer() ,farmerRegimeBox);
		addPair(mainTab,AON.MSG.surcharge() ,surchargeBox);

		addPair(mainTab,AON.MSG.recorded(),recordedBox);
		addPair(mainTab,AON.MSG.source() ,sourceBox);
		addPair(mainTab,AON.MSG.orderBy(),orderByBox);
		addPair(mainTab,AON.MSG.order(),orderBox);
		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.CSS.aonWidthAll());
		scrollPanel.setWidget(mainTab);
		setWidget( scrollPanel );
		
	}

	private void addPair(AonDisplayTable tab, String label, Widget widget) {
		FlowPanel container = new FlowPanel();
		container.setStyleName(AON.CSS.aonFlexColumn());
		container.addStyleName(AON.CSS.aonCustomTextBox());
		Label l = new Label(label);
		l.setStyleName(AON.CSS.aonSearchPanelLabel());
		l.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		widget.setStyleName(AON.CSS.aonCustomTextBoxInput());
		widget.setWidth("100%");
		widget.getElement().getStyle().setProperty("padding", "0");
		container.add(l);
		container.add(widget);
		tab.addRow().addCell(container);
	}

	private void initYearBox(InvoiceModuleOptions opts) {
		yearBox = new AonIntegerBox();
		yearBox.addStyleName(AON.CSS.aonMarginLeft());
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(5);
		yearBox.addValueChangeHandler(event -> {
			fillDates();
			fire(opts);
		});
	}

	private void initPeriodBox(InvoiceModuleOptions opts) {
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
	
	private void initFromDateBox(InvoiceModuleOptions opts) {
		fromDateBox = new AonDateBox();
		fromDateBox.addValueChangeHandler(event -> fire(opts));
	}
	
	private void initToDateBox(InvoiceModuleOptions opts) {
		toDateBox = new AonDateBox();
		toDateBox.addValueChangeHandler(event -> fire(opts));
	}

	private void initConfidentialBox(InvoiceModuleOptions opts) {
		confidentialBox = new ListBox();
		confidentialBox.setWidth(PX100);
		confidentialBox.addItem(" Todas ");
		confidentialBox.addItem( "Facturas NO confidenciales" );
		confidentialBox.addItem( "Facturas confidenciales" );
		confidentialBox.setSelectedIndex(0);
		confidentialBox.addChangeHandler(event -> fire(opts));
	}

	private void initActivityBox(InvoiceModuleOptions opts) {
		if (opts.getConfiguration() != null && opts.getConfiguration().hasActivities()) {
			activityBox = new ListBox();
			activityBox.setWidth("150px");
			activityBox.addItem(TODAS, "");
			activityBox.addItem("-- Sin actividad --", "-1");
			activityBox.setSelectedIndex(0);
			int i = 2;
			for (EnterpriseActivity ea : opts.getConfiguration().getActivities()) {
				activityBox.addItem(ea.getDescription(), AonNumberUtils.toString( ea.getId()));
				if (ea.isPrincipal()) {
					activityBox.setItemText(i, ea.getDescription() + AonStringUtils.ASTERISK);
				}
				i++;
			}
			activityBox.addChangeHandler(event -> fire(opts));
		}
	}

	private void initSeriesBox(InvoiceModuleOptions opts) {
		seriesBox = new AonTextBox();
		seriesBox.addStyleName(AON.CSS.aonMarginLeft());
		seriesBox.setMaxLength(5);
		seriesBox.setVisibleLength(6);
		seriesBox.addValueChangeHandler(event -> fire(opts));
	}
	
	private void initFromNumberBox(InvoiceModuleOptions opts) {
		fromNumberBox = new AonIntegerBox();
		fromNumberBox.addStyleName(AON.CSS.aonMarginLeft());
		fromNumberBox.setMaxLength(8);
		fromNumberBox.setVisibleLength(8);
		fromNumberBox.addValueChangeHandler(event -> fire(opts));
	}
	
	private void initToNumberBox(InvoiceModuleOptions opts) {
		toNumberBox = new AonIntegerBox();
		toNumberBox.addStyleName(AON.CSS.aonMarginLeft());
		toNumberBox.setMaxLength(8);
		toNumberBox.setVisibleLength(8);
		toNumberBox.addValueChangeHandler(event -> fire(opts));
	}
	
	private void initReferenceCodeBox(InvoiceModuleOptions opts) {
		referenceCodeBox = new AonTextBox();
		referenceCodeBox.addStyleName(AON.CSS.aonMarginLeft());
		referenceCodeBox.setMaxLength(15);
		referenceCodeBox.setVisibleLength(18);
		referenceCodeBox.addValueChangeHandler(event -> fire(opts));	
	}

	private void initOutputBox(InvoiceModuleOptions opts) {
		outputBox = new ListBox();
		outputBox.addItem(TODAS);
		outputBox.addItem(AON.MSG.inputInvoices());
		outputBox.addItem(AON.MSG.outputInvoices());
		outputBox.addChangeHandler(event -> fire(opts));
	}

	private void initTransactionOutputBox(InvoiceModuleOptions opts) {
		transactionBox = new InvoiceTransactionListBox(TODAS);
		transactionBox.setWidth(PX100);
		transactionBox.addChangeHandler(event -> fire(opts));
	}
	
	private void initRecordedBox(InvoiceModuleOptions opts) {
		recordedBox = new ListBox();
		recordedBox.addItem(TODAS);
		recordedBox.addItem(AON.MSG.no());
		recordedBox.addItem(AON.MSG.yes());
		recordedBox.addChangeHandler(event -> fire(opts));
	}

	private void initRegistryBox(InvoiceModuleOptions opts) {
		registryBox = new InvoiceRegistryNameBox(opts);
		registryBox.setRequired(false);
		registryBox.addSelectionHandler(event -> fire(opts));
	}
	
	private void initWithholdingBox(InvoiceModuleOptions opts) {
		withholdingBox = new ListBox();
		withholdingBox.setWidth(PX100);
		withholdingBox.addItem(TODAS);
		withholdingBox.addItem(AON.MSG.no());
		withholdingBox.addItem(AON.MSG.yes());
		withholdingBox.addChangeHandler(event -> fire(opts));
	}

	private void initInvestmentBox(InvoiceModuleOptions opts) {
		investmentBox = new ListBox();
		investmentBox.setWidth(PX100);
		investmentBox.addItem(TODAS);
		investmentBox.addItem(AON.MSG.commonAsset());
		investmentBox.addItem(AON.MSG.investAsset());
		investmentBox.addChangeHandler(event -> fire(opts));
	}
	
	private void initAccrualRegimeBox(InvoiceModuleOptions opts) {
		accrualRegimeBox = new ListBox();
		accrualRegimeBox.addItem(TODAS);
		accrualRegimeBox.addItem(AON.MSG.no());
		accrualRegimeBox.addItem(AON.MSG.yes());
		accrualRegimeBox.addChangeHandler(event -> fire(opts));
	}
	
	private void initFarmerRegimeBox(InvoiceModuleOptions opts) {
		farmerRegimeBox = new ListBox();
		farmerRegimeBox.addItem(TODAS);
		farmerRegimeBox.addItem(AON.MSG.no());
		farmerRegimeBox.addItem(AON.MSG.yes());
		farmerRegimeBox.addChangeHandler(event -> {
			transactionBox.setValue(InvoiceTransactionType.NATIONAL);
			surchargeBox.setSelectedIndex(0);
			fire(opts);
		});
	}

	private void initServiceBox(InvoiceModuleOptions opts) {
		serviceBox = new ListBox();
		serviceBox.addItem(TODAS);
		serviceBox.addItem(AON.MSG.no());
		serviceBox.addItem(AON.MSG.yes());
		serviceBox.addChangeHandler(event -> fire(opts));
	}
	
	private void initSurchargeBox(InvoiceModuleOptions opts) {
		surchargeBox = new ListBox();
		surchargeBox.addItem(TODAS);
		surchargeBox.addItem(AON.MSG.no());
		surchargeBox.addItem(AON.MSG.yes());
		surchargeBox.addChangeHandler(event -> {
			transactionBox.setValue(InvoiceTransactionType.NATIONAL);
			farmerRegimeBox.setSelectedIndex(0);
			fire(opts);
		});
	}
	
	private void initRectificationTypeBox(InvoiceModuleOptions opts) {
		rectificationTypeBox = new ListBox();
		rectificationTypeBox.setWidth(PX100);
		rectificationTypeBox.addItem(TODAS);
		rectificationTypeBox.addItem("Ni rectificativa ni rectificada");
		rectificationTypeBox.addItem(RectificationType.NORMAL_RECTIFIER.getDescription());
		rectificationTypeBox.addItem(RectificationType.SPECIAL_RECTIFIER.getDescription());
		rectificationTypeBox.getElement().<SelectElement>cast().getOptions().getItem(3).setDisabled(true);
		rectificationTypeBox.addItem(RectificationType.RECTIFIED.getDescription());
		rectificationTypeBox.addChangeHandler(event -> fire(opts));
	}


	private void initCommunicationTypeBox(InvoiceModuleOptions opts) {
		communicationTypeBox = new InvoiceCommunicationTypeBox(TODAS);
		communicationTypeBox.setWidth(PX100);
		communicationTypeBox.addChangeHandler(event -> fire(opts));
	}
	
	private void initCommunicationStatusBox(InvoiceModuleOptions opts) {
		communicationStatusBox = new InvoiceCommunicationStatusBox(TODAS);
		communicationStatusBox.setWidth(PX100);
		communicationStatusBox.addChangeHandler(event -> fire(opts));
	}

	private void initSourceBox(InvoiceModuleOptions opts) {
		sourceBox = new InvoiceSourceBox();
		sourceBox.setWidth(PX100);
		sourceBox.addChangeHandler(event -> fire(opts));
	}

	private void initOrderByBox(InvoiceModuleOptions opts) {
		orderByBox = new InvoiceConsoleOrderByBox();
		orderByBox.setWidth(PX100);
		orderByBox.addChangeHandler(event -> fire(opts));
	}
	
	private void initOrderBox(InvoiceModuleOptions opts) {
		orderBox = new ListBox();
		orderBox.addItem(AON.MSG.ascendingOrder());
		orderBox.addItem(AON.MSG.descendingOrder());
		orderBox.addChangeHandler(event -> fire(opts));
	}

	private InvoiceConsoleParams getWidgetParams(InvoiceModuleOptions opts) {
		InvoiceConsoleParams params = new InvoiceConsoleParams()
			.setDomain(opts.getDomain())
			.setFromDate(fromDateBox.getValue())
			.setToDate(toDateBox.getValue())
		;
		if (registryBox.getValue() != null) {
			params.setRegistry( registryBox.getValue().getId() );
		}
		if (opts.getConfiguration() != null && opts.getConfiguration().hasActivities() && activityBox.getSelectedIndex() > 0) {
			params.setActivity( AonNumberUtils.toInteger( activityBox.getSelectedValue()));
		}
		
		if (AonStringUtils.isNotBlank(seriesBox.getValue())) {
			params.setSeries( seriesBox.getValue() );
		}
		
		if (fromNumberBox.getValue() != null) {
			params.setFromNumber( fromNumberBox.getValue() );
		}
		
		if (toNumberBox.getValue() != null) {
			params.setToNumber( toNumberBox.getValue() );
		}
		
		if (AonStringUtils.isNotBlank(referenceCodeBox.getValue())) {
			params.setReferenceCode( referenceCodeBox.getValue() );
		}
		
		if (transactionBox.getValue() != null) {
			params.setTransactionType(transactionBox.getValue());
		}
			
		if (confidentialBox.getSelectedIndex() == 1) params.setSecurityLevel(SecurityLevel.OFFICIAL);
		if (confidentialBox.getSelectedIndex() == 2) params.setSecurityLevel(SecurityLevel.CONFIDENTIAL);

		if (outputBox.getSelectedIndex() == 1) params.setOutput(false);
		if (outputBox.getSelectedIndex() == 2) params.setOutput(true);
			
		if (surchargeBox.getSelectedIndex() == 1) params.setSurcharge(false);
		if (surchargeBox.getSelectedIndex() == 2) params.setSurcharge(true);

		if (rectificationTypeBox.getSelectedIndex() > 0) {
			params.setRectificationType(RectificationType.values()[rectificationTypeBox.getSelectedIndex() - 1]);	
		}
			
		if (withholdingBox.getSelectedIndex() == 1) params.setWithholding(false);
		if (withholdingBox.getSelectedIndex() == 2) params.setWithholding(true);

		if (investmentBox.getSelectedIndex() == 1) params.setInvestment(false);
		if (investmentBox.getSelectedIndex() == 2) params.setInvestment(true);
			
		if (accrualRegimeBox.getSelectedIndex() == 1) params.setAccrualRegime(false);
		if (accrualRegimeBox.getSelectedIndex() == 2) params.setAccrualRegime(true);

		if (serviceBox.getSelectedIndex() == 1) params.setService(false);
		if (serviceBox.getSelectedIndex() == 2) params.setService(true);
			
		if (recordedBox.getSelectedIndex() == 1) params.setRecorded(false);
		if (recordedBox.getSelectedIndex() == 2) params.setRecorded(true);
		
		if (communicationTypeBox.getValue() != null) {
			params.setCommunicationType( communicationTypeBox.getValue() );
		}
		if (communicationStatusBox.getValue() != null) {
			params.setCommunicationStatus( communicationStatusBox.getValue() );
		}
		if (sourceBox.getValue() != null) {
			params.setSource( sourceBox.getValue() );
		}
		
		params.setOrderBy( orderByBox.getValue() );
		params.setDescending( orderBox.getSelectedIndex() == 1 );

		return params;
	}


	private void fire(final InvoiceModuleOptions opts) {
		ValueChangeEvent.<InvoiceConsoleParams>fire( InvoiceConsoleFilter.this, getWidgetParams( opts ) ); 
	}

	void initialize(InvoiceModuleOptions opts) {
		yearBox.setValue(DateUtils.getYear(),false);
		periodBox.setSelectedIndex(0);
		fillDates();
		if (opts.getConfiguration() != null && opts.getConfiguration().hasActivities()) {
			activityBox.setSelectedIndex(0);
		}
		fire(opts);
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<InvoiceConsoleParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
}
