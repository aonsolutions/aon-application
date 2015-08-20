package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
import com.esferalia.aon.gwt.template.shared.Warehouse;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
	@UiField CheckBox warehouseCheckBox;
	@UiField CheckBox detailCheckBox;
	@UiField Button pdfButton;
	@UiField Button excelButton;
	@UiField Button cleanButton;
	@UiField Label titleLabel;
	
	Integer domainId;
	Vector<Warehouse> warehouses, warehouseList;
	TemplateList templateList;
	Boolean detail = false;
	
	public ConsumptionPage(Integer domainId, TemplateList templateList) {
		titleLabel = new Label();
		panel = new FlowPanel();
		warehouseListBox = new ListBox();
		warehouseCheckBox = new CheckBox();
		detailCheckBox = new CheckBox();
		pdfButton = new Button();
		excelButton = new Button();
		cleanButton = new Button();
		
		this.templateList = templateList;
		this.domainId = domainId;
		
		Widget ui = pageBinder.createAndBindUi(this);
		RootLayoutPanel.get("rootPanel").add(ui);
		
		init();
	}

	private void init() {
		titleLabel.setText(AON.MSG.aggregateConsumptionTemplates());
		item.getWarehouses(domainId, new AsyncCallback<Vector<Warehouse>>() {
			
			@Override
			public void onSuccess(Vector<Warehouse> result) {
				warehouseList = result;
				warehouses = new Vector<Warehouse>();
				warehouseListBox.addItem("-");
				for (Warehouse w : result) {
					warehouseListBox.addItem(w.getName());
				}
	
				warehouseListBoxChangeHandler();
				warehouseCheckBoxChangeHandler();
				detailCheckBoxChangeHandler();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
	}

	//------------------------------ UI Handlers
	
	@UiHandler("cleanButton")
	void cleanAction(ClickEvent event) {
		clean();
	}
	
	@UiHandler("pdfButton")
	void pdfAction(ClickEvent event) {
		download(PDF);
	}
	
	@UiHandler("excelButton")
	void excelAction(ClickEvent event) {
		download(EXCEL);
	}
	
	private void warehouseListBoxChangeHandler() {
		warehouseListBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String wtext = warehouseListBox.getSelectedItemText();
				if(wtext.equals("-") && warehouses.isEmpty()) excelButton.setEnabled(false);
				else excelButton.setEnabled(true);
				
				for (Warehouse w : warehouseList) {
					
					if(w.getName().equals(wtext)) {
						warehouses.add(w);
					}
				}
				// TODO METER UN LABEL CON EL ALAMACEN SELECCIONADO EN panel. *** mejorar
				HorizontalPanel wTag = closeTagButton(wtext);
				
				warehouseListBox.removeItem(warehouseListBox.getSelectedIndex());
				warehouseListBox.setSelectedIndex(0);
				panel.add(wTag);
			}
		});
	}
	
	private void warehouseCheckBoxChangeHandler(){
		warehouseCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					for(Integer k = warehouseListBox.getItemCount();k> 0;k--){
						String name = warehouseListBox.getItemText(k-1);
						if(!name.equals("-")){
							HorizontalPanel wTag = closeTagButton(name);
						
							warehouseListBox.removeItem(k-1);
							warehouseListBox.setSelectedIndex(0);
						
							panel.add(wTag);
						}
					}
				}
				else{
					warehouseListBox.setEnabled(true);
					clean();
				}
			}
		});
	}
	
	private void detailCheckBoxChangeHandler(){
		detailCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				detail = event.getValue();
			}
		});
	}
	
	HorizontalPanel hpAux;String nameAux;
	private void closeTagButtonClickHandler(Button closeTagButton, HorizontalPanel hp, String name){
		hpAux = hp;nameAux = name;
		closeTagButton.addClickHandler(new ClickHandler() {
			private HorizontalPanel hp = hpAux;
			String name = nameAux;
			@Override
			public void onClick(ClickEvent event) {
				hp = new HorizontalPanel();
				clean(name);
			}
		});
	}
	//------------------------------ Actions

	public void download(String type){
		Integer size = 0;
		TemplateInfo templateInfo = null;
		for (TemplateInfo ti : templateList.getList()) {
			if(ti.getType().equals("Consumo")){
				templateInfo = ti;
				size++;
			}
		}

		String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_aggregate_consumption/"
            	+ "?id=" + Integer.toString(templateInfo.getId())
            	+ "&domain_id=" + domainId;
		
		Integer i = 0;
		for (Warehouse w : warehouses) {
			fileDownloadURL = fileDownloadURL + "&warehouse" + i + "=" + w.getName()
										+"&warehouse_id"+ i +  "=" + w.getId();
			i++;
		}
		
		
		fileDownloadURL = fileDownloadURL + "&size=" + warehouses.size()
							+ "&detail=" + detail
							+ "&file_type="+type;	
						
		Window.open( fileDownloadURL, "_blank",null);
	}
	
	private void clean(){
		for(Integer j = panel.getWidgetCount(); j>0; j--){
			HorizontalPanel hp = (HorizontalPanel) panel.getWidget(j-1);
			Label label = (Label) hp.getWidget(1);

			for(Warehouse w : warehouseList){
				if(w.getName().equals(label.getText()))
					warehouseListBox.addItem(label.getText());
			}
			panel.remove(j-1);
		}
		warehouses.removeAllElements();
	}
	
	private void clean(String name){
		for(Integer i = 0; i<panel.getWidgetCount(); i++){
			HorizontalPanel hp = (HorizontalPanel) panel.getWidget(i);
			Label label = (Label) hp.getWidget(1);
			if(label.getText().equals(name)){
				for(Warehouse w : warehouseList){
					if(w.getName().equals(name))
						warehouseListBox.addItem(label.getText());
				}
				panel.remove(i);
			}
			warehouses.remove(hp);
		}
		warehouseCheckBox.setValue(false);
	}
	
	//------------------------------ Widgets

	private HorizontalPanel closeTagButton(String name) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		Button closeTagButton = new Button("");
		closeTagButton.setStyleName("aon-editDataTable-button aon-icon-close");	
		Label label = new Label(name);
		horizontalPanel.add(closeTagButton);
		horizontalPanel.add(label);
		closeTagButtonClickHandler(closeTagButton, horizontalPanel, name);
	
		return horizontalPanel;
	}
}
