package com.esferalia.aon.gwt.connect.client;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.connect.client.DSIImportClient.DSIImportCallback;
import com.esferalia.aon.gwt.connect.shared.JsEmployee;
import com.esferalia.aon.gwt.connect.shared.JsEmpres;
import com.esferalia.aon.gwt.connect.shared.JsImportEvent;
import com.esferalia.aon.gwt.connect.shared.JsImportEvent.EventTypeVisitor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.ListDataProvider;

public class DSIImportForm implements EntryPoint {

	interface Binder extends UiBinder<Widget, DSIImportForm> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	DateBox dateBox;
	@UiField
	FormPanel uploadFormPanel;
	@UiField
	TabLayoutPanel footTabPanel;
	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	MinimizePanel footPanel;
	@UiField
	FileUpload fileUpload;
	@UiField
	Button sendButton;
	@UiField
	ScrollPanel sPanel;
	@UiField
	VerticalPanel vPanel;
	@UiField
	FlexTable layout;
	@UiField
	SimplePanel simplePanel;
	@UiField
	ResultsPanel resultsPanel;

	private JsArray<JsEmpres> empress;

	private List<DSIImportResult> results;

	@Override
	public void onModuleLoad() {

		// Inject rich styles.
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();

		// Create the UI defined in Employee.ui.xml.
		Widget ui = binder.createAndBindUi(this);

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		sPanel.setVisible(false);

		init();
	}

	protected void init() {

		dateBox.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));

		uploadFormPanel.setAction("/aon_gwt_connect/dsiimport");
		uploadFormPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadFormPanel.setMethod(FormPanel.METHOD_POST);

		uploadFormPanel.getElement().setDraggable("DRAGGABLE_TRUE");

		DSIImportResultsGrid resultsGrid = new DSIImportResultsGrid();

		ListDataProvider<DSIImportResult> listDataProvider = new ListDataProvider<DSIImportResult>();
		listDataProvider.addDataDisplay(resultsGrid);
		this.results = listDataProvider.getList();

		resultsPanel.setWidget(resultsGrid);

	}

	private void showEnterprisesList() {

		sPanel.getElement().getStyle().setBackgroundColor("#FFFFFF");
		vPanel.add(createAdvancedForm());
		sPanel.setVisible(true);
	}

	private Widget createAdvancedForm() {
		FlexCellFormatter cellFormater = layout.getFlexCellFormatter();
		layout.clear();
		layout.getElement().getStyle().setBackgroundColor("#FFFFFF");

		// Create some advanced options

		CheckBox selectAll = new CheckBox();
		selectAll.setTitle("Seleccionar todas");

		layout.setWidget(0, 0, selectAll);

		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
		String[] words = { "Vitoria", "Bilbao", "Pamplona", "San Sebastian",
				"Santander" };
		oracle.add(words[0]);
		oracle.add(words[1]);
		oracle.add(words[2]);
		oracle.add(words[3]);
		final SuggestBox suggest = new SuggestBox(oracle);
		layout.setWidget(0, 1, suggest);
		Grid advancedOptions = new Grid(12, 2);
		advancedOptions.setCellSpacing(6);
		advancedOptions.setWidget(0, 0, new CheckBox());
		advancedOptions.setHTML(0, 1, "Nombre empresa 1");
		advancedOptions.setWidget(1, 0, new CheckBox());
		advancedOptions.setHTML(1, 1, "Nombre empresa 2");
		advancedOptions.setWidget(2, 0, new CheckBox());
		advancedOptions.setHTML(2, 1, "Nombre empresa 3");
		advancedOptions.setWidget(3, 0, new CheckBox());
		advancedOptions.setHTML(3, 1, "Nombre empresa 4");
		// ****
		advancedOptions.setWidget(4, 0, new CheckBox());
		advancedOptions.setHTML(4, 1, "Nombre empresa 5");
		advancedOptions.setWidget(5, 0, new CheckBox());
		advancedOptions.setHTML(5, 1, "Nombre empresa 6");
		advancedOptions.setWidget(6, 0, new CheckBox());
		advancedOptions.setHTML(6, 1, "Nombre empresa 7");
		advancedOptions.setWidget(7, 0, new CheckBox());
		advancedOptions.setHTML(7, 1, "Nombre empresa 8");
		// ****
		advancedOptions.setWidget(8, 0, new CheckBox());
		advancedOptions.setHTML(8, 1, "Nombre empresa 9");
		advancedOptions.setWidget(9, 0, new CheckBox());
		advancedOptions.setHTML(9, 1, "Nombre empresa 10");
		advancedOptions.setWidget(10, 0, new CheckBox());
		advancedOptions.setHTML(10, 1, "Nombre empresa 11");
		advancedOptions.setWidget(11, 0, new CheckBox());
		advancedOptions.setHTML(11, 1, "Nombre empresa 12");

		// Add advanced options to form in a disclosure panel

		DisclosurePanel advancedDisclosure = new DisclosurePanel(
				"Listado de Empresas: ");
		advancedDisclosure.setOpen(true);
		advancedDisclosure.setAnimationEnabled(true);
		advancedDisclosure.setContent(advancedOptions);
		layout.setWidget(3, 0, advancedDisclosure);
		cellFormater.setColSpan(3, 0, 2);

		simplePanel.setWidget(layout);
		return simplePanel;
	}

	private void showResultsPanel() {

		InlineLabel resultsTab = new InlineLabel("Resultados");
		resultsTab.addStyleName(AON.AON_ICON_TIME);
		resultsTab.addStyleName(AON.AON_ICON_CMD_BUTTON);

		DSIImportForm.this.footTabPanel.add(DSIImportForm.this.resultsPanel,
				resultsTab);

		DSIImportForm.this.splitLayoutPanel.setWidgetSize(
				DSIImportForm.this.footPanel, Window.getClientHeight() / 4);
	}

	// ------------------------------------------------------------- UiHandlers

	@UiHandler("fileUpload")
	void onChangeFileUpload(ChangeEvent event) {
		uploadFormPanel.submit();
	}

	@UiHandler("uploadFormPanel")
	void onSubmitUpload(SubmitEvent event) {
	}

	@UiHandler("uploadFormPanel")
	void onSubmitCompleteUpload(SubmitCompleteEvent event) {
		String json = event.getResults();
		JsArrayString dbs = JsonUtils.safeEval(json);
		DSIImportClient.getEmpress(dbs,
				new DSIImportCallback<JsArray<JsEmpres>>() {

					@Override
					public void onError(Throwable t) {
						// TODO Show Dialog, Error at 'resultsPanel' or both.
						// it's up to you
					}

					@Override
					public void onSuccess(JsArray<JsEmpres> empress) {
						DSIImportForm.this.empress = empress;
						// TODO Loads and shows enterprises list.
					}

				});
	}

	@UiHandler("sendButton")
	void onClickSendButton(ClickEvent event) {
		// TODO I pass all enterprises, you must pass only selected ones.
		DSIImportClient.imp0rt(empress, new DSIImportCallback<JsImportEvent>() {

			@Override
			public void onError(Throwable t) {
				// TODO Show Dialog, Error at 'resultsPanel' or both. It's up to
				// you
			}

			@Override
			public void onSuccess(JsImportEvent event) {
				DSIImportResult result = event
						.visit(new EventTypeVisitor<DSIImportResult>() {

							@Override
							public DSIImportResult onCommitted() {
								// TODO Auto-generated method stub
								return null;
							}

							@Override
							public DSIImportResult onRollbacked() {
								// TODO Auto-generated method stub
								return null;
							}

							@Override
							public DSIImportResult onEmployeeIgnored(
									JsEmployee employee) {
								return new DSIImportResultsGrid.EmployeeDSIImportResult(
										employee.getFullName());
							}

							@Override
							public DSIImportResult onEmployeeUpdated(
									JsEmployee employee) {
								return new DSIImportResultsGrid.EmployeeDSIImportResult(
										employee.getFullName());
							}

							@Override
							public DSIImportResult onEmployeeInserted(
									JsEmployee employee) {
								return new DSIImportResultsGrid.EmployeeDSIImportResult(
										employee.getFullName());
							}

							@Override
							public DSIImportResult onEnterpriseIgnored(
									JsEmpres empres) {
								return new DSIImportResultsGrid.EnterpriseDSIImportResult(
										empres.getRSocial());
							}

							@Override
							public DSIImportResult onEnterpriseUpdated(
									JsEmpres empres) {
								return new DSIImportResultsGrid.EnterpriseDSIImportResult(
										empres.getRSocial());
							}

							@Override
							public DSIImportResult onEnterpriseInserted(
									JsEmpres empres) {
								return new DSIImportResultsGrid.EnterpriseDSIImportResult(
										empres.getRSocial());
							}

						});
				if (result != null)
					results.add(result);
			}

		});
	}

	// ------------------------------------------------------------------------

}
