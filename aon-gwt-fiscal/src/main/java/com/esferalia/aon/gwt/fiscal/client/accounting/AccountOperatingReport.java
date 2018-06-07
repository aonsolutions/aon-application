package com.esferalia.aon.gwt.fiscal.client.accounting;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.OperatingPanelReport;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;

public class AccountOperatingReport extends MainEntryPoint {
	
	private static final String ACC_OPERATING_REPORT_PRINT = "/aon_gwt_fiscal/roms/AccountOperatingReportExcelPrint";
	
	@Override
	public void onModuleLoad() {
		
		AON.ensureInjected();
		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		OperatingPanelReport panel = new OperatingPanelReport(getCurrentDomainName(), getCurrentUser(), getCurrentDomain());
		
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
		toolbar.setWidget(0, 0, new Label("Cuenta de explotaci\u00F3n"));
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

		final Button print = new Button();
		print.setText(AON.MSG.print());
		print.setTitle(AON.MSG.print());
		print.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		print.addStyleName(AON.AON_CSS.aonIconPrinter());
		print.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				diskForm.setAction(GWT.getHostPageBaseURL() + ACC_OPERATING_REPORT_PRINT);
				accountReportParamsHidden.setValue(JsonParams.convert(panel.getWidgetParams()));
				domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
				domainNameHidden.setValue(getCurrentDomainName());
				userHidden.setValue(getCurrentUser());
				diskForm.submit();
			}
		});
		buttonContainer.add(print);

		toolbarPanel.add(toolbar);
		
		dockLayoutPanel.addNorth(toolbarPanel, 25);
		dockLayoutPanel.add( panel );
		
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(dockLayoutPanel);
	}

}
