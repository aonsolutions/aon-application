package com.esferalia.aon.gwt.fiscal.client.accounting;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.StatementPanelReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.utilities.CustomPopup;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;

public class AccountStatementReport extends MainEntryPoint {
	
	@Override
	public void onModuleLoad() {
		
		AON.ensureInjected();
		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		StatementPanelReport panel = new StatementPanelReport(getCurrentDomainName(), getCurrentUser(), getCurrentDomain(), true);
		
		
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
		toolbar.setWidget(0, 0, new Label("Extracto de cuentas"));
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

//		final Button print = new Button();
//		print.setText(AON.MSG.print());
//		print.setTitle(AON.MSG.print());
//		print.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
//		print.addStyleName(AON.AON_CSS.aonIconPrinter());
//		print.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				diskForm.setAction(GWT.getHostPageBaseURL() + ACC_JORNAL_REPORT_PRINT);
//				accountEntryParamsHidden.setValue(JsonParams.convert(panel.getWidgetParams()));
//				domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
//				domainNameHidden.setValue(getCurrentDomainName());
//				userHidden.setValue(getCurrentUser());
//				diskForm.submit();
//			}
//		});
//		buttonContainer.add(print);
//
//		final Button excel = new Button();
//		excel.setText(AON.MSG.export());
//		excel.setTitle(AON.MSG.export());
//		excel.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
//		excel.addStyleName(AON.AON_CSS.aonIconExcel());
//		excel.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				diskForm.setAction(GWT.getHostPageBaseURL() + ACC_JORNAL_FLAT_REPORT_PRINT);
//				accountEntryParamsHidden.setValue(JsonParams.convert(panel.getWidgetParams()));
//				domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
//				domainNameHidden.setValue(getCurrentDomainName());
//				userHidden.setValue(getCurrentUser());
//				diskForm.submit();
//			}
//		});
//		buttonContainer.add(excel);

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
		
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(dockLayoutPanel);
	}

	private void showEntry(int domain,Integer entryId, ModuleCallback<AccountEntry> callback) {
		CustomPopup entryDialog = new CustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption(AON.MSG.accountEntries());
		AccountEntryModule module = new AccountEntryModule();
		module.onModuleLoad(entryDialog, getCurrentDomainName(), getCurrentUser(), domain, entryId, new ModuleCallback<AccountEntry>() {
			
			@Override
			public void onRemove(AccountEntry removed) {
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
			public void onChange(AccountEntry changed) {
				entryDialog.hide();
				callback.onRemove(changed);
			}
		});
		entryDialog.center();
		entryDialog.show();
	}
	
}
