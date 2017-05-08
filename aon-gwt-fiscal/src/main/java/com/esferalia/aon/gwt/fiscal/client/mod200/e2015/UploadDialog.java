package com.esferalia.aon.gwt.fiscal.client.mod200.e2015;

import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnCancelUploaderHandler;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;
import gwtupload.client.IUploader.OnStatusChangedHandler;
import gwtupload.client.IUploader.UploaderConstants;
import gwtupload.client.SingleUploader;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialogB;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class UploadDialog extends CustomDialogB {

	interface Binder extends UiBinder<Widget, UploadDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField(provided = true) FlexTable flex_table;
	@UiField(provided = true) Label label;
	@UiField Button accept_button;
	@UiField Button cancel_button;
	
	private UploaderConstants constants = new UploaderConstants() {
	    @Override public String uploadStatusSuccess() { return AON.MSG.uploadStatusSuccess(); }
		@Override public String uploadStatusSubmitting() {return AON.MSG.uploadStatusSubmitting();}
		@Override public String uploadStatusQueued() { return AON.MSG.uploadStatusQueued();}
		@Override public String uploadStatusInProgress() {return AON.MSG.uploadStatusInProgress();}
		@Override public String uploadStatusError() {return AON.MSG.uploadStatusError();}
		@Override public String uploadStatusDeleted() {return AON.MSG.uploadStatusDeleted();}
		@Override public String uploadStatusCanceling() { return AON.MSG.uploadStatusCanceling();}
		@Override public String uploadStatusCanceled() {return AON.MSG.uploadStatusCanceled();}
		@Override public String uploadLabelCancel() {return AON.MSG.uploadLabelCancel();}
		@Override public String uploaderTimeout() {return AON.MSG.uploaderTimeout();}
		@Override public String uploaderServerUnavailable() {return AON.MSG.uploaderServerUnavailable();}
		@Override public String uploaderServerError() {return AON.MSG.uploaderServerError();}
		@Override public String uploaderSend() {return AON.MSG.uploaderSend();}
		@Override public String uploaderInvalidPathError() {return AON.MSG.uploaderInvalidPathError();}
		@Override public String uploaderInvalidExtension() {return AON.MSG.uploaderInvalidExtension();}
		@Override public String uploaderBrowse() {return AON.MSG.uploaderBrowse();}
		@Override public String uploaderBlobstoreError() {return AON.MSG.uploaderBlobstoreError();}
		@Override public String uploaderBlobstoreBilling() {return AON.MSG.uploaderBlobstoreBilling();}
		@Override public String uploaderBadServerResponse() {return AON.MSG.uploaderBadServerResponse();}
		@Override public String uploaderAlreadyDone() {return AON.MSG.uploaderAlreadyDone();}
		@Override public String uploaderActiveUpload() {return AON.MSG.uploaderActiveUpload();}
		@Override public String submitError() {return AON.MSG.submitError();}
	};

	public UploadDialog(String title,String url) {
		setCaption(title);
		label = new Label();
		flex_table = new FlexTable();
		toImport(url);
		setWidget(binder.createAndBindUi(this));
		accept_button.setText(AON.MSG.importAction());
		accept_button.setVisible(true);
		accept_button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onAccept();
			}
		});
		
		cancel_button.setText(AON.MSG.cancelAction());
		cancel_button.setVisible(true);
		cancel_button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCancel();
			}
		});
	}
	
	protected abstract void onAccept();
	
	protected abstract void onCancel();
	
	private void toImport(String url){
		flex_table.setStyleName(AON.AON_CSS.aonPanelGrid());
		flex_table.addStyleName(AON.AON_CSS.aonMarginTop());
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		flex_table.setWidget(0, 0, new Label(AON.MSG.file()));
		flex_table.setWidget(0, 1, newUploader(url));
		flexTableCss();
	}
	
	
	
	long progress = 10;
	SingleUploader up;
	String urlAux;
	private SingleUploader newUploader(String url) {

		
		SingleUploader upload=  new SingleUploader(FileInputType.BROWSER_INPUT);
		
		
		upload.setAutoSubmit(true);
        upload.setServletPath(url);
        upload.setI18Constants( constants ); 
        
        upload.getForm().getWidget().getElement().getChild(1).removeFromParent();
        upload.getForm().setAction(url);
        upload.getForm().setEncoding(FormPanel.ENCODING_MULTIPART);
        upload.getForm().setMethod(FormPanel.METHOD_POST);
        upload.setTitle("uploadFormElement");
        upload.avoidEmptyFiles(true);
       
        up = upload; urlAux = url;
        upload.addOnCancelUploadHandler(new OnCancelUploaderHandler() {
        	String url = urlAux;
        	@Override
			public void onCancel(IUploader uploader) {
        		SingleUploader upload1 = newUploader(url);
        		flex_table.setWidget(0, 1, upload1);
			}
		});
        
        upload.addOnStatusChangedHandler(new OnStatusChangedHandler() {
        	SingleUploader upload = up;
			@Override
			public void onStatusChanged(IUploader uploader) {
				if(upload.getStatus() != Status.SUCCESS){
			
					upload.getStatusWidget().setProgress(progress, 100);
			
				}
				else{
					upload.getStatusWidget().setProgress(100, 100);
				}	
				progress=progress+20;
				//upload.addStatusBar(uploader.getStatusWidget());
			}
		});

        upload.addOnStartUploadHandler(new OnStartUploaderHandler() {
        	SingleUploader upload = up;
			@Override
			public void onStart(IUploader uploader) {
				upload.getStatusWidget().setVisible(true);
			}
		});
        
        upload.addOnFinishUploadHandler(new OnFinishUploaderHandler() {
        	SingleUploader upload = up;
			@Override
			public void onFinish(IUploader uploader) {
				upload.getStatusWidget().setProgress(100, 100);
				upload.getStatusWidget().setStatus(Status.DONE);
				upload.getStatusWidget().setVisible(true);
				progress = 0;		
			}
		});
        upload.getForm().addSubmitCompleteHandler( new SubmitCompleteHandler() {
        	SingleUploader upload = up;
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				upload.getForm().getWidget().getElement().getChild(0).removeFromParent();
				}
		});
        return upload;
		
	}
	
	public void flexTableCss(){
		for (int i = 0; i < flex_table.getRowCount(); i++) {
			for (int j = 0; j < flex_table.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					flex_table.getCellFormatter().setStyleName(i, j,AON.AON_CSS.aonPanelGridOdd());
				} else {
					flex_table.getCellFormatter().setStyleName(i, j,AON.AON_CSS.aonPanelGridOdd());
				}
			}
		}
	}
}