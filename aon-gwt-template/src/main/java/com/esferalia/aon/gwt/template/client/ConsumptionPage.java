package com.esferalia.aon.gwt.template.client;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ConsumptionPage extends Composite{

	private static final String PDF = "pdf";
	private static final String EXCEL = "excel";

	final ITemplateAsync item = GWT.create(ITemplate.class);

	interface PageBinder extends UiBinder<Widget, ConsumptionPage> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);
	
	@UiField FlowPanel panel;
	@UiField ListBox warehouseListBox;
	@UiField Button pdfButton;
	@UiField Button excelButton;
	@UiField Button cleanButton;
	
	public ConsumptionPage() {
		panel = new FlowPanel();
		warehouseListBox = new ListBox();
		pdfButton = new Button();
		excelButton = new Button();
		cleanButton = new Button();
		
		Widget ui = pageBinder.createAndBindUi(this);
		RootLayoutPanel.get("rootPanel").add(ui);
		
		init();
	}

	private void init() {
		warehouseListBox.addItem("-");
		warehouseListBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				// TODO METER UN LABEL CON EL ALAMACEN SELECCIONADO EN panel.
				warehouseListBox.setSelectedIndex(0);
				
			}
		});
	}
	
	//------------------------------ UI Handlers
	
	@UiHandler("cleanButton")
	void cleanAction(ClickEvent event) {
		
	}
	
	@UiHandler("pdfButton")
	void pdfAction(ClickEvent event) {
		download(PDF);
	}
	
	@UiHandler("excelButton")
	void excelAction(ClickEvent event) {
		download(EXCEL);
	}
	
	//------------------------------ Utils
	
	private void download(String type) {
		if(type.equals(PDF)){
			// TODO descargar en pdf.
		}
		else if(type.equals(EXCEL)){
			// TODO descargar en excel.
		}
	}
}
