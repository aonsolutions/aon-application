package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.CalculateService;
import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class MainCalculator extends MainEntryPoint implements CalculateService {

	static interface Binder extends UiBinder<Widget, MainCalculator> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	private static DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat(CalculateService.DATE_FORMAT_PATTERN);

	private static final DateTimeFormat MONTH_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH);

	static String CALC_URL = URL.encode(GWT.getModuleBaseURL() + "calculate");

	@UiField
	Button calcButton;

	@UiField
	MinimizePanel footPanel;

	@UiField
	ResultsPanel resultsPanel;

	@UiField
	MonthListBox monthListBox;

	@UiField
	SplitLayoutPanel splitLayoutPanel;


	@UiField
	SelectDataGrid<Enterprise> enterpriseDataGrid;

	private EnterprisesServiceAsync enterprisesService;

	@Override
	public void onModuleLoad() {

		// Inject rich styles.
		GWT.<MainEntryPoint.GWTResources> create(
				MainEntryPoint.GWTResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.AonResources> create(
				MainEntryPoint.AonResources.class).css().ensureInjected();

		// Create the UI defined in Employee.ui.xml.
		Widget ui = binder.createAndBindUi(this);

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);

		monthListBox.setDateTimeFormat(MONTH_FORMAT);

		// Create a remote service proxy to talk to the server-side Enterprises
		// service.
		EnterprisesServiceAsync gwtEnterprisesService = GWT
				.create(EnterprisesService.class);
		enterprisesService = new EnterprisesServiceAsyncDecorator(
				gwtEnterprisesService);

		// It's a bit tricky, you're free to change, but look it it's pretty
		// isn't it.
		(new AsyncDataProvider<Enterprise>() {
			// called when the table requests a new range of data. You can push
			// data back to the displays using.
			@Override
			protected void onRangeChanged(HasData<Enterprise> display) {
				// Get the new range.
				final Range range = display.getVisibleRange();
				// Query the data asynchronously.
				enterprisesService.getEnterprises(range.getStart(),
						range.getLength(),
						new AsyncCallback<List<Enterprise>>() {

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onSuccess(List<Enterprise> result) {
								// Push the data to the displays.
								// AsyncDataProvider will only update
								// displays that are within range of the data.
								updateRowData(range.getStart(), result);
							}
						});
			}
		}).addDataDisplay(enterpriseDataGrid);

		monthListBox.setSelectedMonth(new Date());
	}

	// -------------------------------------------------------------------------

	@UiHandler("calcButton")
	void onCalcButtonClicked(ClickEvent click) {
		resultsPanel.clear();
		calculate();
		if ( !isResultsPanelVisible() ) 
			showResultsPanel();
	}

	// -------------------------------------------------------------------------

	private void calculate() {
		Date month = monthListBox.getSelectedMonth();

		StringBuffer requestDataBuffer = new StringBuffer();
		requestDataBuffer.append(START_DATE + "="
				+ DATE_FORMAT.format(DateUtils.getFirstDayOfMonth(month)));
		requestDataBuffer.append("&" + END_DATE + "="
				+ DATE_FORMAT.format(DateUtils.getLastDayOfMonth(month)));
		requestDataBuffer.append("&" + ISSUE_DATE + "="
				+ DATE_FORMAT.format(DateUtils.getLastDayOfMonth(month)));

		if (!enterpriseDataGrid.isAllSelected())
			for (Enterprise enterprise : enterpriseDataGrid.getSelectedItems())
				requestDataBuffer.append("&" + ENPERPRISES + "="
						+ enterprise.getId());
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("POST", CALC_URL);
		xhr.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");

		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {

			private int loaded = 0;

			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();
				if (state == XMLHttpRequest.LOADING
						|| state == XMLHttpRequest.DONE) {

					String text = xhr.getResponseText();
					String html = text.substring(loaded);
					resultsPanel.addHTML(html);
					loaded = text.length();
				}
			}
		});

		xhr.send(requestDataBuffer.toString());

	}
	
	private void showResultsPanel() {
		splitLayoutPanel.setWidgetSize(
				footPanel, Window.getClientHeight() / 4);
	}
	
	private boolean isResultsPanelVisible() {
		return splitLayoutPanel.getWidgetSize(footPanel) > 0;
	}
	

}
