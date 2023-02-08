package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCloseTab;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfSummary;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


public class IRPFReport extends MainEntryPoint {

	private static final String IRPF_EXCEL_REPORT_PRINT = "/aon_gwt_fiscal/roms/IrpfReportExcelPrint";
	private static final String IRPF_PDF_REPORT_PRINT = "/aon_gwt_fiscal/roms/IrpfReportPDFPrint";
	private static final int SUMMARY_TAB = 0;
	private static final int RESULTS_TAB = 1;

	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
	}

	static final IrpfReportServiceAsync SERVICE;
	static {
		IrpfReportServiceAsync serviceRaw = GWT.create(IrpfReportService.class);
		SERVICE = new IrpfReportServiceAsyncDecorator(serviceRaw);
	}
	
	private DockLayoutPanel dockLayoutPanel;
	private TabLayoutPanel tabLayout;
	private SimpleLayoutPanel summaryContent;
	private SimpleLayoutPanel resultsContent;
	private SimpleLayoutPanel filterPanelContainer;
	private IRPFReportFilterPanel filterPanel;
	
	private FormPanel diskForm;
	private Hidden irpfParamsHidden;
	private Hidden domainIdHidden;
	private Hidden domainNameHidden;
	private Hidden userHidden;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		onModuleLoad( new IrpfReportModuleOptions()
			.setParentWidget(root)
			.setDomainName(getCurrentDomainName())
			.setDomain(getCurrentDomain())
			.setUser(getCurrentUser())
		);
	}

	public void onModuleLoad(IrpfReportModuleOptions options) {
		AON.ensureInjected();
		
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		dockLayoutPanel.addNorth(getToolbarPanel( options ), AonToolbar.HEIGTH);
		options.getParentWidget().add(dockLayoutPanel);
		
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration config) {
				options.setConfiguration(config);
				
				filterPanelContainer = new SimpleLayoutPanel();
				initializeFilterPanel(options);
				dockLayoutPanel.addNorth( filterPanelContainer, 115);
				SimpleLayoutPanel content = new SimpleLayoutPanel();
				content.setStyleName(AON.CSS.aonSelector());
				tabLayout = new TabLayoutPanel(26, Unit.PX);
				tabLayout.setWidth("100%");
				tabLayout.addSelectionHandler( event -> {
					if (event.getSelectedItem() == SUMMARY_TAB && resultsContent != null) {
						resultsContent.clear();	
					} else if (event.getSelectedItem() == RESULTS_TAB && resultsContent.getWidget() == null) {
						onSearch( options );	
					}
				});

				summaryContent = new SimpleLayoutPanel();
				tabLayout.add(summaryContent, new AonCloseTab(AON.MSG.summary(), false));
				
				resultsContent = new SimpleLayoutPanel();
				tabLayout.add(resultsContent, new AonCloseTab(AON.MSG.informationBreakdown(), false));
				
				content.setWidget(tabLayout);
				dockLayoutPanel.add(content);
				
				onSearch( options );
			}
			
			@Override 
			public void onFailure(Throwable caught) {
				dockLayoutPanel.add(new Label(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage()+ "]"));
			}
		});
	}
	
	private Widget getToolbarPanel(IrpfReportModuleOptions options) {
		AonToolbar toolbarPanel = new AonToolbar("Tabla I.R.P.F.");
		
		final AonToolbarButton pdf = new AonToolbarButton(AON.MSG.print(), AON.CSS.aonIconPdf());
		pdf.addClickHandler(event -> submitForm(options, IRPF_PDF_REPORT_PRINT));
		toolbarPanel.add(pdf);

		final AonToolbarButton excel = new AonToolbarButton(AON.MSG.export(), AON.CSS.aonIconExcel());
		excel.addClickHandler(event -> submitForm(options, IRPF_EXCEL_REPORT_PRINT));
		toolbarPanel.add(excel);

		final AonToolbarButton clean = new AonToolbarButton(AON.MSG.clean(), AON.CSS.aonIconClear());
		clean.addClickHandler(event -> {
			initializeFilterPanel(options);
			onSearch( options );
		});
		toolbarPanel.add(clean);
	
		final AonToolbarButton refresh = new AonToolbarButton(AON.MSG.refresh(), AON.CSS.aonIconRefresh());
		refresh.addClickHandler(event -> onSearch( options ) );
		toolbarPanel.add(refresh);

		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		irpfParamsHidden = new Hidden(IRequestParamsNames.IRPF_PARAMS);
		formFlowPanel.add(irpfParamsHidden);
		domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
		formFlowPanel.add(domainIdHidden);
		domainNameHidden = new Hidden(IRequestParamsNames.DOMAIN_NAME);
		formFlowPanel.add(domainNameHidden);
		userHidden = new Hidden(IRequestParamsNames.USER);
		formFlowPanel.add(userHidden);
		toolbarPanel.add(diskForm);

		return toolbarPanel;
	}
	
	private void initializeFilterPanel(IrpfReportModuleOptions options) {
		filterPanel = new IRPFReportFilterPanel(options);
		filterPanel.addValueChangeHandler( event -> onSearch( options ));
		filterPanelContainer.setWidget(filterPanel);
	}
	
	protected void onSearch(IrpfReportModuleOptions options) {
		onSearch( options, filterPanel.getParams(options) );
	}
	
	protected void onSearch(IrpfReportModuleOptions options, IRPFParams params) {
		if (tabLayout.getSelectedIndex() == SUMMARY_TAB) {
			refreshSummary(options, params);
		} else {
			refreshResults(options, params);
		}
	}

	private void refreshResults(IrpfReportModuleOptions options, IRPFParams params) {
		resultsContent.clear();
		resultsContent.setWidget(new IRPFReportPanel(options, params, null, null));		
	}
	
	private void refreshSummary(IrpfReportModuleOptions options, IRPFParams params) {
		summaryContent.clear();
		SERVICE.getIrpfBreakdownSummary(options.getOccam(), params
				, new AsyncCallback<IrpfSummary>() {
			
			@Override
			public void onSuccess(IrpfSummary summary) {
				IRPFReportSummaryPanel summaryPanel = new IRPFReportSummaryPanel(params, summary);
				summaryPanel.addSelectionHandler(e -> {
					filterPanel.setValue(e.getSelectedItem());
					tabLayout.selectTab(RESULTS_TAB, false);
					onSearch(options, e.getSelectedItem());
				});
				summaryContent.setWidget(summaryPanel);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Label error = new Label(AON.MSG.unexpectedError(caught.getMessage()));
				error.setStyleName(AON.CSS.aonMargin());
				error.addStyleName(AON.CSS.aonColorRed());
				error.addStyleName(AON.CSS.aonBold());
				summaryContent.setWidget(error);
			}
		});
	}
	
	private void submitForm(IrpfReportModuleOptions options, String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		irpfParamsHidden.setValue(JsonParams.convert(filterPanel.getParams(options)));
		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
		domainNameHidden.setValue(getCurrentDomainName());
		userHidden.setValue(getCurrentUser());
		diskForm.submit();
	}

	public static void run() {
		GWT.runAsync(IRPFReport.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Listado IRPF"));
			}

			@Override
			public void onSuccess() {
				IRPFReport irpfReport = new IRPFReport();
				irpfReport.onModuleLoad();
			}
			
		});
	}
	
}
