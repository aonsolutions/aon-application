package com.esferalia.aon.gwt.connect.client;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.Upload;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class BOEImportForm extends Composite implements EntryPoint {
	
	public static ConnectServiceAsync CONNECT_SERVICE;
	

	private static BOEImportFormUiBinder uiBinder = GWT.create(BOEImportFormUiBinder.class);

	interface BOEImportFormUiBinder extends UiBinder<Widget, BOEImportForm> {}

	private static final String URL = GWT.getModuleBaseURL() + "ZippedMod200BOEImportUpload";

	
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
	}
	
	@UiHandler("button")
	public void onButtonClick(ClickEvent event) {		
		//messages.clear();
		
		if (Window.confirm(AON.MSG.continueAction()+"?")) {
			Upload upload = new Upload() {
				
				@Override
				protected void onUpload(String data, String type) {
					CONNECT_SERVICE.importZippedMod2002013(getCurrentDomainName(), getCurrentDomain(), data, new AsyncCallback<List<String>>() {

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onSuccess(List<String> result) {		
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
			};
			upload.upload();

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
