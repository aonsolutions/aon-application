package com.esferalia.aon.gwt.fiscal.client.report;

import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomain;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomainName;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentUser;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getRootPanel;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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

public class OperationReport implements EntryPoint {
	
	private static final String OPERATION_EXCEL_REPORT_BOOK = "/aon_gwt_fiscal/roms/OperationReportExcelBook";
	private static final int TAB_0 = 0; // Facturas Expedidas / Ventas e Ingresos / Expedidas e Ingresos 
	private static final int TAB_1 = 1; // Facturas Recibidas / Compras y Gastos / Recibidas y Gastos

	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
	}

	private OperationReportModuleOptions options;
	
	private DockLayoutPanel dockLayoutPanel;
	private TabLayoutPanel tabLayout;
	private SimpleLayoutPanel tab0Content;
	private SimpleLayoutPanel tab1Content;
	
	private ListBox type;  // Libros de IVA, Libros de IRPF, Libros de IVA e IRPF
	private AonIntegerBox year;
	private PeriodListBox period;
	private AonDateBox fromDate;
	private AonDateBox toDate;
	private ListBox activity;

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
		
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		dockLayoutPanel.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		opts.getParentWidget().add(dockLayoutPanel);
		
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
		dockLayoutPanel.addNorth(getFilterPanel(), 110);
		SimpleLayoutPanel content = new SimpleLayoutPanel();
		content.setStyleName(AON.CSS.aonSelector());
		tab0Content = new SimpleLayoutPanel();
		tab1Content = new SimpleLayoutPanel();
		tabLayout = new TabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		tabLayout.add(tab0Content, "");
		tabLayout.add(tab1Content, "");
		tabLayout.addSelectionHandler( event -> {
			if (event.getSelectedItem() == TAB_0) 
				tab0Content.clear();
			else if (event.getSelectedItem() == TAB_1) 
				tab1Content.clear();
			onSearch();
		});
		content.setWidget(tabLayout);
		dockLayoutPanel.add(content);
		initialize();
	}
	
	private Widget getToolbarPanel() {
		AonToolbar toolbarPanel  = new AonToolbar("LIBROS REGISTRO AEAT");
		toolbarPanel.getElement().getStyle().setDisplay(Display.BLOCK);
		
		// Botón limpiar
		final AonToolbarButton clean = new AonToolbarButton(AON.MSG.clean(),AON.CSS.aonIconClear());
		clean.addClickHandler(event -> initialize());
		toolbarPanel.add(clean);

		// Botón refrescar
		final AonToolbarButton refresh = new AonToolbarButton(AON.MSG.refresh(),AON.CSS.aonIconRefresh());
		refresh.addClickHandler(event -> onSearch());
		toolbarPanel.add(refresh);
		
		// Botón exportar a Excel (Borrador)
		final AonToolbarButton draft = new AonToolbarButton(AON.MSG.export() + " (Borrador de revisi\u00F3n)",AON.CSS.aonIconExcel());
		draft.addClickHandler(event -> submitForm(OPERATION_EXCEL_REPORT_BOOK, getWidgetParams(true)));
		toolbarPanel.add(draft);

		// Botón exportar a Excel (Libros Oficiales)
		final AonToolbarButton export = new AonToolbarButton(AON.MSG.export() + " (Formato AEAT)",AON.CSS.aonIconAeat());
		export.addClickHandler(event -> submitForm(OPERATION_EXCEL_REPORT_BOOK, getWidgetParams(false)));
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
	
	private Widget getFilterPanel() {
		type = new ListBox();
		type.addStyleName(AON.CSS.aonMarginLeft());
		type.addItem("Libros Registro de IVA");
		type.addItem("Libros Registro de IRPF");
		type.addItem("Libros Unificados de IVA e IRPF");
		type.addChangeHandler(event -> onSearch());
		
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
		fromDate.addValueChangeHandler(event -> {
			// Las fechas deben ser del ejercicio indicado
			if (DateUtils.getYear(fromDate.getValue()) == year.getValue())
				onSearch();
			else 
				fillDates();
		}); 
		
		toDate = new AonDateBox();
		toDate.addValueChangeHandler(event -> {
			// Las fechas deben ser del ejercicio indicado
			if (DateUtils.getYear(toDate.getValue()) == year.getValue())
				onSearch();
			else 
				fillDates();
		}); 
	
		if (options.getConfiguration() != null && options.getConfiguration().hasAllActivities()) {
			activity = new ListBox();
			activity.addStyleName(AON.CSS.aonMarginLeft());
			activity.setWidth("300px");
			activity.addItem("-- Todas --", "");
			for (EnterpriseActivity ea : options.getConfiguration().getAllActivities()) {
				String description = ea.getDescription() + (ea.getIae().isEmpty() ? "" : (" ("+ea.getEpigraph()+")"));
				activity.addItem(description, AonNumberUtils.toString( ea.getId()));
				if (ea.isPrincipal()) {
					activity.setItemText(activity.getItemCount()-1, description + " " + AonStringUtils.ASTERISK);
				}
			}
			activity.setSelectedIndex(0);
			activity.addChangeHandler(event -> onSearch()); 
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

		if (options.getConfiguration() != null && options.getConfiguration().hasAllActivities()) {
			InlineLabel activityLabel = new InlineLabel(AON.MSG.activity());
			activityLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
			activityLabel.addStyleName(AON.CSS.aonMarginLeft());
			firstRowPanel.add(activityLabel);
			firstRowPanel.add(activity);
		}
		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.CSS.aonWidthAll());
		scrollPanel.setWidget(tab);
		return scrollPanel;
		
	}

	private void fillDates() {
		Integer y = year.getValue();
		Period p = period.getValue();
		if (y == null) {
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
		onSearch(); 
	}
	
	private void initialize() {
		type.setSelectedIndex(0);
		year.setValue(DateUtils.getYear(),false);
		period.setSelectedIndex(0);
		if (options.getConfiguration() != null && options.getConfiguration().hasAllActivities()) {
			activity.setSelectedIndex(0);
		}
		fillDates(); // Esto llama a onSearch
	}
	
	private void submitForm(String action, OperationParams params) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		operationParamsHidden.setValue(JsonParams.convert(params));				
		domainIdHidden.setValue(String.valueOf(options.getDomain()));
		domainNameHidden.setValue(options.getDomainName());		
		userHidden.setValue(options.getUser());
		diskForm.submit();
	}

	private OperationParams getWidgetParams(boolean draft) {
		OperationParams params = new OperationParams()
			.setDomain(options.getDomain())
			.setFromDate(fromDate.getValue())
			.setToDate(toDate.getValue())
			.setBookType(type.getSelectedIndex())
			.setDraft(draft)
			;
		if (options.getConfiguration() != null && options.getConfiguration().hasAllActivities() ) {
			if (activity.getSelectedIndex() > 0) {
				params.setActivity(AonNumberUtils.toInteger(activity.getSelectedValue()));
			} else {
				params.setActivity(null);
			}
		}
		return params;
	}

	private void onSearch() {
		paintTextTab();
		if (tabLayout.getSelectedIndex() == TAB_0) {
			refreshTab0(getWidgetParams(false));
		} else if (tabLayout.getSelectedIndex() == TAB_1) {
			refreshTab1(getWidgetParams(false) );
		}
	}

	private void refreshTab0(OperationParams params) {
		tab0Content.clear();
		params.setTabType(TAB_0);
		tab0Content.setWidget(new OperationReportTabPanel(options, params, new JsOperationGridTabExpIngPanel()));
	}
	
	private void refreshTab1(OperationParams params) {
		tab1Content.clear();
		params.setTabType(TAB_1);
		tab1Content.setWidget(new OperationReportTabPanel(options, params, new JsOperationGridTabRecGasPanel()));
	}

	private void paintTextTab() {
		if (type.getSelectedIndex() == 0) {
			tabLayout.setTabText(TAB_0, "Facturas Expedidas");
			tabLayout.setTabText(TAB_1, "Facturas Recibidas");
		} else if (type.getSelectedIndex() == 1) {
			tabLayout.setTabText(TAB_0, "Ventas e Ingresos");
			tabLayout.setTabText(TAB_1, "Compras y Gastos");
		} else {
			tabLayout.setTabText(TAB_0, "Expedidas e Ingresos");
			tabLayout.setTabText(TAB_1, "Recibidas y Gastos");
		}
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
