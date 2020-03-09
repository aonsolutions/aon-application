package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class InvoiceSeriesBreakdown extends MainEntryPoint {

	static FinanceServiceAsync financeService;
	
	final static CommonMessages MSG = GWT.create(CommonMessages.class);
	final static AonResources AON_RESOURCES = GWT.create(AonResources.class);
	
	interface InvoiceSeriesBreakdownBinder extends UiBinder<Widget, InvoiceSeriesBreakdown> {
	}

	private static final InvoiceSeriesBreakdownBinder MODEL_140_BINDER = GWT
			.create(InvoiceSeriesBreakdownBinder.class);

	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	ResultsPanel resultsPanel;
	@UiField
	MinimizePanel footPanel;
	@UiField
	Panel formContainer;

	private int domain;
	private int enterprise;

	@UiField
	SimpleLayoutPanel container;

	@UiField
	DateBoxEx fromDate;
	@UiField
	DateBoxEx toDate;
	
	@UiField
	Button showReport;

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		financeService = new FinanceServiceAsyncDecorator(financeServiceRaw);

		Widget ui = MODEL_140_BINDER.createAndBindUi(this);
		
		fromDate.setValue(new Date());
		toDate.setValue(new Date());
		
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

	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}

	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2);
		splitLayoutPanel.animate(500);
	}

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 30);
		splitLayoutPanel.animate(500);
	}

	private void openFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
		splitLayoutPanel.animate(500);
	}
	
	private void showResultsPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 5);
	}

	private boolean isResultsPanelVisible() {
		return splitLayoutPanel.getWidgetSize(footPanel) > 0;
	}
	
	private void cleanErrorMessage() {
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
	
	@UiHandler("showReport")
	void onShowReportClick(ClickEvent event) {
		financeService.getInvoiceSeries(getCurrentDomainName(), getCurrentDomain(), getCurrentUser()
				, fromDate.getValue(), toDate.getValue(), false, new AsyncCallback<LinkedList<InvoiceSeries>>() {
			
			@Override
			public void onSuccess(LinkedList<InvoiceSeries> result) {
				FlowPanel cont = new  FlowPanel();
				cont.setStyleName(AON.AON_CSS.aonWidthAll());
				cont.addStyleName(AON.AON_CSS.aonTextCenter());
				
				if (result != null && !result.isEmpty() ) {
					FlexTable tab = new FlexTable();
					tab.setCellPadding(0);
					tab.setCellSpacing(0);
					tab.setStyleName(AON.AON_CSS.aonPanelGrid());
					tab.addStyleName(AON.AON_CSS.aonFiscalMatrix());
					tab.addStyleName(AON.AON_CSS.aonMarginTop());
					
					tab.getColumnFormatter().setWidth(0, "250px");
					tab.getColumnFormatter().setWidth(1, "100px");
					tab.getColumnFormatter().setWidth(2, "100px");
					tab.getColumnFormatter().setWidth(3, "100px");
					
					
					
					tab.setWidget(0, 1, new Label("SERIE"));
					tab.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonPanelGridOdd());
					
					tab.setWidget(0, 2, new Label("DESDE"));
					tab.getCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonPanelGridOdd());
					
					tab.setWidget(0, 3, new Label("HASTA"));
					tab.getCellFormatter().setStyleName(0, 3, AON.AON_CSS.aonPanelGridOdd());
					
					int row = 1;
					for (InvoiceSeries is : result) {
						if (is.isSeriesInfo()) {
							tab.setWidget(row, 0, new Label(is.isSales()?"EMITIDAS":"RECIBIDAS"));
							tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
							
							tab.setWidget(row, 1, new Label(is.getDescription()));
							tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
							
							tab.setWidget(row, 2, new Label(AonNumberUtils.toString(is.getFromNumber())));
							tab.getCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonPanelGridEven());
							
							tab.setWidget(row, 3, new Label(AonNumberUtils.toString(is.getToNumber())));
							tab.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonPanelGridEven());
						}						
						++row;
					}
					cont.add(tab);
					
					tab = new FlexTable();
					tab.setCellPadding(0);
					tab.setCellSpacing(0);
					tab.setStyleName(AON.AON_CSS.aonPanelGrid());
					tab.addStyleName(AON.AON_CSS.aonFiscalMatrix());
					tab.addStyleName(AON.AON_CSS.aonMarginTop());
					
					tab.getColumnFormatter().setWidth(0, "250px");
					tab.getColumnFormatter().setWidth(1, "100px");
					tab.getColumnFormatter().setWidth(2, "100px");
					
					tab.setWidget(0, 1, new Label("TIPO"));
					tab.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonPanelGridOdd());
					
					tab.setWidget(0, 2, new Label("CANTIDAD"));
					tab.getCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonPanelGridOdd());
					
					row = 1;
					for (InvoiceSeries is : result) {
						if (!is.isSeriesInfo()) {
							tab.setWidget(row, 0, new Label(is.isSales()?"EMITIDAS":"RECIBIDAS"));
							tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
							
							tab.setWidget(row, 1, new Label(is.getDescription()));
							tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
							
							tab.setWidget(row, 2, new Label(AonNumberUtils.toString(is.getFromNumber())));
							tab.getCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonPanelGridEven());
						}						
						++row;
					}
					cont.add(tab);
					
				}
				container.setWidget(cont);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(AON.MSG.unexpectedError(caught.getMessage()));
			}
		});
	}

}
