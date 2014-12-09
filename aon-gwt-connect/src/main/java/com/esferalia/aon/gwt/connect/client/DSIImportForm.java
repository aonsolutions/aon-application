package com.esferalia.aon.gwt.connect.client;

import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader.UploadedInfo;
import gwtupload.client.IUploader;
import gwtupload.client.SingleUploader;
import gwtupload.client.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ProgressBar;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.connect.client.DSIImportClient.DSIImportCallback;
import com.esferalia.aon.gwt.connect.shared.DSIImportService;
import com.esferalia.aon.gwt.connect.shared.JsEmployee;
import com.esferalia.aon.gwt.connect.shared.JsEmpres;
import com.esferalia.aon.gwt.connect.shared.JsImportEvent;
import com.esferalia.aon.gwt.connect.shared.JsImportEvent.EventTypeVisitor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

public class DSIImportForm implements EntryPoint, DSIImportService {

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
			progressBar = new ProgressBar(20, ProgressBar.SHOW_TIME_REMAINING
					+ ProgressBar.SHOW_TEXT);
			this.progressBar.setText("Importando...");
			barPanel.clear();
			barPanel.add(progressBar);
		}

		@Override
		public void run() {
			int progress = progressBar.getProgress() + 4;
			if (progress > 100)
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
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	MinimizePanel footPanel;
	// @UiField
	// SingleUploader fileUpload;
	@UiField
	Button sendButton;
	@UiField
	HorizontalPanel barPanel;

	@UiField
	ResizeLayoutPanel dockPanel;

	@UiField
	DSILoadSelectedGrid dataGrid;

	private EnableButtons enableButtons;
	private JsArray<JsEmpres> empress;

	private List<DSIImportResult> results;
	private List<DSILoadSelected> loads;

	private ProgressBarCallBack progressBarCallback;

	private ResultsPanel importsPanel;
	private ResultsPanel loadsPanel;

	private SingleUploader fileUpload;

	private long progress = 10;

	@Override
	public void onModuleLoad() {

		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();

		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);

		this.dockPanel.setSize("100%", "100%");
		this.dockPanel.setVisible(false);
		this.importsPanel = new ResultsPanel();
		this.loadsPanel = new ResultsPanel();
		this.loadsPanel.clearFlowPanel();

		this.initGrid();
		this.initFileUpload();
	}

	protected void initGrid() {

		DSIImportResultsGrid resultsGrid = new DSIImportResultsGrid();
		ListDataProvider<DSIImportResult> listDataProvider = new ListDataProvider<DSIImportResult>();
		listDataProvider.addDataDisplay(resultsGrid);
		this.results = listDataProvider.getList();

		importsPanel.setWidget(resultsGrid);

		ListDataProvider<DSILoadSelected> listLoadProvider = new ListDataProvider<DSILoadSelected>();
		listLoadProvider.addDataDisplay(dataGrid);
		this.loads = listLoadProvider.getList();

		enableButtons = new EnableButtons(dataGrid);
	}

	protected void initFileUpload() {

		uploadFormPanel.clear();

		fileUpload = new SingleUploader(
				FileInputType.BROWSER_INPUT.with(FileInputType.LABEL
						.getInstance()));

		uploadFormPanel.add(fileUpload);

		fileUpload.setAutoSubmit(true);
		fileUpload.setServletPath(GWT.getModuleBaseURL() + "?" + URL);
		fileUpload.getForm().setAction(URL);
//		fileUpload.setValidExtensions("zip");
		fileUpload.avoidEmptyFiles(true);
		fileUpload.setMultipleSelection(false);

		fileUpload.addOnStatusChangedHandler(onStatusChangeUploaderHandler);
		fileUpload.addOnStartUploadHandler(onStartUploaderHandler);
		fileUpload.addOnFinishUploadHandler(onFinishUploaderHandler);
		fileUpload.addOnCancelUploadHandler(OnCancelUploaderHandler);

		fileUpload.getForm().addSubmitCompleteHandler(
				new SubmitCompleteHandler() {
					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
						fileUpload.getStatusWidget().setProgress(100, 100);
						fileUpload.getStatusWidget().setStatus(Status.SUCCESS);
						
						String json = event.getResults();
						
						JsArrayString dbs = JsonUtils.safeEval(json);

						DSIImportClient.getEmpress(dbs,
								new DSIImportCallback<JsArray<JsEmpres>>() {

									@Override
									public void onError(Throwable t) {
										// TODO Show Dialog, Error at
										// 'resultsPanel' or both.
										// it's up to you
										Window.alert(t.getMessage());
									}

									@Override
									public void onSuccess(
											JsArray<JsEmpres> empress) {
										DSIImportForm.this.empress = empress;
										try {
											loadEmpress(empress);
											dockPanel.setVisible(true);
										} catch (Exception ex) {
											GWT.log(ex.getMessage() + ", "
													+ ex.getCause());
										} finally {
											initFileUpload();
										}
									}
								});
					}
				});
	}

	private IUploader.OnStatusChangedHandler onStatusChangeUploaderHandler = new IUploader.OnStatusChangedHandler() {

		@Override
		public void onStatusChanged(IUploader uploader) {

			if (uploader.getStatus() != Status.SUCCESS) {
				uploader.getStatusWidget().setProgress(progress, 100);
			} else {
				uploader.getStatusWidget().setProgress(100, 100);
			}
			progress += 20;
		}
	};

	private IUploader.OnStartUploaderHandler onStartUploaderHandler = new IUploader.OnStartUploaderHandler() {

		@Override
		public void onStart(IUploader uploader) {
			uploader.getStatusWidget().setVisible(true);
			uploader.submit();
		}
	};

	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {

		@Override
		public void onFinish(IUploader uploader) {

			if (uploader.getStatus() == Status.SUCCESS) {
				UploadedInfo info = uploader.getServerInfo();
				System.out.println("File name " + info.name);
				System.out.println("File content-type " + info.ctype);
				System.out.println("File size " + info.size);

				System.out.println("Server message " + info.message);

				Window.alert("onFinishUploader: " + uploader.getStatus().name());
				uploader.getStatusWidget().setProgress(100, 100);
				uploader.getStatusWidget().setStatus(Status.DONE);

				uploader.getStatusWidget().setVisible(true);
				progress = 0;
			}
		}
	};

	private IUploader.OnCancelUploaderHandler OnCancelUploaderHandler = new IUploader.OnCancelUploaderHandler() {

		@Override
		public void onCancel(IUploader uploader) {

			String response = uploader.getServerRawResponse();
			System.out.println("-------------------cancel upload handler");

			Document doc = XMLParser.parse(response);
			String message = Utils.getXmlNodeValue(doc, "message");
			System.out.println(response);
			// fileList.remove("");

			/*
			 * Window.alert("onCancelUploader: " + uploader.getStatus().name());
			 * initFileUpload();
			 */
		}
	};

	// ------------------------------------------------------------- UiHandlers
	/*
	 * @UiHandler("footPanel") void onFootMinimize(MinimizeEvent event) {
	 * closeFootPanel(); }
	 */
	@UiHandler("sendButton")
	void onClickSendButton(ClickEvent event) {

		this.progressBarCallback = new ProgressBarCallBack();
		results.clear();

		final Set<DSILoadSelected> selected = dataGrid.getSelectedObject();
		final Iterator<DSILoadSelected> iterator = selected.iterator();

		evalProgressBar(selected.size() * 2);

		DSIImportClient.importSelected(selected, empress,
				new DSIImportCallback<JsImportEvent>() {

					@Override
					public void onError(Throwable t) {
						// TODO Show Dialog, Error at 'resultsPanel' or both.
						// It's up to
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
										DSILoadSelected object = iterator
												.next();
										removeObject(object);
										return new DSIImportResultsGrid.EnterpriseUpdatedResults(
												object.getName());
									}

									@Override
									public DSIImportResult onEnterpriseInserted(
											JsEmpres empres) {
										DSILoadSelected object = iterator
												.next();
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

	private void loadEmpress(JsArray<JsEmpres> empress) throws Exception {

		List<String> documents = new ArrayList<String>();

		for (int x = 0; x < empress.length(); x++) {

			documents.add(empress.get(x).getNif());

			DSILoadSelected selected = new DSILoadSelectedGrid.EnterpriseDSILoadSelected(
					empress.get(x));

			loads.add(selected);
		}
	}

	private void setEnableImportButton() {
		sendButton.setVisible(dataGrid.getSelectedObject().size() > 0);
	}

	private void removeObject(DSILoadSelected object) {
		loads.remove(object);
		dataGrid.clearSelected(object);
	}

	private void showImportsPanel() {

		Label tab = new Label("Resultados");
		tab.addStyleName(AON.AON_ICON_TIME);
		tab.addStyleName(AON.AON_ICON_CMD_BUTTON);
		DSIImportForm.this.footTabPanel.add(DSIImportForm.this.importsPanel,
				tab);
		/*
		 * DSIImportForm.this.splitLayoutPanel.setWidgetSize(
		 * DSIImportForm.this.footPanel, Window.getClientHeight() / 2);
		 */
		DSIImportForm.this.splitLayoutPanel.setVisible(true);

	}

	/*
	 * private void closeFootPanel() { splitLayoutPanel.setWidgetSize(footPanel,
	 * 0); }
	 */
}
