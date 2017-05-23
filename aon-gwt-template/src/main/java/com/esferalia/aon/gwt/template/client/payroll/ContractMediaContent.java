package com.esferalia.aon.gwt.template.client.payroll;

import java.util.Date;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ContractMediaContent extends Composite {
	
	interface PageBinder extends UiBinder<Widget, ContractMediaContent> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);
	
	@UiField ListBox yearList;
	@UiField CheckBox resumeCheckBox;
	@UiField CheckBox detailCheckBox;
	@UiField Button downloadButton;
	
	API API;
	public ContractMediaContent(API API) {
		this.API = API;
		downloadButton = new Button();
		Widget ui = pageBinder.createAndBindUi(this);
		RootLayoutPanel.get("rootPanel").add(ui);
		
		init(API);
	}
	
	private void init(API API) {
		downloadButton = new Button();
		detailCheckBox.setValue(true);
		Integer inityear = 2010;
		Date date = new Date();
		Integer year = date.getYear() + 1900;
		
		for(Integer i = 0; i < (year - inityear); i++){
			Integer y = year-i;
			yearList.addItem(y.toString(), y.toString());
		}
	}
	
	@UiHandler("downloadButton")
	void downloadAction(ClickEvent event) {
		if(resumeCheckBox.getValue() || detailCheckBox.getValue()){
			API.getPayroll().printContractMedia(Integer.parseInt(yearList.getSelectedItemText()),
				resumeCheckBox.getValue(), detailCheckBox.getValue());
		}
	}	
}
