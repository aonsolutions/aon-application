package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.accounting.PrintReportDialog.IPrintReportDialogCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.JournalPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.JournalPanelReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.utilities.CustomPopup;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;

public class AccountJournalReport extends MainEntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(JournalPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final String ACC_JORNAL_REPORT_EXCEL_PRINT = "/aon_gwt_fiscal/roms/AccountJournalReportExcelPrint";
	private static final String ACC_JORNAL_REPORT_PDF_PRINT = "/aon_gwt_fiscal/roms/AccountJournalReportPDFPrint";
	private static final String ACC_JORNAL_FLAT_REPORT_PRINT = "/aon_gwt_fiscal/roms/AccountJournalFlatReportExcelPrint";
// 	private static final String ACCOUNT_ENTRY_STREAM_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/AccountEntryFlatStreamServlet");
	
	@Override
	public void onModuleLoad() {
		
		AON.ensureInjected();
		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		JournalPanelReport panel = new JournalPanelReport(getCurrentDomainName(), getCurrentUser(), getCurrentDomain());
		
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
		toolbar.setWidget(0, 0, new Label("Listado diario de movimientos"));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		
		FormPanel diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		Hidden accountEntryParamsHidden = new Hidden(IRequestParamsNames.ACCOUNT_ENTRY_PARAMS);
		Hidden domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
		Hidden domainNameHidden= new Hidden(IRequestParamsNames.DOMAIN_NAME);
		Hidden userHidden = new Hidden(IRequestParamsNames.USER);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(accountEntryParamsHidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		buttonContainer.add(diskForm);

		final Button pdf = new Button();
		pdf.setText(AON.MSG.print());
		pdf.setTitle(AON.MSG.print());
		pdf.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		pdf.addStyleName(AON.AON_CSS.aonIconPdf());
		pdf.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AccountEntryParams params = panel.getWidgetParams();
				ReportMetadata metadata = new ReportMetadata().setTitle("Listado diario de movimientos");
				PrintReportDialog dialog = new PrintReportDialog(metadata
						, new IPrintReportDialogCallback() {
							
							@Override
							public void onError(String msg) {
								Window.alert(msg);
							}
							
							@Override
							public void onCancel() {}
							
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

								diskForm.setAction(GWT.getHostPageBaseURL() + ACC_JORNAL_REPORT_PDF_PRINT);
								accountEntryParamsHidden.setValue(JsonParams.convert(params));
								domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
								domainNameHidden.setValue(getCurrentDomainName());
								userHidden.setValue(getCurrentUser());
								diskForm.submit();
							}
						});
				dialog.center();
				dialog.show();
			}
		});
		pdf.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
			}
		});
		buttonContainer.add(pdf);

		final Button print = new Button();
		print.setText(AON.MSG.print());
		print.setTitle(AON.MSG.print());
		print.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		print.addStyleName(AON.AON_CSS.aonIconExcel());
		print.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				diskForm.setAction(GWT.getHostPageBaseURL() + ACC_JORNAL_REPORT_EXCEL_PRINT);
				accountEntryParamsHidden.setValue(JsonParams.convert(panel.getWidgetParams()));
				domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
				domainNameHidden.setValue(getCurrentDomainName());
				userHidden.setValue(getCurrentUser());
				diskForm.submit();
			}
		});
		buttonContainer.add(print);

		final Button excel = new Button();
		excel.setText(AON.MSG.export());
		excel.setTitle(AON.MSG.export());
		excel.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		excel.addStyleName(AON.AON_CSS.aonIconExcel());
		excel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				diskForm.setAction(GWT.getHostPageBaseURL() + ACC_JORNAL_FLAT_REPORT_PRINT);
				accountEntryParamsHidden.setValue(JsonParams.convert(panel.getWidgetParams()));
				domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
				domainNameHidden.setValue(getCurrentDomainName());
				userHidden.setValue(getCurrentUser());
				diskForm.submit();
			}
		});
		buttonContainer.add(excel);

		toolbarPanel.add(toolbar);
		
		
		dockLayoutPanel.addNorth(toolbarPanel, 25);
		dockLayoutPanel.add( panel );
		
		
		panel.addSelectionHandler(new AccountEntrySelectionHandler() {
			
			@Override
			public void onSelection(AccountEntrySelectionEvent event) {
				AccountEntry entry = event.getSelectedItem();
				showEntry(entry.getDomain(), entry.getId(), event.getCallback());
				
			}
		});
		
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(dockLayoutPanel);
	}
	
//	private void showViewer(String dataURI) {
//		CustomPopup viewerDialog = new CustomPopup();
//		String width = (Window.getClientWidth() - 100) + "px";
//		viewerDialog.setWidth(width);
//		viewerDialog.setHeight((Window.getClientHeight() - 100) + "px");
//		viewerDialog.setAnimationEnabled(true);
//		viewerDialog.setGlassEnabled(true);
//		viewerDialog.setModal(true);
//		viewerDialog.setCaption("Visor PDF");
//		Viewer viewer = new Viewer();
//		viewer.setDocument(dataURI, 1.95 );
//		viewerDialog.center();
//		viewerDialog.show();
//		ScrollPanel scrollPanel = new ScrollPanel();
//		viewerDialog.setWidth(width);
//		scrollPanel.add(viewer);
//		viewer.setWidth(width);
//		viewerDialog.add(scrollPanel);
//	}

	private void showEntry(int domain,Integer entryId, ModuleCallback moduleCallback) {
		CustomPopup entryDialog = new CustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption(AON.MSG.accountEntries());
		AccountEntryModule module = new AccountEntryModule();
		module.onModuleLoad(
			new AccountEntryModuleOptions()
			.setParentWidget(entryDialog)
			.setDomainName(getCurrentDomainName())
			.setUser(getCurrentUser())
			.setDomain(domain)
			.setAccountEntryId(entryId)
			.setExternalCallback(new ModuleCallback() {
				@Override
				public void onRemove(IAccountEntryWrapper removed) {
					entryDialog.hide();
					moduleCallback.onRemove(removed);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					moduleCallback.onFailure(caught);
				}
				
				@Override
				public void onExit() {
					entryDialog.hide();
					moduleCallback.onExit();
				}
				
				@Override
				public void onChange(IAccountEntryWrapper changed) {
					entryDialog.hide();
					moduleCallback.onChange(changed);
				}
			})
		);
		entryDialog.center();
		entryDialog.show();
	}
 
}
