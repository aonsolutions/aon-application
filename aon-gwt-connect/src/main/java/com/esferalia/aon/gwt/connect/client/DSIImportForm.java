package com.esferalia.aon.gwt.connect.client;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ProgressBar;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;

public class DSIImportForm implements EntryPoint {

	interface Binder extends UiBinder<Widget, DSIImportForm> {
	}	
	
	class EnableButtons implements DSILoadSelectedGrid.Listener {

		private DSILoadSelectedGrid loadsGrid;
		
		public EnableButtons(DSILoadSelectedGrid loadsGrid) {
			this.loadsGrid = loadsGrid;
			this.loadsGrid.addListener(this);
			
			sendButton.setVisible(false);
		}
		@Override
		public void onSelectionChangeHandler(SelectionChangeEvent event) {			
			setEnableImportButton();
		}		
	}
	
	class ProgressBarCallBack extends Timer {
		
		ProgressBar progressBar = null;				
		
		public ProgressBarCallBack() {
			progressBar = 
					new ProgressBar(20, ProgressBar.SHOW_TIME_REMAINING + ProgressBar.SHOW_TEXT);
			this.progressBar.setText("Importando...");
			barPanel.clear();
			barPanel.add(progressBar);
		}

		@Override
		public void run() {
			int progress = progressBar.getProgress() + 4;
			if(progress > 100)
				cancel();
			progressBar.setProgress(progress);
		}
		
		private void setText(String text) {
			progressBar.setCompletedMessage(text);
		}
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	DateBox dateBox;
	@UiField
	FormPanel uploadFormPanel;
	@UiField
	TabLayoutPanel footTabPanel;
	@UiField
	TabLayoutPanel westTabPanel;
	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	MinimizePanel footPanel;
	@UiField
	FileUpload fileUpload;
	@UiField
	Button sendButton;
	@UiField
	HorizontalPanel barPanel;
		
	private final static String URL = "/aon-aio/aon_gwt_connect/dsiimport";
	
	private EnableButtons enableButtons;
	private JsArray<JsEmpres> empress;
	
	private List<DSIImportResult> results;
	private List<DSILoadSelected> loads;
	
	private ProgressBarCallBack progressBarCallback;
	private DSILoadSelectedGrid loadsGrid;
	
	private ResultsPanel importsPanel;
	private ResultsPanel loadsPanel;

	@Override
	public void onModuleLoad() {
		
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);	
		
		this.importsPanel = new ResultsPanel();
		this.loadsPanel = new ResultsPanel();
		
		this.loadsPanel.clearFlowPanel();
		
		this.initParameters();
		this.initGrid();		
	}

	protected void initParameters() {
				
		dateBox.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));
		
		uploadFormPanel.setAction(URL);		
		uploadFormPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadFormPanel.setMethod(FormPanel.METHOD_POST);
		uploadFormPanel.getElement().setDraggable("DRAGGABLE_TRUE");
	}
	
	protected void initGrid() {
		
		DSIImportResultsGrid resultsGrid = new DSIImportResultsGrid();
		ListDataProvider<DSIImportResult> listDataProvider = new ListDataProvider<DSIImportResult>();
		listDataProvider.addDataDisplay(resultsGrid);
		this.results = listDataProvider.getList();

		importsPanel.setWidget(resultsGrid);		

		this.loadsGrid = new DSILoadSelectedGrid();
		ListDataProvider<DSILoadSelected> listLoadProvider = new ListDataProvider<DSILoadSelected>();
		listLoadProvider.addDataDisplay(loadsGrid);		
		this.loads = listLoadProvider.getList();
		
		loadsPanel.setWidget(loadsGrid);
		
		enableButtons = new EnableButtons(loadsGrid);
		
	}
	
	// ------------------------------------------------------------- UiHandlers

	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	
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
						
							try {								
								loadEmpress(empress);
								showLoadsPanel();
							} catch(Exception ex) {						
								GWT.log(ex.getMessage() + ", " + ex.getCause());
							}																			
					}
				});
	}

	@UiHandler("sendButton")
	void onClickSendButton(ClickEvent event) {
		
		this.progressBarCallback = new ProgressBarCallBack();		
		results.clear();
		
		final Set<DSILoadSelected> selected = loadsGrid.getSelectedObject();
		final Iterator<DSILoadSelected> iterator = selected.iterator();
		
		evalProgressBar(selected.size() * 2);	
						
		DSIImportClient.importSelected(selected, empress, new DSIImportCallback<JsImportEvent>() {
			
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
								return null;
							}

							@Override
							public DSIImportResult onRollbacked() {
								return null;
							}

							@Override
							public DSIImportResult onEmployeeIgnored(
									JsEmployee employee) {
								return null;
							}

							@Override
							public DSIImportResult onEmployeeUpdated(
									JsEmployee employee) {
								return null;
							}

							@Override
							public DSIImportResult onEmployeeInserted(
									JsEmployee employee) {
								return null;
							}

							@Override
							public DSIImportResult onEnterpriseIgnored(
									JsEmpres empres) {
								
								return new DSIImportResultsGrid.EnterpriseIgnoredResults(
										iterator.next().getName());
							}

							@Override
							public DSIImportResult onEnterpriseUpdated(
									JsEmpres empres) {
								DSILoadSelected object = iterator.next();
								removeObject(object);								
								return new DSIImportResultsGrid.EnterpriseUpdatedResults(
										object.getName());
							}

							@Override
							public DSIImportResult onEnterpriseInserted(
									JsEmpres empres) {
								DSILoadSelected object = iterator.next();
								removeObject(object);
								return new DSIImportResultsGrid.EnterpriseInsertedResult(
										object.getName());
							}
						});
				if (result != null) {
					results.add(result);					
					progressBarCallback.setText("Completado");
					showImportsPanel();					
				}
			}
		});
	}

	// ------------------------------------------------------------------------
	
	private void evalProgressBar(int cargaTrabajo) {
		progressBarCallback.scheduleRepeating(cargaTrabajo);
	}
	
	private void loadEmpress (JsArray<JsEmpres> empress) throws Exception {
		
		DSILoadSelected selected;
		
		for ( int x = 0; x < empress.length(); x ++) {			
			String rsocial = empress.get(x).getRSocial();
			selected = new DSILoadSelectedGrid.EnterpriseDSILoadSelected(rsocial);
			loads.add(selected);
		}		
	}
	
	private void setEnableImportButton() {
		sendButton.setVisible(loadsGrid.getSelectedObject().size() > 0);
	}
	
	private void removeObject(DSILoadSelected object) {
		loads.remove(object);
		loadsGrid.clearSelected(object);
	}
	
	private void showLoadsPanel() {
		InlineLabel tab = new InlineLabel("Empresas");		
		DSIImportForm.this.westTabPanel.add(DSIImportForm.this.loadsPanel, tab);
		DSIImportForm.this.splitLayoutPanel.setWidgetSize(
				DSIImportForm.this.westTabPanel, Window.getClientWidth() / 4);
	}

	private void showImportsPanel() {
		
		Label tab = new Label("Resultados");
		tab.addStyleName(AON.AON_ICON_TIME);
		tab.addStyleName(AON.AON_ICON_CMD_BUTTON);
		DSIImportForm.this.footTabPanel.add(DSIImportForm.this.importsPanel, tab);
		DSIImportForm.this.splitLayoutPanel.setWidgetSize(
				DSIImportForm.this.footPanel, Window.getClientHeight() / 2);
	}
	
	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}

}
