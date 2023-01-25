package com.esferalia.aon.gwt.fiscal.client.report;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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


public class OperationReport extends MainEntryPoint {
	
	private static final String OPERATION_EXCEL_REPORT_PRINT = "/aon_gwt_fiscal/roms/OperationReportExcelPrint";
	private static final String OPERATION_EXCEL_REPORT_BOOK = "/aon_gwt_fiscal/roms/OperationReportExcelBook";
	private static final int IVA_TAB = 0; 
	private static final int IRPF_TAB = 1;

	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
	}

	private OperationReportModuleOptions options;
	
	private TabLayoutPanel tabLayout;
	private SimpleLayoutPanel ivaContent;
	private SimpleLayoutPanel irpfContent;
	
	private ListBox type;  // Compras y Gastos / Ventas e Ingresos
	private AonIntegerBox year;
	private PeriodListBox period;
	private AonDateBox fromDate;
	private AonDateBox toDate;
	private ListBox activity;
	int indexMainActivity = 0;

	private FormPanel diskForm;
	private Hidden operationParamsHidden = new Hidden("operationParams");
	private Hidden domainIdHidden = new Hidden("domainId");
	private Hidden domainNameHidden = new Hidden("domainName");
	private Hidden userHidden = new Hidden("user");
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		OperationReportModuleOptions opts = new OperationReportModuleOptions();
		opts.setParentWidget(root);
		opts.setDomainName(getCurrentDomainName());
		opts.setDomain(getCurrentDomain());
		opts.setUser(getCurrentUser());
		this.onModuleLoad( opts );
	}
	
	public void onModuleLoad( OperationReportModuleOptions opts ) {
		AON.ensureInjected();
		COMMON_SERVICE.getAonConfiguration(opts.getOccam(),new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration result) {
				opts.setConfiguration(result);
				loadModule( opts );
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert( AON.MSG.loadError("Operation Report"));
			}
		});

	}
	
	private void loadModule( OperationReportModuleOptions opts ) {
		this.options = opts;
		AonLayoutPanel aonLayoutPanel = new AonLayoutPanel(Unit.PX);
		aonLayoutPanel.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		aonLayoutPanel.addNorth(getAeatToolbarPanel(), AonToolbar.HEIGTH);
		aonLayoutPanel.addNorth(getFilterPanel(), 70);
		
		SimpleLayoutPanel content = new SimpleLayoutPanel();
		content.setStyleName(AON.CSS.aonSelector());
		tabLayout = new TabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		tabLayout.addSelectionHandler( event -> {
			if (event.getSelectedItem() == IVA_TAB) ivaContent.clear();
			else if (event.getSelectedItem() == IRPF_TAB) irpfContent.clear();
		});
		ivaContent = new SimpleLayoutPanel();
		tabLayout.add(ivaContent, "Listado IVA");
		irpfContent = new SimpleLayoutPanel();
		tabLayout.add(irpfContent, "Listado IRPF");
		content.setWidget(tabLayout);
		aonLayoutPanel.add(content);
		initialize();
		options.getParentWidget().add(aonLayoutPanel);
	}
	
	private Widget getToolbarPanel() {
		AonToolbar toolbarPanel  = new AonToolbar("Panel de Compras y Gastos / Ventas e Ingresos");
		final AonToolbarButton clean = new AonToolbarButton(AON.MSG.clean(),AON.CSS.aonIconClear());
		clean.addClickHandler(event -> initialize());
		toolbarPanel.add(clean);

		final AonToolbarButton refresh = new AonToolbarButton(AON.MSG.refresh(),AON.CSS.aonIconRefresh());
		refresh.addClickHandler(event -> onSearch());
		toolbarPanel.add(refresh);

		final AonToolbarButton export = new AonToolbarButton(AON.MSG.export(),AON.CSS.aonIconExcel());
		export.addClickHandler(event -> submitForm(OPERATION_EXCEL_REPORT_PRINT, getWidgetParams()));
		toolbarPanel.add(export);
		
		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(operationParamsHidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		toolbarPanel.add(diskForm);
		return toolbarPanel;
	}
	
	private Widget getAeatToolbarPanel() {
		AonToolbar toolbarPanel  = new AonToolbar();
		
		Label aeatLabel = new Label("Libros AEAT:");
		aeatLabel.setStyleName(AON.CSS.aonLabelWithIcon());
		aeatLabel.addStyleName(AON.CSS.aonIconAeatBw());
		toolbarPanel.add(aeatLabel);
		
		String aeatLabel1 = "Facturas Expedidas (IVA)";  
		final AonTextButton aeat1 = new AonTextButton(aeatLabel1);
		aeat1.addClickHandler(event -> submitForm(OPERATION_EXCEL_REPORT_BOOK
			, getWidgetParams()
				.setAeatBook(true)
				.setUnifiedBook(false)
				.setIrpf(false)
				.setExpenses(false)));
		toolbarPanel.add(aeat1);
		
		String aeatLabel2 = "Facturas Recibidas (IVA)";
		final AonTextButton aeat2 = new AonTextButton(aeatLabel2);
		aeat2.addClickHandler(event -> submitForm(OPERATION_EXCEL_REPORT_BOOK
			, getWidgetParams()
				.setAeatBook(true)
				.setUnifiedBook(false)
				.setIrpf(false)
				.setExpenses(true)));
		toolbarPanel.add(aeat2);

		String aeatLabel3 = "Ventas e Ingresos (IRPF)";
		final AonTextButton  aeat3 = new AonTextButton (aeatLabel3);
		aeat3.addClickHandler(event -> submitForm(OPERATION_EXCEL_REPORT_BOOK
			, getWidgetParams()
				.setAeatBook(true)
				.setUnifiedBook(false)
				.setIrpf(true)
				.setExpenses(false)));
		toolbarPanel.add(aeat3);
		
		String aeatLabel4 = "Compras y Gastos (IRPF)";
		final AonTextButton  aeat4 = new AonTextButton (aeatLabel4);
		aeat4.addClickHandler(event -> submitForm(OPERATION_EXCEL_REPORT_BOOK
			, getWidgetParams()
				.setAeatBook(true)
				.setUnifiedBook(false)
				.setIrpf(true)
				.setExpenses(true)));
		toolbarPanel.add(aeat4);
		
		String aeatLabel5 = "Ventas e Ingresos (IVA e IRPF)";
		final AonTextButton  aeat5 = new AonTextButton (aeatLabel5);
		aeat5.addClickHandler(event -> submitForm(OPERATION_EXCEL_REPORT_BOOK
			, getWidgetParams()
				.setAeatBook(true)
				.setUnifiedBook(true)
				.setIrpf(true)
				.setExpenses(false)));
		toolbarPanel.add(aeat5);
		
		String aeatLabel6 = "Compras y Gastos (IVA e IRPF)";
		final AonTextButton  aeat6 = new AonTextButton (aeatLabel6);
		aeat6.addClickHandler(event -> submitForm(OPERATION_EXCEL_REPORT_BOOK
			, getWidgetParams()
				.setAeatBook(true)
				.setUnifiedBook(true)
				.setIrpf(true)
				.setExpenses(true)));
		toolbarPanel.add(aeat6);
		
		return toolbarPanel;
	}

	
	private Widget getFilterPanel() {
		type = new ListBox();
		type .addStyleName(AON.CSS.aonMarginLeft());
		type.addItem("Compras y Gastos", "");
		type.addItem("Ventas e Ingresos");
		type.addChangeHandler(event -> fillDates());
		
		year = new AonIntegerBox();
		year.addStyleName(AON.CSS.aonMarginLeft());
		year.setMaxLength(4);
		year.setVisibleLength(5);
		year.addValueChangeHandler(event -> fillDates());
		
		period = new PeriodListBox();
		period.addStyleName(AON.CSS.aonMarginLeft());
		period.addChangeHandler(event -> fillDates());
		
		fromDate = new AonDateBox();
		fromDate.addStyleName(AON.CSS.aonMarginLeft());
		toDate = new AonDateBox();
	
		if (options.getConfiguration() != null && options.getConfiguration().hasActivities()) {
			activity = new ListBox();
			activity.addStyleName(AON.CSS.aonMarginLeft());
			activity.setWidth("300px");
			int i = 0;
			for (EnterpriseActivity ea : options.getConfiguration().getActivities()) {
				
				activity.addItem(ea.getDescription() + (ea.getIae().isEmpty()?"":(" ("+ea.getEpigraph()+")")), AonNumberUtils.toString( ea.getId()));
				if (ea.isPrincipal()) {
					activity.setItemText(i, ea.getDescription() + AonStringUtils.ASTERISK);
					indexMainActivity = i; // Se quedará marcada la actividad principal, por defecto
				}
				i++;
			}
			activity.setSelectedIndex(indexMainActivity);
		}

		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.CSS.aonSearchPanel());
		tab.addStyleName(AON.CSS.aonMarginLeft());
		tab.addStyleName(AON.CSS.aonMarginRight());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		
		tab.getColumnFormatter().setWidth(0, "auto");
		tab.getColumnFormatter().setWidth(1, "30px;");

		FlowPanel filterPanel = new FlowPanel();
		tab.setWidget(0, 0, filterPanel);
		
		// ---------------------------------------------------------------- FIRST ROW
		FlowPanel firstRowPanel = new FlowPanel();
		firstRowPanel.addStyleName(AON.CSS.aonMarginTop());
		filterPanel.add(firstRowPanel);
		
		Label typeLabel = new InlineLabel("Tipo");
		typeLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		typeLabel.addStyleName(AON.CSS.aonMarginLeft());
		firstRowPanel.add(typeLabel);
		firstRowPanel.add(type);
		
		Label yearLabel = new InlineLabel(AON.MSG.fiscalYear());
		yearLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		yearLabel.addStyleName(AON.CSS.aonMarginLeft());
		firstRowPanel.add(yearLabel);
		firstRowPanel.add(year);
		
		Label periodLabel = new InlineLabel(AON.MSG.period());
		periodLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		periodLabel.addStyleName(AON.CSS.aonMarginLeft());
		firstRowPanel.add(periodLabel);
		firstRowPanel.add(period);
		
		Label dateLabel = new InlineLabel(AON.MSG.date());
		dateLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		dateLabel.addStyleName(AON.CSS.aonMarginLeft());
		firstRowPanel.add(dateLabel);
		firstRowPanel.add(fromDate);
		
		InlineLabel to = new InlineLabel(AON.MSG.to());
		to.setStyleName(AON.CSS.aonItalic());
		to.addStyleName(AON.CSS.aonMarginRight());
		to.addStyleName(AON.CSS.aonMarginLeft());
		firstRowPanel.add(to);
		firstRowPanel.add(toDate);

		if (options.getConfiguration() != null && options.getConfiguration().hasActivities()) {
			InlineLabel activityLabel = new InlineLabel(AON.MSG.activity());
			activityLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
			firstRowPanel.add(activityLabel);
			firstRowPanel.add(activity);
		}

		
		AonSearchPanelButton searchButton = new AonSearchPanelButton(AON.MSG.searchAction(),AON.CSS.aonIconSearch());
		searchButton.addStyleName(AON.CSS.aonBold());
		searchButton.getElement().getStyle().setPaddingLeft(20, Unit.PX);
		searchButton.setText(AON.MSG.searchAction());
		searchButton.addClickHandler(event -> onSearch());
		firstRowPanel.add(searchButton);

		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.CSS.aonWidthAll());
		scrollPanel.setWidget(tab);
		return scrollPanel;
	}

	private void fillDates() {
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
	
	private void initialize() {
		type.setSelectedIndex(0);
		year.setValue(DateUtils.getYear(),false);
		period.setSelectedIndex(0);
		fillDates();
		if (options.getConfiguration() != null && options.getConfiguration().hasActivities()) {
			activity.setSelectedIndex(indexMainActivity);
		}
//		 onSearch(); Disable initial search
	}
	
	private void submitForm(String action, OperationParams params) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		operationParamsHidden.setValue(JsonParams.convert(params));				
		domainIdHidden.setValue(String.valueOf(options.getDomain()));
		domainNameHidden.setValue(options.getDomainName());		
		userHidden.setValue(options.getUser());
		diskForm.submit();
	}

	private OperationParams getWidgetParams() {
		OperationParams params = new OperationParams()
			.setDomain(options.getDomain())
			.setFromDate(fromDate.getValue())
			.setToDate(toDate.getValue())
			.setAeatBook(false)
			.setUnifiedBook(false)
			.setExpenses(type.getSelectedIndex() == 0)
			;
		if (options.getConfiguration() != null && options.getConfiguration().hasActivities() ) {
			params.setActivity( AonNumberUtils.toInteger( activity.getSelectedValue()));
			params.setActivityDescription( activity.getSelectedItemText() == null ? "" : activity.getSelectedItemText().replace("*",""));
		}
		return params;
	}

	private void onSearch() {
		if (tabLayout.getSelectedIndex() == IVA_TAB) {
			refreshIVA(getWidgetParams());
		} else if (tabLayout.getSelectedIndex() == IRPF_TAB) {
			refreshIRPF(getWidgetParams() );
		}
	}

	private void refreshIVA(OperationParams params) {
		ivaContent.clear();
		params.setIrpf(false);
		ivaContent.setWidget(new OperationReportVatPanel(options, params));	
	}

	private void refreshIRPF(OperationParams params) {
		irpfContent.clear();
		params.setIrpf(true);
		irpfContent.setWidget(new OperationReportIrpfPanel(options, params));
	}

	public static void run() {
		GWT.runAsync(OperationReport.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("OperationReport"));
			}
			
			@Override
			public void onSuccess() {
				OperationReport operationReport = new OperationReport();
				operationReport.onModuleLoad();
			}
		});
	}
		
}
