package com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2014;

import gwtupload.client.SingleUploader;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;

public class UploadDialog extends CustomDialog {

	public interface AcceptCallBack {
		void onAccept(String xmlResults);
		void onCancel();
	}

	private AcceptCallBack callback;
	private Button submit;
	private CheckBox saveCheck;
	private FlexTable flexTable;
//	private long progress = 10;
	private String domainName;
	private int domain;

	public UploadDialog(final AcceptCallBack callback,String title, String label, String domainName,int domain) {
		AON.AON_RESOURCES.css().ensureInjected();
		this.domainName = domainName; 
		this.domain = domain;
		
		this.callback = callback;
		setVisible(false);
		setCaption(title);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		
		String url = GWT.getHostPageBaseURL() + "/aon_gwt_fiscal/Mod2002013BOEUpload";
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.AON_CSS.aonTextCenter());
		submit = new Button(AON.MSG.accept());
		buttonsPanel.add(submit);
		
		FlowPanel panel = new FlowPanel();
		panel.setWidth("550px");
		panel.setHeight("100px");
		panel.setStyleName(AON.AON_CSS.aonPadding());
		flexTable = new FlexTable();	
		flexTable.setStyleName(AON.AON_CSS.aonPanelGrid());
		flexTable.addStyleName(AON.AON_CSS.aonWidthAll());
		flexTable.setCellSpacing(0);
		flexTable.setWidget(0, 0, new Label(label));
		flexTable.setWidget(0, 1, newUploader(url));
		panel.add(flexTable);
		saveCheck = new CheckBox();
		saveCheck.setName("saveCheck");
		saveCheck.setText(AON.MSG.saveImportedModel());
		saveCheck.setValue(true);
		saveCheck.setEnabled(false);
		panel.add(saveCheck);
		panel.add(buttonsPanel);
		this.setWidget(panel);
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

	

	private SingleUploader newUploader(final String url) {
		final SingleUploader upload =  new SingleUploader();
		upload.setAutoSubmit(true);
		upload.setServletPath(url);
        upload.getForm().getWidget().getElement().getChild(1).removeFromParent();
        upload.getForm().setAction(url);
        upload.getForm().setEncoding(FormPanel.ENCODING_MULTIPART);
        upload.getForm().setMethod(FormPanel.METHOD_POST);
        upload.add(new Hidden("domainId",String.valueOf(this.domain)));
        upload.add(new Hidden("domainName",this.domainName));
        upload.avoidEmptyFiles(true);
        
//        upload.addOnCancelUploadHandler(new OnCancelUploaderHandler() {
//        	@Override
//			public void onCancel(IUploader uploader) {
//        		SingleUploader upload1 = newUploader(url);
//        		flexTable.setWidget(0, 1, upload1);
//			}
//		});
//        
//        upload.addOnStatusChangedHandler(new OnStatusChangedHandler() {
//        	@Override
//			public void onStatusChanged(IUploader uploader) {
//				if(upload.getStatus() != Status.SUCCESS){
//					upload.getStatusWidget().setProgress(progress, 100);
//				}
//				else{
//					upload.getStatusWidget().setProgress(100, 100);
//				}	
//				progress=progress+20;
//			}
//		});
//
//        upload.addOnStartUploadHandler(new OnStartUploaderHandler() {
//			@Override
//			public void onStart(IUploader uploader) {
//				upload.getStatusWidget().setVisible(true);
//			}
//		});
//        upload.addOnFinishUploadHandler(new OnFinishUploaderHandler() {
//			@Override
//			public void onFinish(IUploader uploader) {
//				upload.getStatusWidget().setProgress(100, 100);
//				upload.getStatusWidget().setStatus(Status.DONE);
//				upload.getStatusWidget().setVisible(true);
////				progress = 0;
//				upload.reset();
//			}
//		});
        
        upload.getForm().addSubmitCompleteHandler(new SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
                String xml = event.getResults();
                callback.onAccept(xml);                
                upload.getForm().getWidget().getElement().getChild(0).removeFromParent();
                hide();
			}
			
		});
        submit.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				upload.submit();
			}
		});
        return upload;
		
	}
}
