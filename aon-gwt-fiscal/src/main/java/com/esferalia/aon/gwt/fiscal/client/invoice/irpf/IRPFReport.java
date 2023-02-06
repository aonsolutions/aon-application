package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
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
	
	private IrpfReportModuleOptions options;
	
	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration config) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				onModuleLoad( new IrpfReportModuleOptions()
					.setParentWidget(root)
					.setDomainName(getCurrentDomainName())
					.setDomain(getCurrentDomain())
					.setUser(getCurrentUser())
					.setConfiguration(config)
				);
			}
			
			@Override 
			public void onFailure(Throwable caught) {
				Window.alert( AON.MSG.loadError("Modelo 303"));
			}
		});
	}

	private IrpfReportModuleOptions getOptions() {
		if (this.options == null) {
			this.options = new IrpfReportModuleOptions();
		}
		return this.options;
	}
	
	public void onModuleLoad(IrpfReportModuleOptions options) {
		this.options = options;
		AON.ensureInjected();
		
		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		dockLayoutPanel.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		options.getParentWidget().add(dockLayoutPanel);
		
		filterPanelContainer = new SimpleLayoutPanel();
		initializeFilterPanel(options);
		dockLayoutPanel.addNorth( filterPanelContainer, 115);
		SimpleLayoutPanel content = new SimpleLayoutPanel();
		content.setStyleName(AON.CSS.aonSelector());
		tabLayout = new TabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		tabLayout.addSelectionHandler( event -> {
			if (event.getSelectedItem() == 0 && resultsContent != null) {
				resultsContent.clear();	
			} else if (event.getSelectedItem() == 1 && resultsContent.getWidget() == null) {
				onSearch();	
			}
		});
						
		summaryContent = new SimpleLayoutPanel();
		tabLayout.add(summaryContent, AON.MSG.summary());
		
		resultsContent = new SimpleLayoutPanel();
		tabLayout.add(resultsContent, AON.MSG.informationBreakdown());
		
		content.setWidget(tabLayout);
		dockLayoutPanel.add(content);
		
		onSearch();
	}
	
	private Widget getToolbarPanel() {
		AonToolbar toolbarPanel = new AonToolbar("Tabla I.R.P.F.");
		
		final AonToolbarButton pdf = new AonToolbarButton(AON.MSG.print(), AON.CSS.aonIconPdf());
		pdf.addClickHandler(event -> submitForm(IRPF_PDF_REPORT_PRINT));
		toolbarPanel.add(pdf);

		final AonToolbarButton excel = new AonToolbarButton(AON.MSG.export(), AON.CSS.aonIconExcel());
		excel.addClickHandler(event -> submitForm(IRPF_EXCEL_REPORT_PRINT));
		toolbarPanel.add(excel);

		final AonToolbarButton clean = new AonToolbarButton(AON.MSG.clean(), AON.CSS.aonIconClear());
		clean.addClickHandler(event -> {
			initializeFilterPanel(options);
			onSearch();
		});
		toolbarPanel.add(clean);
	
		final AonToolbarButton refresh = new AonToolbarButton(AON.MSG.refresh(), AON.CSS.aonIconRefresh());
		refresh.addClickHandler(event -> onSearch() );
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
		filterPanel.addValueChangeHandler( event -> onSearch());
		filterPanelContainer.setWidget(filterPanel);
	}
	
	protected void onSearch() {
		if (tabLayout.getSelectedIndex() == 0) {
			refreshSummary(filterPanel.getParams(getOptions()));
		} else {
			refreshResults(filterPanel.getParams(getOptions()));
		}
	}
	
	private void refreshResults(IRPFParams params) {
		resultsContent.clear();
		filterPanel.setValue( params );
		resultsContent.setWidget(new IRPFReportPanel(getOptions(), params, null, null));		
	}
	
	private void refreshSummary(IRPFParams params) {
		summaryContent.clear();
		SERVICE.getIrpfBreakdownSummary(getOptions().getOccam(), params
				, new AsyncCallback<IrpfSummary>() {
			
			@Override
			public void onSuccess(IrpfSummary summary) {
				summaryContent.setWidget(new IRPFReportSummaryPanel(params, summary));
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
	
	private void submitForm(String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		irpfParamsHidden.setValue(JsonParams.convert(filterPanel.getParams(getOptions())));
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
