package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.template.shared.Warehouse;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
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
	
	Integer domainId;
	Vector<Warehouse> warehouses, warehouseList;
	
	public ConsumptionPage(Integer domainId) {
		panel = new FlowPanel();
		warehouseListBox = new ListBox();
		pdfButton = new Button();
		excelButton = new Button();
		cleanButton = new Button();
		
		this.domainId = domainId;
		
		Widget ui = pageBinder.createAndBindUi(this);
		RootLayoutPanel.get("rootPanel").add(ui);
		
		init();
	}

	private void init() {
		item.getWarehouses(domainId, new AsyncCallback<Vector<Warehouse>>() {
			
			@Override
			public void onSuccess(Vector<Warehouse> result) {
				warehouseList = result;
				warehouses = new Vector<Warehouse>();
				warehouseListBox.addItem("-");
				for (Warehouse w : result) {
					warehouseListBox.addItem(w.getName());
				}
	
				warehouseListBox.addChangeHandler(new ChangeHandler() {
					
					@Override
					public void onChange(ChangeEvent event) {
						String wtext = warehouseListBox.getSelectedItemText();
						for (Warehouse w : warehouseList) {
							if(w.equals(wtext)) warehouses.add(w);
						}
						// TODO METER UN LABEL CON EL ALAMACEN SELECCIONADO EN panel. *** mejorar
						Label l = new Label(wtext);
						warehouseListBox.removeItem(warehouseListBox.getSelectedIndex());
						warehouseListBox.setSelectedIndex(0);
						panel.add(l);
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
	}
	
	//------------------------------ UI Handlers
	
	@UiHandler("cleanButton")
	void cleanAction(ClickEvent event) {
		for(Integer i = 0; i<panel.getWidgetCount(); i++){
			Label label = (Label) panel.getWidget(i);
			label.getText();
			for(Warehouse w : warehouseList){
				if(w.getName().equals(label.getText()))
					warehouseListBox.addItem(label.getText());
			}
			panel.remove(i);
		}
		warehouses.removeAllElements();
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
