package com.esferalia.aon.gwt.connect.client;

import java.util.List;
import java.util.Set;

import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploadStatus.UploadStatusConstants;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.UploadedInfo;
import gwtupload.client.SingleUploader;

import com.esferalia.aon.gwt.common.client.ProgressBar;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class BOEImportForm extends Composite implements EntryPoint {

	private static BOEImportFormUiBinder uiBinder = GWT
			.create(BOEImportFormUiBinder.class);

	interface BOEImportFormUiBinder extends UiBinder<Widget, BOEImportForm> {
	}

	class ProgressBarCallBack extends Timer {

		ProgressBar progressBar = null;

		public ProgressBarCallBack() {
			progressBar = new ProgressBar(20, ProgressBar.SHOW_TIME_REMAINING
					+ ProgressBar.SHOW_TEXT);
			this.progressBar.setText("Importando.. Espere por favor");
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

	private static final String URL = "/aon-aio/aon_gwt_connect/boeimport";

	@UiField
	FormPanel uploadFormPanel;
	@UiField
	TabLayoutPanel footTabPanel;

	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	MinimizePanel footPanel;
	@UiField
	HorizontalPanel barPanel;

	@UiField
	ResizeLayoutPanel dockPanel;

	private SingleUploader fileUpload;
	private long progress = 10;

	public BOEImportForm() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void onModuleLoad() {

		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		Widget ui = uiBinder.createAndBindUi(this);

		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);

		this.dockPanel.setSize("100%", "100%");
		this.dockPanel.setVisible(false);

		initFileUpload();

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
					}
				});
	}

	// ====================================================================

	private IUploader.OnStatusChangedHandler onStatusChangeUploaderHandler = new IUploader.OnStatusChangedHandler() {

		@Override
		public void onStatusChanged(IUploader uploader) {

			if (uploader.getStatus() != Status.SUCCESS) {
				UploadedInfo info = uploader.getServerInfo();
				System.out.println("File name " + info.name);
				System.out.println("File content-type " + info.ctype);
				System.out.println("File size " + info.size);

				System.out.println("Server message " + info.message);

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
			
			initFileUpload();
		}
	};

}
