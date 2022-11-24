package com.esferalia.aon.gwt.fiscal.client.invoice;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.matrix.FiscalModelService;
import com.esferalia.aon.gwt.fiscal.client.matrix.FiscalModelServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.matrix.FiscalModelServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceModelReportParams;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


public class InvoiceModelReport extends MainEntryPoint {
	
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
	}

	static final FiscalModelServiceAsync FISCAL_MODEL_SERVICE;
	static {
		FiscalModelServiceAsync fiscalServiceRaw = GWT.create(FiscalModelService.class);
		FISCAL_MODEL_SERVICE = new FiscalModelServiceAsyncDecorator(fiscalServiceRaw);
	}

	private InvoiceModelReportModuleOptions options;
	
	private SimpleLayoutPanel content;
	
	private AonIntegerBox year;
	private PeriodListBox period;
	private AonDateBox fromDate;
	private AonDateBox toDate;
	private ListBox activity;
	int indexMainActivity = 0;
	private CheckBox unboundCheck;

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		InvoiceModelReportModuleOptions opts = new InvoiceModelReportModuleOptions();
		opts.setParentWidget(root);
		opts.setDomainName(getCurrentDomainName());
		opts.setDomain(getCurrentDomain());
		opts.setUser(getCurrentUser());
		this.onModuleLoad( opts );
	}
	
	public void onModuleLoad( InvoiceModelReportModuleOptions opts ) {
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
	
	private void loadModule( InvoiceModelReportModuleOptions opts ) {
		this.options = opts;
		AonLayoutPanel aonLayoutPanel = new AonLayoutPanel(Unit.PX);
		aonLayoutPanel.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		aonLayoutPanel.addNorth(getFilterPanel(), 90);
		
		content = new SimpleLayoutPanel();
		content.setStyleName(AON.CSS.aonSelector());
		aonLayoutPanel.add(content);
		initialize();
		options.getParentWidget().add(aonLayoutPanel);
	}
	
	private Widget getToolbarPanel() {
		AonToolbar toolbarPanel  = new AonToolbar("Facturas y Modelos Fiscales");
		final AonToolbarButton clean = new AonToolbarButton(AON.MSG.clean(),AON.CSS.aonIconClear());
		clean.addClickHandler(event -> initialize());
		toolbarPanel.add(clean);

		final AonToolbarButton refresh = new AonToolbarButton(AON.MSG.refresh(),AON.CSS.aonIconRefresh());
		refresh.addClickHandler(event -> onSearch());
		toolbarPanel.add(refresh);

		return toolbarPanel;
	}
	
	private Widget getFilterPanel() {
		year = new AonIntegerBox();
		year.addStyleName(AON.CSS.aonMarginLeft());
		year.setMaxLength(4);
		year.setVisibleLength(5);
		year.addValueChangeHandler(event -> {
			fillDates();
			onSearch();
		});
		
		period = new PeriodListBox();
		period.addStyleName(AON.CSS.aonMarginLeft());
		period.addChangeHandler(event -> {
			fillDates();
			onSearch();
		});
		
		fromDate = new AonDateBox();
		fromDate.addStyleName(AON.CSS.aonMarginLeft());
		fromDate.addValueChangeHandler(event -> onSearch());
		toDate = new AonDateBox();
		toDate.addValueChangeHandler(event -> onSearch());
		
		unboundCheck = new CheckBox("Mostrar solo facturas no vinculadas");
		unboundCheck.addStyleName(AON.CSS.aonMarginLeft());
		unboundCheck.addClickHandler(event -> onSearch());
	
		if (options.getConfiguration() != null && options.getConfiguration().hasActivities()) {
			activity = new ListBox();
			activity.addStyleName(AON.CSS.aonMarginLeft());
			activity.setWidth("300px");
			activity.addItem(" ----- ");
			int i = 0;
			for (EnterpriseActivity ea : options.getConfiguration().getActivities()) {
				String description =  ea.getDescription() + (ea.getIae().isEmpty()?"":(" ("+ea.getEpigraph()+")"));
				activity.addItem(description, AonNumberUtils.toString( ea.getId()));
				if (ea.isPrincipal()) {
					activity.setItemText(i, ea.getDescription() + AonStringUtils.SPACE + AonStringUtils.ASTERISK);
					indexMainActivity = i; // Se quedará marcada la actividad principal, por defecto
				}
				i++;
			}
			activity.setSelectedIndex(indexMainActivity);
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
		
		// ---------------------------------------------------------------- FIRST ROW
		FlowPanel firstRowPanel = new FlowPanel();
		firstRowPanel.addStyleName(AON.CSS.aonMarginTop());
		filterPanel.add(firstRowPanel);
		
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

		firstRowPanel.add(unboundCheck);

		// ---------------------------------------------------------------- SECOND ROW
		FlowPanel thirdRowPanel = new FlowPanel();
		thirdRowPanel.addStyleName(AON.CSS.aonMarginTop());
		filterPanel.add(thirdRowPanel);
		
		if (options.getConfiguration() != null && options.getConfiguration().hasActivities()) {
			InlineLabel activityLabel = new InlineLabel(AON.MSG.activity());
			activityLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
			thirdRowPanel.add(activityLabel);
			thirdRowPanel.add(activity);
		}

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
		year.setValue(DateUtils.getYear(),false);
		period.setSelectedIndex(0);
		fillDates();
		if (options.getConfiguration() != null && options.getConfiguration().hasActivities()) {
			activity.setSelectedIndex(indexMainActivity);
		}
		onSearch();
	}
	
	private InvoiceModelReportParams getWidgetParams() {
		InvoiceModelReportParams params = new InvoiceModelReportParams()
			.setDomain(options.getDomain())
			.setFromDate(fromDate.getValue())
			.setToDate(toDate.getValue())
			.setUnbound(unboundCheck.getValue())
			;
		if (options.getConfiguration() != null 
			&& options.getConfiguration().hasActivities() 
			&& activity.getSelectedIndex() > 0 ) {
			
			params.setActivity( AonNumberUtils.toInteger( activity.getSelectedValue()));
			params.setActivityDescription( activity.getSelectedItemText() == null ? "" : activity.getSelectedItemText().replace("*",""));
		}
		return params;
	}

	private void onSearch() {
		content.clear();
		content.setWidget(new InvoiceModelReportPanel(options, getWidgetParams()));	
	}

	public static void run() {
		GWT.runAsync(InvoiceModelReport.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("OperationReport"));
			}
			
			@Override
			public void onSuccess() {
				InvoiceModelReport operationReport = new InvoiceModelReport();
				operationReport.onModuleLoad();
			}
		});
	}
		
}
