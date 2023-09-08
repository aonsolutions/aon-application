package com.esferalia.aon.gwt.fiscal.client.invoice.vat;


import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCloseTab;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTabLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.accounting.PrintReportDialog;
import com.esferalia.aon.gwt.fiscal.client.accounting.PrintReportDialog.IPrintReportDialogCallback;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.VatReportSummaryPanel.VatReportSummaryPanelCallback;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


public class VatReport extends MainEntryPoint {

	private static final String VAT_EXCEL_REPORT_PRINT = "/aon_gwt_fiscal/roms/VatReportExcelPrint";
	private static final String VAT_PDF_REPORT_PRINT = "/aon_gwt_fiscal/roms/VatReportPDFPrint";
	
	static final VATServiceAsync SERVICE;
	static {
		VATServiceAsync serviceRaw = GWT.create(VATService.class);
		SERVICE = new VATServiceAsyncDecorator(serviceRaw);
	}
	private static final int SUMMARY_TAB = 0;
	private static final int RESULTS_TAB = 1;
	
	private DockLayoutPanel dockLayoutPanel;
	private SimpleLayoutPanel content;
	private AonTabLayoutPanel tabLayout;
	private SimpleLayoutPanel vatSummaryContainer;
	private SimpleLayoutPanel vatPanelContainer;
	private VatReportFilterPanel filterPanel;

	private FormPanel diskForm;
	private Hidden vatParamsHidden;
	private Hidden domainIdHidden;
	private Hidden domainNameHidden;
	private Hidden userHidden;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		VatReportModuleOptions options = new VatReportModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		this.onModuleLoad( options );
	}
	
	public void onModuleLoad( final VatReportModuleOptions options ) {
		AON.ensureInjected();
		
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		dockLayoutPanel.addNorth(getToolbarPanel( options ), AonToolbar.HEIGTH);
		options.getParentWidget().add(dockLayoutPanel);
		
		SERVICE.getAonConfiguration(options.getOccam(), new AsyncCallback<AonConfiguration>() {
				@Override
				public void onSuccess(AonConfiguration result) {
					options.setConfiguration(result);
					
					filterPanel = new VatReportFilterPanel( options );
					getFilterPanel().addValueChangeHandler( params -> onSearch(options, params.getValue()));
					dockLayoutPanel.addNorth(getFilterPanel(), 135);
					content = new SimpleLayoutPanel();
					content.setStyleName(AON.CSS.aonSelector());
					tabLayout = new AonTabLayoutPanel(26, Unit.PX);
					tabLayout.setWidth("100%");
					tabLayout.addSelectionHandler( event -> {
						if (event.getSelectedItem() == SUMMARY_TAB && vatPanelContainer != null) {
							vatPanelContainer.clear();	
						} else if (event.getSelectedItem() == RESULTS_TAB && vatPanelContainer.getWidget() == null) {
							onSearch(options, getFilterPanel().getParams(options));	
						}
					});
					
					vatSummaryContainer = new SimpleLayoutPanel();
					tabLayout.add(vatSummaryContainer, new AonCloseTab(AON.MSG.summary(), false));
					
					vatPanelContainer = new SimpleLayoutPanel();
					tabLayout.add(vatPanelContainer, new AonCloseTab(AON.MSG.informationBreakdown(), false));
					
					content.setWidget(tabLayout);
					dockLayoutPanel.add(content);
					initialize(options);
				}

				@Override
				public void onFailure(Throwable caught) {
					dockLayoutPanel.add(new Label(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage()+ "]"));
				}
			});

	}

	private void initialize(VatReportModuleOptions options) {
		getFilterPanel().initialize( options );
		onSearch(options,  getFilterPanel().getParams(options) );
	}
	
	private Widget getToolbarPanel(VatReportModuleOptions options) {
		AonToolbar toolbarPanel = new AonToolbar("Tabla I.V.A.");
		
		final AonToolbarButton clean = new AonToolbarButton(AON.MSG.clean(), AON.CSS.aonIconClear());
		clean.addClickHandler(event -> initialize(options));
		toolbarPanel.add(clean);
	
		final AonToolbarButton pdf = new AonToolbarButton(AON.MSG.print(), AON.CSS.aonIconPdf());
		pdf.addClickHandler(event -> {
			AccountingReportParams params = getFilterPanel().getParams( options );
			ReportMetadata metadata = new ReportMetadata().setTitle("Listado de IVA");
			PrintReportDialog dialog = new PrintReportDialog(metadata
				, new IPrintReportDialogCallback() {
					
					@Override
					public void onError(String msg) {
						Window.alert(msg);
					}
					
					@Override
					public void onCancel() {
						// Nothing
					}
					
					@Override
					public void onAccept(ReportMetadata metadata) {
						params.setTitle(metadata.getTitle());
						params.setSubject(metadata.getSubject());
						params.setShowCover(metadata.isShowCover());
						params.setPageOffset(metadata.getPageOffset());
						params.setPageOffsetText(metadata.getPageOffsetText());
						params.setHideFilter(metadata.isHideFilter());
						params.setHeaderText(metadata.getHeaderText());
						params.setHideDateTimeOnFooter(metadata.isHideDateTimeOnFooter());
						params.setFooterText(metadata.getFooterText());

						diskForm.setAction(GWT.getHostPageBaseURL() + VAT_PDF_REPORT_PRINT);
						vatParamsHidden.setValue(JsonParams.convert(params));
						domainIdHidden.setValue(String.valueOf(options.getDomain()));
						domainNameHidden.setValue(options.getDomainName());
						userHidden.setValue(options.getUser());
						diskForm.submit();
					}
				});
			dialog.center();
			dialog.show();
		});
		toolbarPanel.add(pdf);
		
		final AonToolbarButton excel = new AonToolbarButton(AON.MSG.export(), AON.CSS.aonIconExcel());
		excel.addClickHandler(event -> submitForm(options, VAT_EXCEL_REPORT_PRINT));
		toolbarPanel.add(excel);

		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		vatParamsHidden = new Hidden(IRequestParamsNames.VAT_PARAMS);
		formFlowPanel.add(vatParamsHidden);
		domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
		formFlowPanel.add(domainIdHidden);
		domainNameHidden = new Hidden(IRequestParamsNames.DOMAIN_NAME);
		formFlowPanel.add(domainNameHidden);
		userHidden = new Hidden(IRequestParamsNames.USER);
		formFlowPanel.add(userHidden);
		toolbarPanel.add(diskForm);
		
		return toolbarPanel;
	}
	
	private void submitForm(VatReportModuleOptions options, String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		vatParamsHidden.setValue(JsonParams.convert(getFilterPanel().getParams(options)));
		domainIdHidden.setValue(String.valueOf(options.getDomain()));
		domainNameHidden.setValue(options.getDomainName());
		userHidden.setValue(options.getUser());
		diskForm.submit();
	}

	private void onSearch( VatReportModuleOptions options, AccountingReportParams params) {
		if (tabLayout.getSelectedIndex() == SUMMARY_TAB) {
			refreshSummary(options, params);
		} else {
			refreshResults(options, params);
		}
	}
	
	private void refreshResults(VatReportModuleOptions options, AccountingReportParams params) {
		vatPanelContainer.clear();
		vatPanelContainer.setWidget(new VatReportPanel(options, params));
	}

	private void refreshSummary(VatReportModuleOptions options, AccountingReportParams params) {
		vatSummaryContainer.clear();
		SERVICE.getVatSummaryContext(options.getOccam(), params , new AsyncCallback<LinkedList<VatSummaryContext>>() {
			
			@Override
			public void onSuccess(LinkedList<VatSummaryContext> result) {
				VatReportSummaryPanelCallback callback = new VatReportSummaryPanelCallback() {
					@Override
					public void refreshVatSummaryTypeFilter(VatSummaryType type) {
						VatReport.this.getFilterPanel().refreshVatSummaryType(type);
					}
					
					@Override
					public void refreshPercentFilter(Double percent) {
						VatReport.this.getFilterPanel().refreshPercent(percent);
					}
					
					@Override
					public void refreshOutputFilter(Boolean output) {
						VatReport.this.getFilterPanel().refreshOutput(output);
					}
					@Override
					public void refreshSurchargePercentFilter(Double surchargePercent) {
						VatReport.this.getFilterPanel().refreshSurchargePercent(surchargePercent);
					}
				};
				VatReportSummaryPanel summaryPanel = new VatReportSummaryPanel(params, callback, result);
				summaryPanel.addValueChangeHandler(e -> {
					tabLayout.selectTab(RESULTS_TAB, false);
					refreshResults(options, e.getValue());	
				});
				vatSummaryContainer.setWidget( summaryPanel );
			}

			@Override
			public void onFailure(Throwable caught) {
				Label error = new Label(AON.MSG.unexpectedError(caught.getMessage()));
				error.setStyleName(AON.CSS.aonMargin());
				error.addStyleName(AON.CSS.aonColorRed());
				error.addStyleName(AON.CSS.aonBold());
				vatSummaryContainer.setWidget(error);
			}
		});		
	}

	private VatReportFilterPanel getFilterPanel() {
		return this.filterPanel;
	}
}
