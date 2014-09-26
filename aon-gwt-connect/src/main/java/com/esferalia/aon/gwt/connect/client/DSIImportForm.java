package com.esferalia.aon.gwt.connect.client;

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

		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();

		Widget ui = binder.createAndBindUi(this);

		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);		
		sPanel.setVisible(false);

		init();		
	}

	protected void init() {
		
		sPanel.setVisible(false);

		dateBox.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));
		
		uploadFormPanel.setAction("/aon-aio/aon_gwt_connect/dsiimport");
		
		uploadFormPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadFormPanel.setMethod(FormPanel.METHOD_POST);

		uploadFormPanel.getElement().setDraggable("DRAGGABLE_TRUE");
		
		DSIImportResultsGrid resultsGrid = new DSIImportResultsGrid();

		ListDataProvider<DSIImportResult> listDataProvider = new ListDataProvider<DSIImportResult>();
		listDataProvider.addDataDisplay(resultsGrid);
		this.results = listDataProvider.getList();

		resultsPanel.setWidget(resultsGrid);

	}

	private void showEnterprisesList(JsArray<JsEmpres> empress) {

		sPanel.getElement().getStyle().setBackgroundColor("#FFFFFF");
		vPanel.add(createAdvancedForm(empress));
		sPanel.setVisible(true);
	}

	private Widget createAdvancedForm(JsArray<JsEmpres> empress) {
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
		Grid advancedOptions = new Grid(empress.length(), 2);		
		advancedOptions.setCellSpacing(6);
		
		for(int i = 0; i < empress.length(); i ++) {
			
			advancedOptions.setWidget(i, 0, new CheckBox());
			advancedOptions.setHTML(i, 1, empress.get(i).getRSocial());			
		}

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
						showEnterprisesList(empress);
						
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
				if (result != null) {
					results.add(result);
				}
			}

		});
	}

	// ------------------------------------------------------------------------

}
