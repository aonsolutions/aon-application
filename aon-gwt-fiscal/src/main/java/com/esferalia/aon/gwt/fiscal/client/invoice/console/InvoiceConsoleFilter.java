package com.esferalia.aon.gwt.fiscal.client.invoice.console;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.RectificationType;
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
	private AonAccountingRegistryBox registryBox;
	private ListBox outputBox;
	private InvoiceTransactionListBox transactionBox;
	
	private ListBox investmentBox = new ListBox();
	private ListBox serviceBox = new ListBox();
	private ListBox accrualRegimeBox = new ListBox();
	private ListBox farmerRegimeBox = new ListBox();
	private ListBox surchargeBox = new ListBox();
	private ListBox rectificationTypeBox = new ListBox();

	
	InvoiceConsoleFilter( InvoiceConsoleModuleOptions opts ) {
		initYearBox(opts);
		initPeriodBox(opts);
		initFromDateBox(opts);
		initToDateBox(opts);
		initConfidentialBox(opts);
		initActivityBox(opts);
		initRegistryBox(opts);
		initOutputBox(opts);
		initTransactionOutputBox(opts);
		initInvestmentBox(opts);
		initServiceBox(opts);
		initAccrualRegimeBox(opts);
		initFarmerRegimeBox(opts);
		initSurchargeBox(opts);
		initRectificationTypeBox(opts);
		
		paint( opts);
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
				.addCell(new Label(AON.MSG.fiscalYear()),AON.CSS.aonSearchPanelLabel())
				.addCell(yearBox)
				.addCell(new Label(AON.MSG.period()),AON.CSS.aonSearchPanelLabel())
				.addCell(periodBox)
				.addCell(new Label(AON.MSG.date()),AON.CSS.aonSearchPanelLabel() )
				.addCell(fromDateBox)
				.addCell(new Label(AON.MSG.to()),AON.CSS.aonItalic())
				.addCell(toDateBox)
				.addCellIf(hasActivities,new Label(AON.MSG.activity()),AON.CSS.aonItalic())
				.addCellIf(hasActivities,activityBox)
				.addCell(new Label(AON.MSG.titular()),AON.CSS.aonSearchPanelLabel() )
				.addCell(registryBox)
				.addCell(new Label(), AON.CSS.aonFlexGrow1())
		);
			
		mainTab.addRow().addCell( 
			new AonDisplayTable().addRow()
				.addCell(new Label(AON.MSG.invoices()),AON.CSS.aonSearchPanelLabel() )
				.addCell(outputBox)
				.addCell(new Label(AON.MSG.transaction()),AON.CSS.aonSearchPanelLabel() )
				.addCell(transactionBox)
				.addCell(new Label(AON.MSG.investment()),AON.CSS.aonSearchPanelLabel() )
				.addCell(investmentBox)
				.addCell(new Label(AON.MSG.service()),AON.CSS.aonSearchPanelLabel() )
				.addCell(serviceBox)
				.addCell(new Label(AON.MSG.vatAccrualPaymentAbbr()),AON.CSS.aonSearchPanelLabel() )
				.addCell(accrualRegimeBox)
				.addCell(new Label(AON.MSG.withholdingFarmer()),AON.CSS.aonSearchPanelLabel() )
				.addCell(farmerRegimeBox)
				.addCell(new Label(AON.MSG.surcharge()),AON.CSS.aonSearchPanelLabel() )
				.addCell(surchargeBox)
				.addCell(new Label(AON.MSG.rectified()),AON.CSS.aonSearchPanelLabel() )
				.addCell(rectificationTypeBox)
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
		confidentialBox.addItem( "Facturas NO confidenciales" );
		confidentialBox.addItem( "Facturas confidenciales" );
		confidentialBox.addItem(" Todas ");
		confidentialBox.setSelectedIndex(2);
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
	
	private void initRegistryBox(InvoiceConsoleModuleOptions opts) {
		registryBox = new AonAccountingRegistryBox(opts,true);
		registryBox.setRequired(false);
		registryBox.addSelectionHandler(event -> fire(opts));
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
		if (transactionBox.getValue() != null) {
			if (transactionBox.getValue() == InvoiceTransactionType.NATIONAL) {
				params.setVatSummaryType(VatSummaryType.NATIONAL);
				if (surchargeBox.getSelectedIndex() == 2) params.setVatSummaryType(VatSummaryType.SURCHARGE);
				if (farmerRegimeBox.getSelectedIndex() == 2) params.setVatSummaryType(VatSummaryType.FARMER);
			} else if (transactionBox.getValue() == InvoiceTransactionType.INTRACOMMUNITY) {
				params.setVatSummaryType(VatSummaryType.INTRACOMMUNITY);
			} else if (transactionBox.getValue() == InvoiceTransactionType.EXTRACOMMUNITY) {
				params.setVatSummaryType(VatSummaryType.EXTRACOMMUNITY);
			} else if (transactionBox.getValue() == InvoiceTransactionType.CAN_CEU_MEL) {
				params.setVatSummaryType(VatSummaryType.CAN_CEU_MEL);
			} else if (transactionBox.getValue() == InvoiceTransactionType.OTHER_ISP) {
				params.setVatSummaryType(VatSummaryType.OTHER_ISP);
			}
		}
			
		if (outputBox.getSelectedIndex() == 1) params.setOutput(false);
		if (outputBox.getSelectedIndex() == 2) params.setOutput(true);
			
		if (surchargeBox.getSelectedIndex() == 1) params.setSurcharge(false);
		if (surchargeBox.getSelectedIndex() == 2) params.setSurcharge(true);

		if (rectificationTypeBox.getSelectedIndex() > 0) {
			params.setRectificationType(RectificationType.values()[rectificationTypeBox.getSelectedIndex() - 1]);	
		}
			
		if (investmentBox.getSelectedIndex() == 1) params.setInvestment(false);
		if (investmentBox.getSelectedIndex() == 2) params.setInvestment(true);
			
		if (accrualRegimeBox.getSelectedIndex() == 1) params.setAccrualRegime(false);
		if (accrualRegimeBox.getSelectedIndex() == 2) params.setAccrualRegime(true);

		if (serviceBox.getSelectedIndex() == 1) params.setService(false);
		if (serviceBox.getSelectedIndex() == 2) params.setService(true);
			
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
