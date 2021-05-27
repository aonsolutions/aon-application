package com.esferalia.aon.gwt.connect.client;

import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnCancelUploaderHandler;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;
import gwtupload.client.IUploader.OnStatusChangedHandler;
import gwtupload.client.IUploader.UploaderConstants;
import gwtupload.client.SingleUploader;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class BOEImportForm extends Composite implements EntryPoint {
	
	public static ConnectServiceAsync CONNECT_SERVICE;
	
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

	private static BOEImportFormUiBinder uiBinder = GWT.create(BOEImportFormUiBinder.class);

	interface BOEImportFormUiBinder extends UiBinder<Widget, BOEImportForm> {}

	private static final String URL = GWT.getModuleBaseURL() + "ZippedMod200BOEImportUpload";

	@UiField
	FlexTable flexTable;
	
	@UiField
	Button button;

	@UiField
	SplitLayoutPanel splitLayoutPanel;
	
	private long progress = 10;

	public BOEImportForm() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		
		ConnectServiceAsync connectServiceRaw = GWT.create(ConnectService.class);
		CONNECT_SERVICE = new ConnectServiceAsyncDecorator(connectServiceRaw);		
		
		Widget ui = uiBinder.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(ui);
		initFileUpload();
	}

	protected void initFileUpload() {
		button.setText(AON.MSG.importAction());
		
		flexTable.setStyleName(AON.AON_CSS.aonPanelGrid());
		flexTable.addStyleName(AON.AON_CSS.aonMarginTop());
		flexTable.addStyleName(AON.AON_CSS.aonWidthAll());
		flexTable.setBorderWidth(1);
		flexTable.setCellSpacing(0);
		flexTable.getColumnFormatter().setWidth(0, "200px;");
		flexTable.setWidget(0, 0, new Label(AON.MSG.file()));
		flexTable.setWidget(0, 1, newUploader(URL));
		flexTable.setWidget(1, 0, button);
		flexTableCss();
	}

	public void flexTableCss(){
		for (int i = 0; i < flexTable.getRowCount(); i++) {
			for (int j = 0; j < flexTable.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					flexTable.getCellFormatter().setStyleName(i, j,AON.AON_CSS.aonPanelGridOdd());
				} else {
					flexTable.getCellFormatter().setStyleName(i, j,AON.AON_CSS.aonPanelGridOdd());
				}
			}
		}
	}

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
        upload.setValidExtensions(".zip");
       
        up = upload; urlAux = url;
        upload.addOnCancelUploadHandler(new OnCancelUploaderHandler() {
        	String url = urlAux;
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
        upload.getForm().addSubmitCompleteHandler( new SubmitCompleteHandler() {
        	SingleUploader upload = up;
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				upload.getForm().getWidget().getElement().getChild(0).removeFromParent();
				}
		});
        return upload;
		
	}
	
	@UiHandler("button")
	public void onButtonClick(ClickEvent event) {		
		if (up == null)
			return;	
		
		//messages.clear();
		
		if (Window.confirm(AON.MSG.continueAction()+"?")) {
			CONNECT_SERVICE.importZippedMod2002013(getCurrentDomainName(), getCurrentDomain(), new AsyncCallback<List<String>>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(List<String> result) {
					flexTable.setWidget(0, 1, newUploader(URL));					
					Label label = null;					
					FlowPanel messages = new FlowPanel();
					
					for (String msg : result) {
						if (AonStringUtils.isEmpty(msg)) {
							label = new Label("");
							label.setStyleName(AON.AON_CSS.aonBorderBottom());
							messages.add(label);
						} else {
							label = new Label(msg);
							label.setStyleName(AON.AON_CSS.aonPaddingLeft());
							messages.add(label);
						}
					}
					showImportMessages(messages);
				}
			});
		}
	}
	
	private void showImportMessages (FlowPanel messages) {

		ScrollPanel scrollPanel = new ScrollPanel(messages);
		splitLayoutPanel.addSouth(scrollPanel, Window.getClientHeight() / 2);
	}
	
	
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public static native String getRootPanel()
	/*-{
		return $wnd.localStorage.getItem("rootPanel");
	}-*/;
}
