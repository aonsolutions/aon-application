package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonDataGrid;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class InvoiceReport extends MainEntryPoint {

	static FiscalServiceAsync fiscalService;
	
	final static CommonMessages MSG = GWT.create(CommonMessages.class);
	final static AonResources AON_RESOURCES = GWT.create(AonResources.class);
	final static DataGrid.Resources DATA_GRID_STYLE = GWT.create(AonDataGrid.class);
	
	interface InvoiceReportBinder extends UiBinder<Widget, InvoiceReport> {
	}

	private static final InvoiceReportBinder INVOICE_REPORT_BINDER = GWT
			.create(InvoiceReportBinder.class);

	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	ResultsPanel resultsPanel;
	@UiField
	MinimizePanel footPanel;
	@UiField
	Panel formContainer;
	
	@UiField
	CheckBox sales;

	@UiField
	CheckBox purchases;
	
	@UiField
	CheckBox expenses;
	
	@UiField
	CheckBox undeductibleExpenses;
	
	private int domain;
	private int enterprise;

	@UiField
	FormPanel diskForm;
	@UiField
	DateBoxEx fromDate;
	@UiField
	DateBoxEx toDate;
	
	@UiField
	Button generateFileButton;
	
	
	@UiField
	Hidden domainId;
	@UiField
	Hidden domainName;

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		AON_RESOURCES.css().ensureInjected();

		FiscalServiceAsync mod180ServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(mod180ServiceRaw);

		Widget ui = INVOICE_REPORT_BINDER.createAndBindUi(this);
		

		fromDate.getTextBox().setName("fromDate");
		toDate.getTextBox().setName("toDate");
		domainId.setName("domainId");
		domainName.setName("domainName");
		sales.setName("sales");
		purchases.setName("purchases");
		expenses.setName("expenses");
		undeductibleExpenses.setName("undeductibleExpenses");
		
		sales.setValue(true);
		purchases.setValue(true);
		expenses.setValue(true);
		undeductibleExpenses.setValue(true);
		
		Date date = new Date();
		CalendarUtil.setToFirstDayOfMonth(date);
		fromDate.setValue(date);
		
		date = new Date();
		CalendarUtil.setToFirstDayOfMonth(date);
		CalendarUtil.addMonthsToDate(date, 1);
		CalendarUtil.addDaysToDate(date, -1);
		toDate.setValue( date );
		
		
		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
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

	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		if (!sales.getValue() && !purchases.getValue() 
			&& !expenses.getValue() && !undeductibleExpenses.getValue()) {
			Window.alert("Si no selecciona ning\u00FAn tipo de factura, no obtendr\u00E1 resultados.");
		} else if (fromDate.getValue() != null && toDate.getValue() != null 
				&& fromDate.getValue().after(toDate.getValue())) {
			Window.alert("Si indica una fecha \"desde\" mayor que la fecha \"hasta\", no obtendr\u00E1 resultados.");
		} else {
			diskForm.setMethod(FormPanel.METHOD_POST);
			diskForm.setAction(GWT.getHostPageBaseURL() + "aon_gwt_fiscal/InvoiceReport");
			diskForm.setEncoding(FormPanel.ENCODING_URLENCODED);
			
			domainId.setValue(String.valueOf(getCurrentDomain()));
			domainName.setValue(getCurrentDomainName());
			diskForm.submit();
		}
	}

	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	@UiHandler("footPanel")
	void onFootMaximize(MinimizeEvent event) {
	}

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}

	private void maximizeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}
	
	private void showResultsPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 5);
	}

	private boolean isResultsPanelVisible() {
		return splitLayoutPanel.getWidgetSize(footPanel) > 0;
	}
	
	private void cleanErrorMessage() {
		resultsPanel.clearFlowPanel();
		closeFootPanel();
	}

	private void showErrorMessage(String msg) {
		showResultsPanel();
		addErrorMessage(msg);
	}

	private void addErrorMessage(String msg) {
		SimplePanel panel = new SimplePanel();
		Label label = new Label(msg);
		label.addStyleName("aon-icon-errorwarning");
		label.addStyleName("aon-message-error");
		label.addStyleName("aon-icon");
		panel.add(label);
		resultsPanel.setWidget(panel);
	}
}
