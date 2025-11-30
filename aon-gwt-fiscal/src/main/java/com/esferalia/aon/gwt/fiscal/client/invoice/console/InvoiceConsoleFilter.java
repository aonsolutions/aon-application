package com.esferalia.aon.gwt.fiscal.client.invoice.console;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.InvoiceCommunicationStatusBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceCommunicationTypeBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;


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
	private AonAccountingRegistryBox registryBox;
	private ListBox outputBox;
	private InvoiceTransactionListBox transactionBox;
	
	private ListBox withholdingBox = new ListBox();
	private ListBox investmentBox = new ListBox();
	private ListBox serviceBox = new ListBox();
	private ListBox accrualRegimeBox = new ListBox();
	private ListBox farmerRegimeBox = new ListBox();
	private ListBox surchargeBox = new ListBox();
	private ListBox rectificationTypeBox = new ListBox();
	
	private ListBox recordedBox = new ListBox();
	
	private InvoiceCommunicationTypeBox communicationTypeBox = new InvoiceCommunicationTypeBox();
	private InvoiceCommunicationStatusBox communicationStatusBox = new InvoiceCommunicationStatusBox();

	
	InvoiceConsoleFilter( InvoiceConsoleModuleOptions opts ) {
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
		
		paint( opts);
	}
	
	private Label filterLabel( String label ) {
		Label l = new Label(label);
		l.setStyleName(AON.CSS.aonSearchPanelLabel());
		l.addStyleName(AON.CSS.aonMarginLeftSep());
		return l;
	}
	
	private void paint(InvoiceConsoleModuleOptions opts) {
		AonDisplayTable mainTab = new AonDisplayTable(
				 AON.CSS.aonSearchPanel()
				,AON.CSS.aonBlockCenter()
				,AON.CSS.aonWidthAlmostAll()
			);
		boolean hasActivities = (opts.getConfiguration() != null && opts.getConfiguration().hasActivities());
		mainTab.addRow().addCell( 
			new AonDisplayTable().addRow()
				.addCell(filterLabel( AON.MSG.invoices()) )
				.addCell(outputBox)
				.addCell(filterLabel( AON.MSG.fiscalYear()))
				.addCell(yearBox)
				.addCell(filterLabel( AON.MSG.period()))
				.addCell(periodBox)
				.addCell(filterLabel( AON.MSG.date()) )
				.addCell(fromDateBox)
				.addCell(filterLabel( AON.MSG.to()),AON.CSS.aonItalic())
				.addCell(toDateBox)
				.addCell(filterLabel( AON.MSG.confidential()))
				.addCell(confidentialBox)
				.addCellIf(hasActivities,filterLabel( AON.MSG.activity()))
				.addCellIf(hasActivities,activityBox)
				.addCell(filterLabel( AON.MSG.recorded()))
				.addCell(recordedBox)
				.addCell(new Label(), AON.CSS.aonFlexGrow1())
		);
			
		mainTab.addRow().addCell( 
			new AonDisplayTable().addRow()
				.addCell(filterLabel( AON.MSG.series()))
				.addCell(seriesBox)
				.addCell(filterLabel( AON.MSG.number()) )
				.addCell(fromNumberBox)
				.addCell(filterLabel( AON.MSG.to()),AON.CSS.aonItalic())
				.addCell(toNumberBox)
				.addCell(filterLabel( AON.MSG.reference()) )
				.addCell(referenceCodeBox)
				.addCell(filterLabel( AON.MSG.titular()) )
				.addCell(registryBox)
				.addCell(new Label(), AON.CSS.aonFlexGrow1())
		);
		
		mainTab.addRow().addCell( 
			new AonDisplayTable().addRow()
				.addCell(filterLabel( AON.MSG.transaction()) )
				.addCell(transactionBox)
				.addCell(filterLabel( AON.MSG.withholding()) )
				.addCell(withholdingBox)
				.addCell(filterLabel( AON.MSG.rectified()) )
				.addCell(rectificationTypeBox)
				.addCell(new Label(), AON.CSS.aonFlexGrow1())
				.addCell(filterLabel( AON.MSG.communicated()) )
				.addCell(communicationTypeBox)
				.addCell(filterLabel( AON.MSG.withStatus()) )
				.addCell(communicationStatusBox)
		);
		
		mainTab.addRow().addCell( 
			new AonDisplayTable().addRow()
				.addCell(filterLabel( AON.MSG.investment()) )
				.addCell(investmentBox)
				.addCell(filterLabel( AON.MSG.service()) )
				.addCell(serviceBox)
				.addCell(filterLabel( AON.MSG.vatAccrualPaymentAbbr()) )
				.addCell(accrualRegimeBox)
				.addCell(filterLabel( AON.MSG.withholdingFarmer()) )
				.addCell(farmerRegimeBox)
				.addCell(filterLabel( AON.MSG.surcharge()) )
				.addCell(surchargeBox)
				.addCell(new Label(), AON.CSS.aonFlexGrow1())
		);

		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.CSS.aonWidthAll());
		scrollPanel.setWidget(mainTab);
		setWidget( scrollPanel );
		
	}

	private void initYearBox(InvoiceConsoleModuleOptions opts) {
		yearBox = new AonIntegerBox();
		yearBox.addStyleName(AON.CSS.aonMarginLeft());
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(5);
		yearBox.addValueChangeHandler(event -> {
			fillDates();
			fire(opts);
		});
	}

	private void initPeriodBox(InvoiceConsoleModuleOptions opts) {
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
	
	private void initFromDateBox(InvoiceConsoleModuleOptions opts) {
		fromDateBox = new AonDateBox();
		fromDateBox.addValueChangeHandler(event -> fire(opts));
	}
	
	private void initToDateBox(InvoiceConsoleModuleOptions opts) {
		toDateBox = new AonDateBox();
		toDateBox.addValueChangeHandler(event -> fire(opts));
	}

	private void initConfidentialBox(InvoiceConsoleModuleOptions opts) {
		confidentialBox = new ListBox();
		confidentialBox.setWidth(PX100);
		confidentialBox.addItem(" Todas ");
		confidentialBox.addItem( "Facturas NO confidenciales" );
		confidentialBox.addItem( "Facturas confidenciales" );
		confidentialBox.setSelectedIndex(0);
		confidentialBox.addChangeHandler(event -> fire(opts));
	}

	private void initActivityBox(InvoiceConsoleModuleOptions opts) {
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

	private void initSeriesBox(InvoiceConsoleModuleOptions opts) {
		seriesBox = new AonTextBox();
		seriesBox.addStyleName(AON.CSS.aonMarginLeft());
		seriesBox.setMaxLength(5);
		seriesBox.setVisibleLength(6);
		seriesBox.addValueChangeHandler(event -> fire(opts));
	}
	
	private void initFromNumberBox(InvoiceConsoleModuleOptions opts) {
		fromNumberBox = new AonIntegerBox();
		fromNumberBox.addStyleName(AON.CSS.aonMarginLeft());
		fromNumberBox.setMaxLength(8);
		fromNumberBox.setVisibleLength(8);
		fromNumberBox.addValueChangeHandler(event -> fire(opts));
	}
	
	private void initToNumberBox(InvoiceConsoleModuleOptions opts) {
		toNumberBox = new AonIntegerBox();
		toNumberBox.addStyleName(AON.CSS.aonMarginLeft());
		toNumberBox.setMaxLength(8);
		toNumberBox.setVisibleLength(8);
		toNumberBox.addValueChangeHandler(event -> fire(opts));
	}
	
	private void initReferenceCodeBox(InvoiceConsoleModuleOptions opts) {
		referenceCodeBox = new AonTextBox();
		referenceCodeBox.addStyleName(AON.CSS.aonMarginLeft());
		referenceCodeBox.setMaxLength(15);
		referenceCodeBox.setVisibleLength(18);
		referenceCodeBox.addValueChangeHandler(event -> fire(opts));	
	}

	private void initOutputBox(InvoiceConsoleModuleOptions opts) {
		outputBox = new ListBox();
		outputBox.addItem(TODAS);
		outputBox.addItem(AON.MSG.inputInvoices());
		outputBox.addItem(AON.MSG.outputInvoices());
		outputBox.addChangeHandler(event -> fire(opts));
	}

	private void initTransactionOutputBox(InvoiceConsoleModuleOptions opts) {
		transactionBox = new InvoiceTransactionListBox(TODAS);
		transactionBox.setWidth(PX100);
		transactionBox.addChangeHandler(event -> fire(opts));
	}
	
	private void initRecordedBox(InvoiceConsoleModuleOptions opts) {
		recordedBox = new ListBox();
		recordedBox.addItem(TODAS);
		recordedBox.addItem(AON.MSG.no());
		recordedBox.addItem(AON.MSG.yes());
		recordedBox.addChangeHandler(event -> fire(opts));
	}

	private void initRegistryBox(InvoiceConsoleModuleOptions opts) {
		registryBox = new AonAccountingRegistryBox(opts,true);
		registryBox.setRequired(false);
		registryBox.addSelectionHandler(event -> fire(opts));
	}
	
	private void initWithholdingBox(InvoiceConsoleModuleOptions opts) {
		withholdingBox = new ListBox();
		withholdingBox.setWidth(PX100);
		withholdingBox.addItem(TODAS);
		withholdingBox.addItem(AON.MSG.no());
		withholdingBox.addItem(AON.MSG.yes());
		withholdingBox.addChangeHandler(event -> fire(opts));
	}

	private void initInvestmentBox(InvoiceConsoleModuleOptions opts) {
		investmentBox = new ListBox();
		investmentBox.setWidth(PX100);
		investmentBox.addItem(TODAS);
		investmentBox.addItem(AON.MSG.commonAsset());
		investmentBox.addItem(AON.MSG.investAsset());
		investmentBox.addChangeHandler(event -> fire(opts));
	}
	
	private void initAccrualRegimeBox(InvoiceConsoleModuleOptions opts) {
		accrualRegimeBox = new ListBox();
		accrualRegimeBox.addItem(TODAS);
		accrualRegimeBox.addItem(AON.MSG.no());
		accrualRegimeBox.addItem(AON.MSG.yes());
		accrualRegimeBox.addChangeHandler(event -> fire(opts));
	}
	
	private void initFarmerRegimeBox(InvoiceConsoleModuleOptions opts) {
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

	private void initServiceBox(InvoiceConsoleModuleOptions opts) {
		serviceBox = new ListBox();
		serviceBox.addItem(TODAS);
		serviceBox.addItem(AON.MSG.no());
		serviceBox.addItem(AON.MSG.yes());
		serviceBox.addChangeHandler(event -> fire(opts));
	}
	
	private void initSurchargeBox(InvoiceConsoleModuleOptions opts) {
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
	
	private void initRectificationTypeBox(InvoiceConsoleModuleOptions opts) {
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


	private void initCommunicationTypeBox(InvoiceConsoleModuleOptions opts) {
		communicationTypeBox = new InvoiceCommunicationTypeBox(TODAS);
		communicationTypeBox.setWidth(PX100);
		communicationTypeBox.addChangeHandler(event -> fire(opts));
	}
	
	private void initCommunicationStatusBox(InvoiceConsoleModuleOptions opts) {
		communicationStatusBox = new InvoiceCommunicationStatusBox(TODAS);
		communicationStatusBox.setWidth(PX100);
		communicationStatusBox.addChangeHandler(event -> fire(opts));
	}
	
	private InvoiceConsoleParams getWidgetParams(InvoiceConsoleModuleOptions opts) {
		InvoiceConsoleParams params = new InvoiceConsoleParams()
			.setDomain(opts.getDomain())
			.setRegistry(registryBox.getId())
			.setFromDate(fromDateBox.getValue())
			.setToDate(toDateBox.getValue())
		;
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

		return params;
	}


	private void fire(final InvoiceConsoleModuleOptions opts) {
		ValueChangeEvent.<InvoiceConsoleParams>fire( InvoiceConsoleFilter.this, getWidgetParams( opts ) ); 
	}

	void initialize(InvoiceConsoleModuleOptions opts) {
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
