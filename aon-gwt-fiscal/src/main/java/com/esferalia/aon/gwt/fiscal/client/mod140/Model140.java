package com.esferalia.aon.gwt.fiscal.client.mod140;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Model140 extends MainEntryPoint {

	static FiscalServiceAsync fiscalService;
	
	final static CommonMessages MSG = GWT.create(CommonMessages.class);
	final static AonResources AON_RESOURCES = GWT.create(AonResources.class);
	
	interface Model140Binder extends UiBinder<Widget, Model140> {
	}

	private static final Model140Binder MODEL_140_BINDER = GWT
			.create(Model140Binder.class);

	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	ResultsPanel resultsPanel;
	@UiField
	MinimizePanel footPanel;
	@UiField
	Panel formContainer;

	@UiField
	FormPanel diskForm;
	@UiField
	TextBox epigraph; 
	@UiField
	DateBoxEx fromDate;
	@UiField
	DateBoxEx toDate;
	
	@UiField
	Button generateFileButton;
	
	
	@UiField
	Hidden domainId;
	@UiField
	Hidden domainName;
	@UiField
	Hidden user;

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		FiscalServiceAsync mod180ServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(mod180ServiceRaw);

		Widget ui = MODEL_140_BINDER.createAndBindUi(this);
		

		epigraph.setName("epigraph"); 
		fromDate.getTextBox().setName("fromDate");
		toDate.getTextBox().setName("toDate");
		domainId.setName("domainId");
		domainName.setName("domainName");
		user.setName("user");
		
		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(ui);
	}

	// -------------------------------------------------------------- UiHandler

	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		diskForm.setMethod(FormPanel.METHOD_POST);
		diskForm.setAction(GWT.getHostPageBaseURL() + "aon_gwt_fiscal/ms/Model140File");
		diskForm.setEncoding(FormPanel.ENCODING_URLENCODED);
		
		domainId.setValue(String.valueOf(getCurrentDomain()));
		domainName.setValue(getCurrentDomainName());
		user.setValue(getCurrentUser());
		diskForm.submit();
	}

	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	@UiHandler("footPanel")
	void onFootMaximize(MinimizeEvent event) {
	}

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}

}
