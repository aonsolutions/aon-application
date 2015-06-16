package com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2014;

import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnCancelUploaderHandler;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;
import gwtupload.client.IUploader.OnStatusChangedHandler;
import gwtupload.client.SingleUploader;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Label;

public class UploadDialog extends CustomDialog {

	public interface AcceptCallBack {
		void onAccept();
		void onCancel();
	}

	private AcceptCallBack callback;
	FlexTable flexTable;

	public UploadDialog(final AcceptCallBack callback,String title, String label) {
		AON.AON_RESOURCES.css().ensureInjected();
		
		this.callback = callback;
		setVisible(false);
		setCaption(title);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);

		final FormPanel form = new FormPanel();
		String url = GWT.getHostPageBaseURL() + "/aon_gwt_fiscal/Mod2002013BOEUpload";
		form.setAction(url);
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
    	form.setMethod(FormPanel.METHOD_POST);
    	
		FlowPanel panel = new FlowPanel();
		form.add(panel);
		panel.setWidth("550px");
		panel.setHeight("200px");
		panel.setStyleName(AON.AON_CSS.aonPadding());
		flexTable = new FlexTable();	
		flexTable.setStyleName(AON.AON_CSS.aonPanelGrid());
		flexTable.addStyleName(AON.AON_CSS.aonWidthAll());
		flexTable.setBorderWidth(1);
		flexTable.setCellSpacing(0);
		flexTable.setWidget(0, 0, new Label(label));
		flexTable.setWidget(0, 1, newUploader(url));
		panel.add(flexTable);
		
		FlowPanel buttonPanel = new FlowPanel();
		panel.add(buttonPanel);
		buttonPanel.addStyleName(AON.AON_CSS.aonMarginTop());
		buttonPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		Button accept = new Button();
		accept.setText(AON.MSG.import2013());
		accept.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				form.submit();
				callback.onAccept();
			}
		});
		buttonPanel.add(accept);
		this.setWidget(form);
	}
	
	@Override
	public void onClose() {
		this.hide();
		callback.onCancel();
	}

	public void onShow() {
		center();
		show();
	}

	long progress = 10;
	SingleUploader up;
	String urlAux;

	private SingleUploader newUploader(String url) {
		SingleUploader upload=  new SingleUploader(FileInputType.BROWSER_INPUT);
		
		
		upload.setAutoSubmit(true);
        upload.setServletPath(url);
        
        upload.getForm().getWidget().getElement().getChild(1).removeFromParent();
        upload.getForm().setAction(url);
        upload.getForm().setEncoding(FormPanel.ENCODING_MULTIPART);
        upload.getForm().setMethod(FormPanel.METHOD_POST);
        upload.setTitle("uploadFormElement");
        upload.avoidEmptyFiles(true);
       
        up = upload; 
        urlAux = url;
        upload.addOnCancelUploadHandler(new OnCancelUploaderHandler() {
        	SingleUploader upload = up; String url = urlAux;
        	@Override
			public void onCancel(IUploader uploader) {
        		SingleUploader upload1 = newUploader(url);
        		flexTable.setWidget(0, 1, upload1);
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
        upload.getForm().addFormHandler(new FormHandler() {
        	SingleUploader upload = up;
			@Override
			public void onSubmitComplete(FormSubmitCompleteEvent event) {
				upload.getForm().getWidget().getElement().getChild(0).removeFromParent();
			}
			
			@Override
			public void onSubmit(FormSubmitEvent event) {}
		});
        return upload;
		
	}
}
