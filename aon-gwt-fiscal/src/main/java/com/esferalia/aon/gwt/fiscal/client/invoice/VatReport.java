package com.esferalia.aon.gwt.fiscal.client.invoice;


import java.util.Date;
import java.util.LinkedList;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.shared.JsonVatParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.fiscal.VatParams;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


public class VatReport extends MainEntryPoint {

	private static final String VAT_EXCEL_REPORT_PRINT = "/aon_gwt_fiscal/VatReportExcelPrint";
	private static final DateTimeFormat FORMATTER = DateTimeFormat.getFormat("dd/MM/yyyy"); 
	
	private static CommonServiceAsync commonService;
	private static FiscalServiceAsync fiscalService;
	
	private AonConfiguration configuration;
	private DockLayoutPanel dockLayoutPanel;
	private SimpleLayoutPanel content;
	private TabLayoutPanel tabLayout;
	private SimpleLayoutPanel summaryContent;
	private SimpleLayoutPanel resultsContent;
	
	private IntegerBox year;
	private PeriodListBox period;
	private DateBoxEx fromDate;
	private DateBoxEx toDate;
	private ListBox output;
	private InvoiceTransactionListBox transaction;

	private ListBox activity;
	private AccountingRegistryBox registry;
	private ListBox investment;
	
	private ListBox accrualRegime;
	private ListBox farmerRegime;
	private ListBox service;
	private ListBox surcharge;
	private ListBox rectificationType;

	private VatParams params;	
	
	FormPanel diskForm;
	Hidden vatParamsHidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;
	
	NumberFormat formatter;
	
	private int domain;
	private int enterprise;

	interface SafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"gwt-InlineLabel .aon-padding-right aon-padding-left-20 {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}

	private static final SafeTemplate template = GWT.create(SafeTemplate.class);

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		dockLayoutPanel.addNorth(getToolbarPanel(), 25);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(dockLayoutPanel);
		
		formatter = NumberFormat.getDecimalFormat();
		formatter.overrideFractionDigits(2, 2);
		
		commonService.getAonConfiguration(getCurrentDomainName(),
				getCurrentDomain(),
				new AsyncCallback<AonConfiguration>() {
					@Override
					public void onSuccess(AonConfiguration result) {
						configuration = result;
						dockLayoutPanel.addNorth(getFilterPanel(), 135);
						content = new SimpleLayoutPanel();
						content.setStyleName(AON.AON_CSS.aonSelector());
						tabLayout = new TabLayoutPanel(26, Unit.PX);
						tabLayout.setWidth("100%");
						tabLayout.addSelectionHandler( new SelectionHandler<Integer>() {
							
							@Override
							public void onSelection(SelectionEvent<Integer> event) {
								if (event.getSelectedItem() == 0 && resultsContent != null) {
									resultsContent.clear();	
								} else if (event.getSelectedItem() == 1 && resultsContent.getWidget() == null) {
									onSearch();	
								}
							}
						});
						
						summaryContent = new SimpleLayoutPanel();
						tabLayout.add(summaryContent, template.tab(AON.MSG.summary(), AON.AON_CSS.aonIconModel()));
						
						resultsContent = new SimpleLayoutPanel();
						tabLayout.add(resultsContent, template.tab(AON.MSG.informationBreakdown(), AON.AON_CSS.aonIconInfo()));
						
						content.setWidget(tabLayout);
						dockLayoutPanel.add(content);
						initialize();
					}

					@Override
					public void onFailure(Throwable caught) {
						dockLayoutPanel.add(new Label(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage()+ "]"));
					}
				});

	}

	
	protected void initialize() {
		year.setValue(DateUtils.getYear(),false);
		period.setSelectedIndex(0);
		fillDates();
		output.setSelectedIndex(0);
		transaction.setSelectedIndex(0);
		
		if (configuration != null && configuration.hasActivities()) {
			activity.setSelectedIndex(0);
		}
		registry.setValue((AccountingRegistry) null,false);
		
		investment.setSelectedIndex(0);
		accrualRegime.setSelectedIndex(0);
		farmerRegime.setSelectedIndex(0);
		service.setSelectedIndex(0);
		surcharge.setSelectedIndex(0);
		rectificationType.setSelectedIndex(0);
		
		onSearch();
	}

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	private Widget getToolbarPanel() {
		FlowPanel toolbarPanel = new FlowPanel();
		toolbarPanel.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
		toolbarPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable toolbar = new FlexTable();
		toolbar.setCellPadding(0);
		toolbar.setCellSpacing(0);
		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
		toolbar.setWidget(0, 0, titlePanel);
		toolbar.setWidget(0, 0, new Label("Tabla I.V.A."));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		
		final Button clean = new Button();
		clean.setText(AON.MSG.clean());
		clean.setTitle(AON.MSG.clean());
		clean.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		clean.addStyleName(AON.AON_CSS.aonIconDelete());
		clean.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				initialize();
			}
		});
		buttonContainer.add(clean);
		
		final Button excel = new Button();
		excel.setText(AON.MSG.export());
		excel.setTitle(AON.MSG.export());
		excel.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		excel.addStyleName(AON.AON_CSS.aonIconExcel());
		excel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				submitForm(VAT_EXCEL_REPORT_PRINT);
			}
		});
		buttonContainer.add(excel);

		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		vatParamsHidden = new Hidden("vatParams");
		formFlowPanel.add(vatParamsHidden);
		domainIdHidden = new Hidden("domainId");
		formFlowPanel.add(domainIdHidden);
		domainNameHidden = new Hidden("domainName");
		formFlowPanel.add(domainNameHidden);
		buttonContainer.add(diskForm);
		
		
		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}
	
	private void submitForm(String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		vatParamsHidden.setValue(JsonVatParams.convert(getWidgetParams()));
		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
		domainNameHidden.setValue(getCurrentDomainName());
		diskForm.submit();
	}
	
	
	private Widget getFilterPanel() {
		year = new IntegerBox();
		year.setMaxLength(4);
		year.setVisibleLength(5);
		year.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				fillDates();
				onSearch();
			}
		});
		
		period = new PeriodListBox();
		period.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				fillDates();
				onSearch();
			}
		});
		
		fromDate = new DateBoxEx();
		fromDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				onSearch();
			}
		});
		toDate = new DateBoxEx();
		toDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				onSearch();
			}
		});
		
		output = new ListBox();
		output.addItem("-- Todas --");
		output.addItem(AON.MSG.inputInvoices());
		output.addItem(AON.MSG.outputInvoices());
		output.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch();
			}
		});
		
		transaction = new InvoiceTransactionListBox();
		transaction.setWidth("100px");
		transaction.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch();
			}
		});

		if (configuration != null && configuration.hasActivities()) {
			activity = new ListBox();
			activity.setWidth("120px");
			activity.addItem("-- Todas --", "");
			activity.setSelectedIndex(0);
			int i = 1;
			for (EnterpriseActivity ea : configuration.getActivities()) {
				activity.addItem(ea.getDescription() + (ea.getIae() == null?"":(" ("+ea.getEpigraph()+")")), AonNumberUtils.toString( ea.getId()));
				if (ea.isPrincipal()) {
					activity.setItemText(i, ea.getDescription() + AonStringUtils.ASTERISK);
				}
				i++;
			}
			activity.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					onSearch();
				}
			});
		}

		registry = new AccountingRegistryBox(getCurrentDomainName(), getCurrentDomain());
		registry.setRequired(false);
		registry.addSelectionHandler(new SelectionHandler<AccountingRegistry>() {
			
			@Override
			public void onSelection(SelectionEvent<AccountingRegistry> event) {
				onSearch();
			}
		});
		
		investment = new ListBox();
		investment.setWidth("100px");
		investment.addItem("-- Todas --");
		investment.addItem(AON.MSG.commonAsset());
		investment.addItem(AON.MSG.investAsset());
		investment.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch();
			}
		});
		
		accrualRegime = new ListBox();
		accrualRegime.addItem("-- Todas --");
		accrualRegime.addItem(AON.MSG.no());
		accrualRegime.addItem(AON.MSG.yes());
		accrualRegime.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch();
			}
		});

		farmerRegime = new ListBox();
		farmerRegime.addItem("-- Todas --");
		farmerRegime.addItem(AON.MSG.no());
		farmerRegime.addItem(AON.MSG.yes());
		farmerRegime.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				transaction.setValue(InvoiceTransactionType.NATIONAL);
				surcharge.setSelectedIndex(0);
				onSearch();
			}
		});

		service = new ListBox();
		service.addItem("-- Todas --");
		service.addItem(AON.MSG.no());
		service.addItem(AON.MSG.yes());
		service.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch();
			}
		});
		
		surcharge = new ListBox();
		surcharge.addItem("-- Todas --");
		surcharge.addItem(AON.MSG.no());
		surcharge.addItem(AON.MSG.yes());
		surcharge.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				transaction.setValue(InvoiceTransactionType.NATIONAL);
				farmerRegime.setSelectedIndex(0);
				onSearch();
			}
		});
		
		rectificationType = new ListBox();
		rectificationType.setWidth("100px");
		rectificationType.addItem("-- Todas --");
		rectificationType.addItem("Ni rectificativa ni rectificada");
		rectificationType.addItem(RectificationType.NORMAL_RECTIFIER.getDescription());
		
		rectificationType.addItem(RectificationType.SPECIAL_RECTIFIER.getDescription());
		rectificationType.getElement().<SelectElement>cast().getOptions().getItem(3).setDisabled(true);
		
		rectificationType.addItem(RectificationType.RECTIFIED.getDescription());
		rectificationType.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch();
			}
		});

		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonPanelGridSearch());
		tab.addStyleName(AON.AON_CSS.aonWidth90Percent());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		
		tab.getColumnFormatter().setWidth(0, "auto");
		tab.getColumnFormatter().setWidth(1, "30px;");

		FlowPanel filterPanel = new FlowPanel();
		tab.setWidget(0, 0, filterPanel);
		
		// ---------------------------------------------------------------- FIRST ROW
		FlowPanel firstRowPanel = new FlowPanel();
		firstRowPanel.addStyleName(AON.AON_CSS.aonMarginTop5());
		filterPanel.add(firstRowPanel);
		
		Label yearLabel = new InlineLabel(AON.MSG.fiscalYear());
		yearLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		yearLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		firstRowPanel.add(yearLabel);
		firstRowPanel.add(year);
		
		Label periodLabel = new InlineLabel(AON.MSG.period());
		periodLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		periodLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		firstRowPanel.add(periodLabel);
		firstRowPanel.add(period);
		
		Label dateLabel = new InlineLabel(AON.MSG.date());
		dateLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		dateLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		firstRowPanel.add(dateLabel);
		firstRowPanel.add(fromDate);
		InlineLabel to = new InlineLabel(AON.MSG.to());
		to.setStyleName(AON.AON_CSS.aonItalic());
		to.addStyleName(AON.AON_CSS.aonMarginRight());
		to.addStyleName(AON.AON_CSS.aonMarginLeft());
		firstRowPanel.add(to);
		firstRowPanel.add(toDate);
		
		Label outputLabel = new InlineLabel(AON.MSG.invoices());
		outputLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		outputLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		firstRowPanel.add(outputLabel);
		service.addStyleName(AON.AON_CSS.aonMarginRight());
		firstRowPanel.add(output);

		Label transactionLabel = new InlineLabel(AON.MSG.transaction());
		transactionLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		transactionLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		firstRowPanel.add(transactionLabel);
		service.addStyleName(AON.AON_CSS.aonMarginRight());
		firstRowPanel.add(transaction);

		// ---------------------------------------------------------------- SECOND ROW
		FlowPanel secondRowPanel = new FlowPanel();
		secondRowPanel.addStyleName(AON.AON_CSS.aonMarginTop5());
		filterPanel.add(secondRowPanel);


		Label titularLabel = new InlineLabel(AON.MSG.titular());
		titularLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		titularLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		secondRowPanel.add(titularLabel);
		secondRowPanel.add(registry);

		// ---------------------------------------------------------------- THIRD ROW
		FlowPanel thirdRowPanel = new FlowPanel();
		thirdRowPanel.addStyleName(AON.AON_CSS.aonMarginTop5());
		filterPanel.add(thirdRowPanel);
		
		Label investmentLabel = new InlineLabel(AON.MSG.investment());
		investmentLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		investmentLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		thirdRowPanel.add(investmentLabel);
		investment.addStyleName(AON.AON_CSS.aonMarginRight());
		thirdRowPanel.add(investment);
		
		Label serviceLabel = new InlineLabel(AON.MSG.service());
		serviceLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		serviceLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		thirdRowPanel.add(serviceLabel);
		service.addStyleName(AON.AON_CSS.aonMarginRight());
		thirdRowPanel.add(service);

		Label accrualLabel = new InlineLabel(AON.MSG.vatAccrualPaymentAbbr());
		accrualLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		accrualLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		thirdRowPanel.add(accrualLabel);
		accrualRegime.addStyleName(AON.AON_CSS.aonMarginRight());
		thirdRowPanel.add(accrualRegime);

		if (configuration != null && configuration.hasActivities()) {
			InlineLabel activityLabel = new InlineLabel(AON.MSG.activity());
			activityLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
			activityLabel.addStyleName(AON.AON_CSS.aonFontSmall());
			thirdRowPanel.add(activityLabel);
			thirdRowPanel.add(activity);
		}
		
		// ---------------------------------------------------------------- FOURTH ROW
		FlowPanel fourthRowPanel = new FlowPanel();
		fourthRowPanel.addStyleName(AON.AON_CSS.aonMarginTop5());
		filterPanel.add(fourthRowPanel);

		Label farmerLabel = new InlineLabel(AON.MSG.withholdingFarmer());
		farmerLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		farmerLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		fourthRowPanel.add(farmerLabel);
		farmerRegime.addStyleName(AON.AON_CSS.aonMarginRight());
		fourthRowPanel.add(farmerRegime);

		Label surchargeLabel = new InlineLabel(AON.MSG.surcharge());
		surchargeLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		surchargeLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		fourthRowPanel.add(surchargeLabel);
		surcharge.addStyleName(AON.AON_CSS.aonMarginRight());
		fourthRowPanel.add(surcharge);
		
		Label rectifiedLabel = new InlineLabel(AON.MSG.rectified());
		rectifiedLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		rectifiedLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		fourthRowPanel.add(rectifiedLabel);
		rectificationType.addStyleName(AON.AON_CSS.aonMarginRight());
		fourthRowPanel.add(rectificationType);
		

		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		scrollPanel.setWidget(tab);
		return scrollPanel;
	}

	protected void fillDates() {
		Integer y = year.getValue();
		Period p = period.getValue();
		if ( y == null) {
			fromDate.setValue(null,false);
			toDate.setValue(null,false);
		} else {
			if (p == null) {
				fromDate.setValue(DateUtils.getFirstDayOfYear(y - 1900),false);
				toDate.setValue(DateUtils.getLastDayOfYear(y - 1900),false);
			} else {
				fromDate.setValue(DateUtils.getDate(p.getStartMonth(), y),false);
				toDate.setValue(DateUtils.getLastDayOfMonth(DateUtils.getDate(p.getDueMonth(), y)),false);
			}
		}
	}

	protected void onSearch() {
		if (tabLayout.getSelectedIndex() == 0) {
			refreshSummary(getWidgetParams());
		} else {
			refreshResults(getWidgetParams() );
		}
	}
	
	private VatParams getWidgetParams() {
		VatParams params = new VatParams()
			.setDomain(getCurrentDomain())
			.setRegistry(registry.getId())
			.setFromDate(fromDate.getValue())
			.setToDate(toDate.getValue())
			;
		if (configuration != null && configuration.hasActivities() && activity.getSelectedIndex() > 0) {
			params.setActivity( AonNumberUtils.toInteger( activity.getSelectedValue()));
		}
		if (transaction.getValue() != null) {
			if (transaction.getValue() == InvoiceTransactionType.NATIONAL) {
				params.setVatSummaryType(VatSummaryType.NATIONAL);
				if (surcharge.getSelectedIndex() == 2) params.setVatSummaryType(VatSummaryType.SURCHARGE);
				if (farmerRegime.getSelectedIndex() == 2) params.setVatSummaryType(VatSummaryType.FARMER);
			}
			if (transaction.getValue() == InvoiceTransactionType.INTRACOMMUNITY) params.setVatSummaryType(VatSummaryType.INTRACOMMUNITY);
			if (transaction.getValue() == InvoiceTransactionType.EXTRACOMMUNITY) params.setVatSummaryType(VatSummaryType.EXTRACOMMUNITY);
			if (transaction.getValue() == InvoiceTransactionType.CAN_CEU_MEL) params.setVatSummaryType(VatSummaryType.CAN_CEU_MEL);
			if (transaction.getValue() == InvoiceTransactionType.OTHER_ISP) params.setVatSummaryType(VatSummaryType.OTHER_ISP);
		}
		

		if (output.getSelectedIndex() == 1) params.setOutput(false);
		if (output.getSelectedIndex() == 2) params.setOutput(true);
		
		if (surcharge.getSelectedIndex() == 1) params.setSurcharge(false);
		if (surcharge.getSelectedIndex() == 2) params.setSurcharge(true);

		if (rectificationType.getSelectedIndex() > 0) {
			params.setRectificationType(RectificationType.values()[rectificationType.getSelectedIndex() - 1]);	
		}
		
		if (investment.getSelectedIndex() == 1) params.setInvestment(false);
		if (investment.getSelectedIndex() == 2) params.setInvestment(true);
		
		if (accrualRegime.getSelectedIndex() == 1) params.setAccrualRegime(false);
		if (accrualRegime.getSelectedIndex() == 2) params.setAccrualRegime(true);

		if (service.getSelectedIndex() == 1) params.setService(false);
		if (service.getSelectedIndex() == 2) params.setService(true);
		
		return params;
	}

	private void refreshAndSeeResults(VatParams params) {
		if (tabLayout.getSelectedIndex() == 0) {
			tabLayout.setAnimationDuration(300);
			tabLayout.selectTab(1,false);
		}
		refreshResults(params);
	}
	
	private void refreshResults(VatParams params) {
		resultsContent.clear();
		resultsContent.setWidget(new VatReportPanel(getCurrentDomainName(), getCurrentDomain(), params, null, null));
	}

	private void refreshSummary(VatParams params) {
		summaryContent.clear();
		ScrollPanel scroll = new ScrollPanel();			
		summaryContent.setWidget(scroll);
		fiscalService.getVatSummaryContext(getCurrentDomainName(), getCurrentDomain(), params
				, new AsyncCallback<LinkedList<VatSummaryContext>>() {
			
			@Override
			public void onSuccess(LinkedList<VatSummaryContext> result) {
				TreeMap<VatSummaryType,TreeMap<Double,Pair<VatSummaryContext, VatSummaryContext>>> map = 
						new TreeMap<VatSummaryType,TreeMap<Double,Pair<VatSummaryContext, VatSummaryContext>>>();
				for (VatSummaryContext vat : result){
					TreeMap<Double,Pair<VatSummaryContext,VatSummaryContext>> block = map.get(vat.getSummaryType());
					if (block == null) {
						block = new TreeMap<Double, Pair<VatSummaryContext,VatSummaryContext>>();
						map.put(vat.getSummaryType(), block);
					}
					Pair<VatSummaryContext,VatSummaryContext> line = block.get(vat.getPercentage());
					if (line == null) {
						line = Pair.of(vat.isOutput()?vat:null, vat.isOutput()?null:vat);
					} else {
						line = Pair.of(vat.isOutput()?vat:line.getLeft(), vat.isOutput()?line.getRight():vat);
					}
					block.put(vat.getPercentage(), line);					
				}
				
				FlexTable tab = new FlexTable();
				tab.setCellPadding(0);
				tab.setCellSpacing(0);
				tab.setStyleName(AON.AON_CSS.aonBlockCenter());
				tab.addStyleName(AON.AON_CSS.aonMarginTop());
				tab.addStyleName(AON.AON_CSS.aonBorderCollapse());
				
				tab.getColumnFormatter().setStyleName(0, AON.AON_CSS.aonWidth150());
				
				tab.getColumnFormatter().setStyleName(1, AON.AON_CSS.aonWidth120());
				tab.getColumnFormatter().setStyleName(2, AON.AON_CSS.aonWidth60());
				tab.getColumnFormatter().setStyleName(3, AON.AON_CSS.aonWidth120());
				
				tab.getColumnFormatter().setStyleName(4, AON.AON_CSS.aonWidth20());
				
				tab.getColumnFormatter().setStyleName(5, AON.AON_CSS.aonWidth120());
				tab.getColumnFormatter().setStyleName(6, AON.AON_CSS.aonWidth60());
				tab.getColumnFormatter().setStyleName(7, AON.AON_CSS.aonWidth120());
				tab.getColumnFormatter().setStyleName(8, AON.AON_CSS.aonWidth120());
				
				paintTableHeader(tab);
				
				double outputBase = 0;
				double outputQuota = 0;
				double inputBase = 0;
				double inputQuota = 0;
				double inputDeductibleQuota = 0;

				double estimation = 0;

				int row = 2;
				for (VatSummaryType type :  map.keySet() ) {
					row = paintEmptyRow(tab,row);
					
					Label typeLabel = new Label(type.getDescription());
					typeLabel.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							refreshAndSeeResults(getWidgetParams().setVatSummaryType(type));
						}
					});
					tab.setWidget(row,0, typeLabel);
					int rowspan = map.get(type).values().size();
					tab.getFlexCellFormatter().setRowSpan(row, 0, rowspan+1);
					
					tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonBold());
					tab.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
					tab.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFontBig());
					tab.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonSimpleBorder());
					tab.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonClickableBlock());
					tab.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBackgroundDisabled());
					
					boolean first = true;

					double typeOutputBase = 0;
					double typeOutputQuota = 0;
					double typeInputBase = 0;
					double typeInputQuota = 0;
					double typeInputDeductibleQuota = 0;

					for (Pair<VatSummaryContext,VatSummaryContext> pair : map.get(type).values() ) {
						int col = first? 0 : -1;
						first = false;
						if (pair.getLeft() != null) {
							ClickHandler leftClickHandler = new ClickHandler() {
								
								@Override
								public void onClick(ClickEvent event) {
									refreshAndSeeResults(getWidgetParams()
										.setVatSummaryType(type)
										.setPercent(pair.getLeft().getPercentage())
										.setOutput(true));
								}
							};
							typeOutputBase = typeOutputBase + pair.getLeft().getBase();
							typeOutputQuota = typeOutputQuota + pair.getLeft().getQuota();
							
							Label baseLabel = addCell(tab, row, (col+1) , formatter.format( pair.getLeft().getBase()));
							tab.getCellFormatter().addStyleName(row, (col+1), AON.AON_CSS.aonClickableBlock());
							baseLabel.addClickHandler(leftClickHandler);
							
							Label percentLabel = addCell(tab, row, (col+2) , formatter.format( pair.getLeft().getPercentage()) );
							tab.getCellFormatter().addStyleName(row, (col+2), AON.AON_CSS.aonClickableBlock());
							percentLabel.addClickHandler(leftClickHandler);
							
							Label quotaLabel = addCell(tab, row, (col+3) , formatter.format( pair.getLeft().getQuota()));
							tab.getCellFormatter().addStyleName(row, (col+3), AON.AON_CSS.aonClickableBlock());
							quotaLabel.addClickHandler(leftClickHandler);
							
						} else {
							addCell(tab, row, (col+1) , "");
							addCell(tab, row, (col+2) , "");
							addCell(tab, row, (col+3) , "");
						}
						
						if (pair.getRight() != null) {
							typeInputBase = typeInputBase + pair.getRight().getBase();
							typeInputQuota = typeInputQuota + pair.getRight().getQuota();
							typeInputDeductibleQuota = typeInputDeductibleQuota + pair.getRight().getDeductibleQuota();
							ClickHandler rightClickHandler = new ClickHandler() {
								
								@Override
								public void onClick(ClickEvent event) {
									refreshAndSeeResults(getWidgetParams()
										.setVatSummaryType(type)
										.setPercent(pair.getRight().getPercentage())
										.setOutput(false));
								}
							};
							Label baseLabel = addCell(tab, row, (col+5) , formatter.format( pair.getRight().getBase()));
							tab.getCellFormatter().addStyleName(row, (col+5), AON.AON_CSS.aonClickableBlock());
							baseLabel.addClickHandler(rightClickHandler);
							
							Label percentLabel = addCell(tab, row, (col+6) , formatter.format( pair.getRight().getPercentage()) );
							tab.getCellFormatter().addStyleName(row, (col+6), AON.AON_CSS.aonClickableBlock());
							percentLabel.addClickHandler(rightClickHandler);
							
							Label quotaLabel = addCell(tab, row, (col+7) , formatter.format( pair.getRight().getQuota()));
							tab.getCellFormatter().addStyleName(row, (col+7), AON.AON_CSS.aonClickableBlock());
							quotaLabel.addClickHandler(rightClickHandler);
							
							Label deductibleQuotaLabel = addCell(tab, row, (col+8) , formatter.format( pair.getRight().getDeductibleQuota()));
							tab.getCellFormatter().addStyleName(row, (col+8), AON.AON_CSS.aonClickableBlock());
							deductibleQuotaLabel.addClickHandler(rightClickHandler);
							
						} else {
							addCell(tab, row, (col+5) , "");
							addCell(tab, row, (col+6) , "");
							addCell(tab, row, (col+7) , "");
							addCell(tab, row, (col+8) , "");
						}
						++row;
					}
						
					if (type == VatSummaryType.NATIONAL
						|| type == VatSummaryType.SURCHARGE
						|| type == VatSummaryType.FARMER) {
							estimation = estimation + typeOutputQuota;	
							estimation = estimation - typeInputDeductibleQuota;
						}

					row = paintTotal( tab, row,type,typeOutputBase,typeOutputQuota,typeInputBase,typeInputQuota,typeInputDeductibleQuota);					
					
					outputBase = outputBase + typeOutputBase;
					outputQuota = outputQuota  + typeOutputQuota; 
					inputBase = inputBase + typeInputBase;
					inputQuota = inputQuota + typeInputQuota;
					inputDeductibleQuota = inputDeductibleQuota + typeInputDeductibleQuota;
				}
				row = paintEmptyRow(tab, row);
				row = paintTotal( tab, row,null,outputBase,outputQuota,inputBase,inputQuota,inputDeductibleQuota);
				row = paintEmptyRow(tab, row);
				 
				tab.setWidget(row, 0, new Label());
				String est = "Estimaci\u00F3n: ";
				if (estimation < 0  ) {
					est = est + "A Compensar / Devolver: " + formatter.format( AonMathUtils.absRounded( estimation ) );
					tab.getCellFormatter().setStyleName(row,0, AON.AON_CSS.aonColorGreen());
				} else if (estimation > 0  ) { 
					est = est + "A Ingresar: " + formatter.format( estimation );
					tab.getCellFormatter().setStyleName(row,0, AON.AON_CSS.aonColorRed());
				} else {
					est = est + "Cero / Sin Actividad ";
				}
				tab.getFlexCellFormatter().setColSpan(row, 0, 9);
				tab.getCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextCenter());
				tab.getCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonFontMedium());
				tab.getCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonSimpleBorder());
				tab.getCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBold());
				tab.setWidget(row, 0, new Label(est));
				scroll.setWidget( tab );		
			}
			
			private int paintEmptyRow(FlexTable tab, int row) {
				tab.setWidget(row, 0, new Label());
				tab.getFlexCellFormatter().setColSpan(row, 0, 9);
				tab.getRowFormatter().setStyleName(row, AON.AON_CSS.aonHeight5());
				return ++row;
			}

			private int paintTotal(FlexTable tab, int row, VatSummaryType type,double typeOutputBase, double typeOutputQuota,
					double typeInputBase, double typeInputQuota, double typeInputDeductibleQuota) {
				int col = type==null?1:0;
				ClickHandler leftClickHandler = new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						refreshAndSeeResults(getWidgetParams().setVatSummaryType(type).setOutput(true));
					}
				}; 
				Label obl = addCell(tab, row, col+0 , formatter.format( typeOutputBase));
				obl.addClickHandler(leftClickHandler);
				Label oql = addCell(tab, row, col+2 , formatter.format( typeOutputQuota));
				oql.addClickHandler(leftClickHandler);
				
				ClickHandler rightClickHandler = new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						refreshAndSeeResults(getWidgetParams().setVatSummaryType(type).setOutput(false));
					}
				}; 
				Label ibl = addCell(tab, row, col+4 , formatter.format( typeInputBase));
				ibl.addClickHandler(rightClickHandler);
				Label iql = addCell(tab, row, col+6 , formatter.format( typeInputQuota));
				iql.addClickHandler(rightClickHandler);
				Label idql = addCell(tab, row, col+7 , formatter.format( typeInputDeductibleQuota));
				idql.addClickHandler(rightClickHandler);
				
				tab.getCellFormatter().addStyleName(row, col+0, AON.AON_CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(row,col+0, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(row, col+2, AON.AON_CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(row,col+2, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(row, col+4, AON.AON_CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(row,col+4, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(row, col+6, AON.AON_CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(row,col+6, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(row, col+7, AON.AON_CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(row,col+7, AON.AON_CSS.aonBold());
				return ++row;
			}

			private Label addCell(FlexTable tab, int row, int col, String text) {
				Label label = new Label(text);
				tab.setWidget(row, col, label);
				tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextRight());
				tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonSimpleBorder());
				return label;
			}

			private void paintTableHeader(FlexTable tab) {
				Label outputLabel = new Label( AON.MSG.outputInvoices() );
				tab.setWidget(0,1, outputLabel);
				tab.getFlexCellFormatter().setColSpan(0, 1, 3);
				tab.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonTextCenter());
				tab.getCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonFontBig());
				tab.getCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonSimpleBorder());
				tab.getCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonBackgroundDisabled());
				outputLabel.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						VatParams params = getWidgetParams();
						params.setOutput(true);
						refreshAndSeeResults(params);
					}
				});
				
				Label inputLabel = new Label( AON.MSG.inputInvoices() );
				tab.setWidget(0,3, inputLabel);
				tab.getFlexCellFormatter().setColSpan(0, 3, 4);
				tab.getCellFormatter().setStyleName(0, 3, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(0, 3, AON.AON_CSS.aonTextCenter());
				tab.getCellFormatter().addStyleName(0, 3, AON.AON_CSS.aonFontBig());
				tab.getCellFormatter().addStyleName(0, 3, AON.AON_CSS.aonSimpleBorder());
				tab.getCellFormatter().addStyleName(0, 3, AON.AON_CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(0, 3, AON.AON_CSS.aonBackgroundDisabled());
				inputLabel.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						VatParams params = getWidgetParams();
						params.setOutput(false);
						refreshAndSeeResults(params);
					}
				});

				Label outputBaseLabel = new Label( AON.MSG.taxableBase() );
				tab.setWidget(1,1, outputBaseLabel);
				tab.getCellFormatter().setStyleName(1, 1, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(1, 1, AON.AON_CSS.aonTextRight());
				tab.getCellFormatter().addStyleName(1, 1, AON.AON_CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName(1, 1, AON.AON_CSS.aonBackgroundDisabled());
				tab.getCellFormatter().addStyleName(1, 1, AON.AON_CSS.aonSimpleBorder());
				
				Label outputPercentLabel = new Label( "%" );
				tab.setWidget(1,2, outputPercentLabel);
				tab.getCellFormatter().setStyleName(1, 2, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(1, 2, AON.AON_CSS.aonTextCenter());
				tab.getCellFormatter().addStyleName(1, 2, AON.AON_CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName(1, 2, AON.AON_CSS.aonBackgroundDisabled());
				tab.getCellFormatter().addStyleName(1, 2, AON.AON_CSS.aonSimpleBorder());

				Label outputQuotaLabel = new Label( AON.MSG.quota() );
				tab.setWidget(1,3, outputQuotaLabel);
				tab.getCellFormatter().setStyleName(1, 3, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(1, 3, AON.AON_CSS.aonTextRight());
				tab.getCellFormatter().addStyleName(1, 3, AON.AON_CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName(1, 3, AON.AON_CSS.aonBackgroundDisabled());
				tab.getCellFormatter().addStyleName(1, 3, AON.AON_CSS.aonSimpleBorder());

				Label inputBaseLabel = new Label( AON.MSG.taxableBase() );
				tab.setWidget(1,5, inputBaseLabel);
				tab.getCellFormatter().setStyleName(1, 5, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(1, 5, AON.AON_CSS.aonTextRight());
				tab.getCellFormatter().addStyleName(1, 5, AON.AON_CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName(1, 5, AON.AON_CSS.aonBackgroundDisabled());
				tab.getCellFormatter().addStyleName(1, 5, AON.AON_CSS.aonSimpleBorder());

				Label inputPercentLabel = new Label( "%" );
				tab.setWidget(1,6, inputPercentLabel);
				tab.getCellFormatter().setStyleName(1, 6, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(1, 6, AON.AON_CSS.aonTextRight());
				tab.getCellFormatter().addStyleName(1, 6, AON.AON_CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName(1, 6, AON.AON_CSS.aonBackgroundDisabled());
				tab.getCellFormatter().addStyleName(1, 6, AON.AON_CSS.aonSimpleBorder());

				Label inputQuotaLabel = new Label( AON.MSG.quota() );
				tab.setWidget(1,7, inputQuotaLabel);
				tab.getCellFormatter().setStyleName(1, 7, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(1, 7, AON.AON_CSS.aonTextCenter());
				tab.getCellFormatter().addStyleName(1, 7, AON.AON_CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName(1, 7, AON.AON_CSS.aonBackgroundDisabled());
				tab.getCellFormatter().addStyleName(1, 7, AON.AON_CSS.aonSimpleBorder());
				
				Label inputDeductibleQuotaLabel = new Label( AON.MSG.dedQuota() );
				tab.setWidget(1,8, inputDeductibleQuotaLabel);
				tab.getCellFormatter().setStyleName(1, 8, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(1, 8, AON.AON_CSS.aonTextRight());
				tab.getCellFormatter().addStyleName(1, 8, AON.AON_CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName(1, 8, AON.AON_CSS.aonBackgroundDisabled());
				tab.getCellFormatter().addStyleName(1, 8, AON.AON_CSS.aonSimpleBorder());
			}

			@Override
			public void onFailure(Throwable caught) {
				Label error = new Label(AON.MSG.unexpectedError(caught.getMessage()));
				error.setStyleName(AON.AON_CSS.aonMargin());
				error.addStyleName(AON.AON_CSS.aonColorRed());
				error.addStyleName(AON.AON_CSS.aonBold());
				scroll.setWidget(error);
			}
		});		
	}
}
