package com.esferalia.aon.gwt.fiscal.client.invoice.vat;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
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

public class VatReportFilterPanel extends ScrollPanel implements HasValueChangeHandlers<AccountingReportParams>{
	
	private static final String PX100 = "100px";

	private static final String TODAS = "-- Todas --";

	private IntegerBox yearBox;
	private PeriodListBox periodBox;
	private DateBoxEx fromDateBox;
	private DateBoxEx toDateBox;
	private ListBox outputBox;
	private InvoiceTransactionListBox transactionBox;

	private ListBox activityBox;
	private AccountingRegistryBox registryBox;
	private ListBox investmentBox;
	
	private ListBox accrualRegimeBox;
	private ListBox farmerRegimeBox;
	private ListBox serviceBox;
	private ListBox surchargeBox;
	private ListBox rectificationTypeBox;
	private AonTextBox percentBox;
	private AonTextBox surchargePercentBox;

	VatReportFilterPanel(VatReportModuleOptions opt) {
		yearBox = new IntegerBox();
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(5);
		yearBox.addValueChangeHandler(event -> {
			fillDates();
			onSearch(opt);
		});
		
		periodBox = new PeriodListBox();
		periodBox.addChangeHandler(event -> {
			fillDates();
			onSearch(opt);
		});
		
		fromDateBox = new DateBoxEx();
		fromDateBox.addValueChangeHandler(event -> onSearch(opt));
		toDateBox = new DateBoxEx();
		toDateBox.addValueChangeHandler(event -> onSearch(opt));
		
		outputBox = new ListBox();
		outputBox.addItem(TODAS);
		outputBox.addItem(AON.MSG.inputInvoices());
		outputBox.addItem(AON.MSG.outputInvoices());
		outputBox.addChangeHandler(event -> onSearch(opt));
		
		transactionBox = new InvoiceTransactionListBox(TODAS);
		transactionBox.setWidth(PX100);
		transactionBox.addChangeHandler(event -> onSearch(opt));

		if (opt.getConfiguration() != null && opt.getConfiguration().hasActivities()) {
			activityBox = new ListBox();
			activityBox.setWidth("120px");
			activityBox.addItem(TODAS, "");
			activityBox.setSelectedIndex(0);
			int i = 1;
			for (EnterpriseActivity ea : opt.getConfiguration().getActivities()) {
				activityBox.addItem(ea.getDescription() + (ea.getIae().isEmpty()?"":(" ("+ea.getEpigraph()+")")), AonNumberUtils.toString( ea.getId()));
				if (ea.isPrincipal()) {
					activityBox.setItemText(i, ea.getDescription() + AonStringUtils.ASTERISK);
				}
				i++;
			}
			activityBox.addItem("-- Sin actividad --", "-1");
			activityBox.addChangeHandler(event -> onSearch(opt));
		}

		registryBox = new AccountingRegistryBox(opt.getOccam());
		registryBox.setRequired(false);
		registryBox.addSelectionHandler(event -> onSearch(opt));
		
		investmentBox = new ListBox();
		investmentBox.setWidth(PX100);
		investmentBox.addItem(TODAS);
		investmentBox.addItem(AON.MSG.commonAsset());
		investmentBox.addItem(AON.MSG.investAsset());
		investmentBox.addChangeHandler(event -> onSearch(opt));
		
		accrualRegimeBox = new ListBox();
		accrualRegimeBox.addItem(TODAS);
		accrualRegimeBox.addItem(AON.MSG.no());
		accrualRegimeBox.addItem(AON.MSG.yes());
		accrualRegimeBox.addChangeHandler(event -> onSearch(opt));

		farmerRegimeBox = new ListBox();
		farmerRegimeBox.addItem(TODAS);
		farmerRegimeBox.addItem(AON.MSG.no());
		farmerRegimeBox.addItem(AON.MSG.yes());
		farmerRegimeBox.addChangeHandler(event -> {
			transactionBox.setValue(InvoiceTransactionType.NATIONAL);
			surchargeBox.setSelectedIndex(0);
			onSearch(opt);
		});

		serviceBox = new ListBox();
		serviceBox.addItem(TODAS);
		serviceBox.addItem(AON.MSG.no());
		serviceBox.addItem(AON.MSG.yes());
		serviceBox.addChangeHandler(event -> onSearch(opt));
		
		surchargeBox = new ListBox();
		surchargeBox.addItem(TODAS);
		surchargeBox.addItem(AON.MSG.no());
		surchargeBox.addItem(AON.MSG.yes());
		surchargeBox.addChangeHandler(event -> {
			transactionBox.setValue(InvoiceTransactionType.NATIONAL);
			farmerRegimeBox.setSelectedIndex(0);
			onSearch(opt);
		});
		
		rectificationTypeBox = new ListBox();
		rectificationTypeBox.setWidth(PX100);
		rectificationTypeBox.addItem(TODAS);
		rectificationTypeBox.addItem("Ni rectificativa ni rectificada");
		rectificationTypeBox.addItem(RectificationType.NORMAL_RECTIFIER.getDescription());
		
		rectificationTypeBox.addItem(RectificationType.SPECIAL_RECTIFIER.getDescription());
		rectificationTypeBox.getElement().<SelectElement>cast().getOptions().getItem(3).setDisabled(true);
		
		rectificationTypeBox.addItem(RectificationType.RECTIFIED.getDescription());
		rectificationTypeBox.addChangeHandler(event -> onSearch(opt));

		percentBox = new AonTextBox();
		percentBox.setVisibleLength(5);
		percentBox.addValueChangeHandler(event -> onSearch(opt));
		
		surchargePercentBox = new AonTextBox();
		surchargePercentBox.setVisibleLength(5);
		surchargePercentBox.addValueChangeHandler(event -> onSearch(opt));

		AonDisplayTable mainTab = new AonDisplayTable(
				 AON.CSS.aonSearchPanel()
				,AON.CSS.aonBlockCenter()
				,AON.CSS.aonWidthAlmostAll()
			);
		boolean hasActivities = (opt.getConfiguration() != null && opt.getConfiguration().hasActivities());
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
				.addCell(new Label(), AON.CSS.aonFlexGrow1())
		);
		
		mainTab.addRow().addCell( 
			new AonDisplayTable().addRow()
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
		);
		
		mainTab.addRow().addCell( 
				new AonDisplayTable().addRow()
				.addCell(new Label(AON.MSG.withholdingFarmer()),AON.CSS.aonSearchPanelLabel() )
				.addCell(farmerRegimeBox)
				.addCell(new Label(AON.MSG.surcharge()),AON.CSS.aonSearchPanelLabel() )
				.addCell(surchargeBox)
				.addCell(new Label(AON.MSG.rectified()),AON.CSS.aonSearchPanelLabel() )
				.addCell(rectificationTypeBox)
				.addCell(new Label(AON.MSG.percent()),AON.CSS.aonSearchPanelLabel() )
				.addCell(percentBox)
				.addCell(new Label(AON.MSG.surchargePercent()),AON.CSS.aonSearchPanelLabel() )
				.addCell(surchargePercentBox)
				.addCell(new Label(), AON.CSS.aonFlexGrow1())
		);

		initialize( opt );
		
		addStyleName(AON.CSS.aonWidthAll());
		setWidget(mainTab);
	}

	protected void onSearch( VatReportModuleOptions opt ) {
		ValueChangeEvent.<AccountingReportParams>fire( VatReportFilterPanel.this, getParams( opt ) );
	}

	protected void initialize(VatReportModuleOptions opt) {
		yearBox.setValue(DateUtils.getYear(),false);
		periodBox.setSelectedIndex(0);
		fillDates();
		outputBox.setSelectedIndex(0);
		transactionBox.setSelectedIndex(0);
		
		if (opt.getConfiguration() != null && opt.getConfiguration().hasActivities()) {
			activityBox.setSelectedIndex(0);
		}
		registryBox.setValue((AccountingRegistry) null,false);
		
		investmentBox.setSelectedIndex(0);
		accrualRegimeBox.setSelectedIndex(0);
		farmerRegimeBox.setSelectedIndex(0);
		serviceBox.setSelectedIndex(0);
		surchargeBox.setSelectedIndex(0);
		rectificationTypeBox.setSelectedIndex(0);
		percentBox.setValue(null, false);
		surchargePercentBox.setValue(null, false);
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
	
	AccountingReportParams getParams(VatReportModuleOptions opt) {
		AccountingReportParams params = new AccountingReportParams()
			.setDomain(opt.getDomain())
			.setRegistry(registryBox.getId())
			.setFromDate(fromDateBox.getValue())
			.setToDate(toDateBox.getValue())
			;
		if (opt.getConfiguration() != null && opt.getConfiguration().hasActivities() && activityBox.getSelectedIndex() > 0) {
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
		
		String p = percentBox.getValue();
		if (AonNumberUtils.isNumber(p)) {
			params.setPercent( AonNumberUtils.toDouble(p) );
		} else {
			params.setPercent( null );	
		}

		String s = surchargePercentBox.getValue();
		if (AonNumberUtils.isNumber(s)) {
			params.setVatSummaryType(VatSummaryType.SURCHARGE);
			params.setSurchargePercent( AonNumberUtils.toDouble(s) );
		} else {
			params.setSurchargePercent( null );	
		}

		return params;
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<AccountingReportParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	public void refreshVatSummaryType(VatSummaryType type) {
		if (type == null) {
			transactionBox.setValue( null );
			farmerRegimeBox.setSelectedIndex(0);
			surchargeBox.setSelectedIndex(0);
		} else if (type == VatSummaryType.NATIONAL) {
			transactionBox.setValue( InvoiceTransactionType.NATIONAL);
			farmerRegimeBox.setSelectedIndex(0);
			surchargeBox.setSelectedIndex(0);
		} else if (type == VatSummaryType.SURCHARGE) {
			transactionBox.setValue( InvoiceTransactionType.NATIONAL);
			surchargeBox.setSelectedIndex(2);
			farmerRegimeBox.setSelectedIndex(0);
		} else if (type == VatSummaryType.FARMER) {
			transactionBox.setValue( InvoiceTransactionType.NATIONAL);
			surchargeBox.setSelectedIndex(0);
			farmerRegimeBox.setSelectedIndex(2);
		} else if (type == VatSummaryType.INTRACOMMUNITY) {
			transactionBox.setValue( InvoiceTransactionType.INTRACOMMUNITY);
			farmerRegimeBox.setSelectedIndex(0);
			surchargeBox.setSelectedIndex(0);
		} else if (type == VatSummaryType.EXTRACOMMUNITY) {
			transactionBox.setValue( InvoiceTransactionType.EXTRACOMMUNITY);
			farmerRegimeBox.setSelectedIndex(0);
			surchargeBox.setSelectedIndex(0);
		} else if (type == VatSummaryType.CAN_CEU_MEL) {
			transactionBox.setValue( InvoiceTransactionType.CAN_CEU_MEL);
			farmerRegimeBox.setSelectedIndex(0);
			surchargeBox.setSelectedIndex(0);
		} else if (type == VatSummaryType.OTHER_ISP) {
			transactionBox.setValue( InvoiceTransactionType.OTHER_ISP);
			farmerRegimeBox.setSelectedIndex(0);
			surchargeBox.setSelectedIndex(0);
		}
	}

	public void refreshPercent(Double percent) {
		percentBox.setValue( AonNumberUtils.toString(percent), false);
	}

	public void refreshOutput(Boolean output) {
		if (output == null) {
			outputBox.setSelectedIndex(0);
		} else if ( output.booleanValue() ) {
			outputBox.setSelectedIndex(2);
		} else {
			outputBox.setSelectedIndex(1);
		}
	}

	public void refreshSurchargePercent(Double surchargePercent) {
		surchargePercentBox.setValue( AonNumberUtils.toString(surchargePercent), false);
	}
	
}
