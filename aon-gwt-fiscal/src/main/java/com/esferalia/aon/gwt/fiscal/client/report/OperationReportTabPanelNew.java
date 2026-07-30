package com.esferalia.aon.gwt.fiscal.client.report;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.fiscal.OperationParamsNew;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.xhr.client.XMLHttpRequest;

class OperationReportTabPanelNew extends ScrollPanel{
		
	private static final String REPORT_URL = URL.encode(GWT.getModuleBaseURL() + "roms/AccountingOperationReportStreamNew");
	
	private FlowPanel rootPanel = new FlowPanel();
	
	public OperationReportTabPanelNew(OperationReportModuleOptionsNew options, OperationParamsNew params, JsOperationGridPanelNew grid) {
		setStyleName(AON.CSS.aonScrollArea());
		addStyleName("salary-scroll"); // Forzar scroll horizontal visible
		
		rootPanel.add(grid);
		setWidget(rootPanel);
		
		grid.setParams(params);
		grid.addSelectionHandler(event -> showEntry(options, event.getSelectedItem().getEntryId()));
		
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add(new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, REPORT_URL);
		xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(xhrt -> {
			if (xhr.getReadyState() == XMLHttpRequest.DONE) {
				JavaScriptObject arrayObject = JsonUtils.safeEval(xhr.getResponseText());
				JsArray<JsOperationBreakdownNew> array = arrayObject.cast();
				for (int i = 0; i < array.length(); i++) {
					grid.addRow(array.get(i));			
				}
				popup.hide();
				grid.addFooterRow();
			}
		});
		
		StringBuilder requestData = new StringBuilder();
		requestData.append("&domainName=" + options.getDomainName() );
		requestData.append("&domainId=" + options.getDomain() );
		requestData.append("&user=" + options.getUser() );
		requestData.append("&operationParams=" + JsonParams.convert(params));
		xhr.send(requestData.toString());
		
	}
	
	private void showEntry(OperationReportModuleOptionsNew options, Integer entryId) {
		
		// Si no hay entryId, o es cero, no hacemos nada
		if (entryId == null || entryId.intValue() == 0) {
			return;
		}		
		
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption(AON.MSG.accountEntries());
		AccountEntryModule module = new AccountEntryModule();
		module.onModuleLoad(new AccountEntryModuleOptions()
			.setParentWidget(entryDialog)
			.setDomainName(options.getDomainName())
			.setUser(options.getUser())
			.setDomain(options.getDomain())
			.setAccountEntryId(entryId)
			.setSessionLogTabVisible(false)
			.setJournalTabVisible(false)
			.setExtraInfoTabVisible(false)
			.setExternalCallback( new ModuleCallback() {
				
				private static final long serialVersionUID = -7040645114567563036L;

				@Override
				public void onRemove(IAccountEntryWrapper removed) {
					entryDialog.hide();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					entryDialog.hide();
				}
				
				@Override
				public void onExit() {
					entryDialog.hide();
				}
				
				@Override
				public void onChange(IAccountEntryWrapper changed) {
					entryDialog.hide();
				}
			})
		);
		entryDialog.center();
		entryDialog.show();
	}

}
