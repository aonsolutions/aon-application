package com.esferalia.aon.gwt.fiscal.client.accounting;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.accounting.PrintReportDialog.IPrintReportDialogCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.ConsolidatedBalancePanelReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.utilities.CustomPopup;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;

public class AccountConsolidatedBalanceReport extends MainEntryPoint {
	
	private static final String ACC_BALANCE_REPORT_PDF_PRINT = "/aon_gwt_fiscal/roms/AccountBalanceReportPDFPrint";
	private static final String ACC_BALANCE_REPORT_EXCEL_PRINT = "/aon_gwt_fiscal/roms/AccountBalanceReportExcelPrint";
	
	@Override
	public void onModuleLoad() {
		
		AON.ensureInjected();
		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		ConsolidatedBalancePanelReport panel = new ConsolidatedBalancePanelReport(getCurrentDomainName(), getCurrentUser(), getCurrentDomain());
		
		panel.addSelectionHandler(new AccountEntrySelectionHandler() {
			
			@Override
			public void onSelection(AccountEntrySelectionEvent event) {
				AccountEntry entry = event.getSelectedItem();  
				showEntry(entry.getDomain(), entry.getId(), event.getCallback());
				
			}
		});
		
		
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
		toolbar.setWidget(0, 0, new Label("Balances contables consolidados"));
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
		Hidden accountReportParamsHidden = new Hidden(IRequestParamsNames.ACCOUNT_REPORT_PARAMS);
		Hidden domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
		Hidden domainNameHidden= new Hidden(IRequestParamsNames.DOMAIN_NAME);
		Hidden userHidden = new Hidden(IRequestParamsNames.USER);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(accountReportParamsHidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		buttonContainer.add(diskForm);

		final Button pdfPrint = new Button();
		pdfPrint.setText(AON.MSG.print());
		pdfPrint.setTitle(AON.MSG.print());
		pdfPrint.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		pdfPrint.addStyleName(AON.AON_CSS.aonIconPdf());
		pdfPrint.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AccountingReportParams params = panel.getWidgetParams();
				ReportMetadata metadata = new ReportMetadata().setTitle(params.getBalanceType().getName());
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

								diskForm.setAction(GWT.getHostPageBaseURL() + ACC_BALANCE_REPORT_PDF_PRINT);
								accountReportParamsHidden.setValue(JsonParams.convert(params));
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
		
		buttonContainer.add(pdfPrint);

		final Button excelPrint = new Button();
		excelPrint.setText(AON.MSG.print());
		excelPrint.setTitle(AON.MSG.print());
		excelPrint.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		excelPrint.addStyleName(AON.AON_CSS.aonIconExcel());
		excelPrint.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				diskForm.setAction(GWT.getHostPageBaseURL() + ACC_BALANCE_REPORT_EXCEL_PRINT);
				accountReportParamsHidden.setValue(JsonParams.convert(panel.getWidgetParams()));
				domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
				domainNameHidden.setValue(getCurrentDomainName());
				userHidden.setValue(getCurrentUser());
				diskForm.submit();
			}
		});
		buttonContainer.add(excelPrint);

		toolbarPanel.add(toolbar);
		
		dockLayoutPanel.addNorth(toolbarPanel, 25);
		dockLayoutPanel.add( panel );
		
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(dockLayoutPanel);
	}

	private void showEntry(int domain,Integer entryId, ModuleCallback callback) {
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
						callback.onRemove(removed);
					}
					
					@Override
					public void onFailure(Throwable caught) {}
					
					@Override
					public void onExit() {
						entryDialog.hide();
					}
					
					@Override
					public void onChange(IAccountEntryWrapper changed) {
						entryDialog.hide();
						callback.onRemove(changed);
					}
		}));
		entryDialog.center();
		entryDialog.show();
	}
}
