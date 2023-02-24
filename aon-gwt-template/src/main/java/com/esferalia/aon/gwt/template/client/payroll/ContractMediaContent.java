package com.esferalia.aon.gwt.template.client.payroll;

import java.util.Date;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.payroll.Payroll.UnsexedCallback;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLoadingPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ContractMediaContent extends Composite {
	
	interface TabLayoutFolderSafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"aon_tab_label {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}
	private static final TabLayoutFolderSafeTemplate TABLAYOUT_FOLDER_TEMPLATE = GWT.create(TabLayoutFolderSafeTemplate.class);
	
	
	interface PageBinder extends UiBinder<Widget, ContractMediaContent> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);
	
	@UiField ListBox yearList;
	@UiField CheckBox resumeCheckBox;
	@UiField CheckBox detailCheckBox;
	@UiField Button downloadButton;
	@UiField Button downloadButtonExcel;
	@UiField FlowPanel centerFlow;
	@UiField SplitLayoutPanel splitPanel;
	
	AonMinimizePanel footPanel;
	TabLayoutPanel tabLayout;
	FlowPanel sessionLog;
	boolean minimizedByUser;
	
	public static native String getRootPanel()
	/*-{
		return $wnd.localStorage.getItem("rootPanel");
	}-*/;
	
	API API;
	public ContractMediaContent(API API) {
		this.API = API;
		downloadButton = new Button();
		Widget ui = pageBinder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		init(API);
	}
	
	private void init(API API) {
		centerFlow.getElement().getStyle().setDisplay(Display.NONE);
		downloadButton = new Button();
		
		detailCheckBox.setValue(true);
		Integer inityear = 2010;
		Date date = new Date();
		Integer year = date.getYear() + 1900;
		
		for(Integer i = 0; i < (year - inityear); i++){
			Integer y = year-i;
			yearList.addItem(y.toString(), y.toString());
		}
		yearList.setSelectedIndex(1);
		
		AonLoadingPanel loadingPanel = new AonLoadingPanel("Su informe est\u00E1 siendo generado. Por favor, espere.");
		loadingPanel.show();
		centerFlow.add(loadingPanel);
		splitPanel.addSouth(getMinimizePanel(), 30);
	}
	
	@UiHandler("downloadButton")
	void downloadAction(ClickEvent event) {
		if(resumeCheckBox.getValue() || detailCheckBox.getValue()){
			API.getPayroll().printContractMedia(Integer.parseInt(yearList.getSelectedItemText()),
				resumeCheckBox.getValue(), detailCheckBox.getValue());
		}
	}	
	@UiHandler("downloadButtonExcel")
	void downloadActionExcel(ClickEvent event) {
		if(resumeCheckBox.getValue() || detailCheckBox.getValue()){
			closeFootPanel();
			UnsexedCallback unsexedCallback = (nss, name) -> {
				Label lbl = new Label("ADVERTENCIA: Sexo no definido - NAF: " + nss + ", Nombre: "+ name);
				lbl.getElement().getStyle().setColor("orange");
				sessionLog.add(lbl);
				openFootPanel();
			};
			
			API.getPayroll().printRemunerationRecord(Integer.parseInt(yearList.getSelectedItemText()), centerFlow, unsexedCallback);
		}
	}
	
	private AonMinimizePanel getMinimizePanel() {
		footPanel = new AonMinimizePanel();
		footPanel.addMinimizeHandler(event -> {
			minimizedByUser = true;
			closeFootPanel();
		});
		footPanel.addMaximizeHandler(event -> openFootPanel());
		footPanel.setStyleName(AON.CSS.aonSelector());
		tabLayout = new TabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		
		sessionLog = new FlowPanel();
		footPanel.addStyleName(AON.AON_CSS.aonBackgroundWhite());
		
		tabLayout.add(sessionLog, TABLAYOUT_FOLDER_TEMPLATE.tab(AON.MSG.information(), AON.CSS.aonIconHistory()));
		
		footPanel.add(tabLayout);
		
		tabLayout.setAnimationDuration(300);
		tabLayout.addSelectionHandler(event -> {
			minimizedByUser = false;
			openFootPanelIfNeeded();
		});
		
		return footPanel; 
	}
	
	private void closeFootPanel() {
		splitPanel.setWidgetSize(footPanel, 30);
		splitPanel.animate(500);
	}
	
	private void openFootPanelIfNeeded() {
		if (!minimizedByUser && splitPanel.getWidgetSize(footPanel) <= 30) {
			openFootPanel();
		}
	}
	
	private void openFootPanel() {
		int effectiveHeigth = 3;
		splitPanel.setWidgetSize(footPanel, (double)Window.getClientHeight() / effectiveHeigth);
		splitPanel.animate(500);
	}

	
}
