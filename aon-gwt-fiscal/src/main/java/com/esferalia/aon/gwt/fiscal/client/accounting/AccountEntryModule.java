package com.esferalia.aon.gwt.fiscal.client.accounting;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonDataGrid;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AccountEntryModule extends MainEntryPoint {

	static FiscalServiceAsync fiscalService;
	
	final static CommonMessages MSG = GWT.create(CommonMessages.class);
	final static AonResources AON_RESOURCES = GWT.create(AonResources.class);
	final static DataGrid.Resources DATA_GRID_STYLE = GWT.create(AonDataGrid.class);
	
	interface AccountEntryModuleBinder extends UiBinder<Widget, AccountEntryModule> {
	}
	private static final AccountEntryModuleBinder BINDER = GWT.create(AccountEntryModuleBinder.class);

	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	ResultsPanel resultsPanel;
	@UiField
	MinimizePanel footPanel;
	
	@UiField(provided=true)
	AccountBox account;

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		AON_RESOURCES.css().ensureInjected();
		FiscalServiceAsync mod180ServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(mod180ServiceRaw);
		
		account = new AccountBox(getCurrentDomainName(),getCurrentDomain()); 
		
		Widget ui = BINDER.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		
		resultsPanel.setFlowPanelVisible(false);
	}
	

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	// -------------------------------------------------------------- UiHandler

	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	@UiHandler("footPanel")
	void onFootMaximize(MinimizeEvent event) {
	}

	private void closeFootPanel() {
		dockLayoutPanel.setWidgetSize(footPanel, 0);
	}
	
	private void showResultsPanel() {
		dockLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
	}
}
