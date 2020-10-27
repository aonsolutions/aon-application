package com.esferalia.aon.gwt.template.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.template.shared.Hotel;
import com.esferalia.aon.gwt.template.shared.ProductCategory;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class ConsumptionPage extends Composite{

	public static final String PDF = "pdf";
	public static final String EXCEL = "excel";

	final ITemplateAsync item = GWT.create(ITemplate.class);
	
	interface PageBinder extends UiBinder<Widget, ConsumptionPage> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);
	

	@UiField VerticalPanel hotelBoxPanel;
	@UiField VerticalPanel warehouseBoxPanel;
	@UiField VerticalPanel selectedBoxPanel;
	
	@UiField ListBox categoryListBox;
	
	@UiField CheckBox warehouseCheckBox;
	@UiField CheckBox detailCheckBox;
	// @UiField Button pdfButton;
	@UiField Button excelButton;
	@UiField Button cleanButton;
	@UiField Label titleLabel;
	
	@UiField HTMLPanel datePanel;
	@UiField DateBox startDate;
	@UiField DateBox endDate;
	@UiField CheckBox packagedCheckBox;
	@UiField CheckBox errorCheckBox;
	@UiField CheckBox difCheckBox;
	@UiField CheckBox twoLastCheckBox;
	@UiField CheckBox withoutInvCheckBox;
	
	AonData aonData;
	
	ListBox hotelBox;
	ListBox warehouseBox;
	ListBox selectedBox;
	
	Map<String, Boolean> map;
	Map<String, String> hwMap;
	
	List<Hotel> hotels;
	LinkedList<TemplateInfo> templateList;
	Boolean detail = false;
	
	public ConsumptionPage(AonData aonData, LinkedList<TemplateInfo> templateList) {
		this.aonData = aonData;
		this.templateList = templateList;
		titleLabel = new Label();

		hotelBoxPanel = new VerticalPanel();hotelBoxPanel.setSpacing(4);
		warehouseBoxPanel = new VerticalPanel();warehouseBoxPanel.setSpacing(4);
		selectedBoxPanel = new VerticalPanel();selectedBoxPanel.setSpacing(4);
		warehouseCheckBox = new CheckBox();
		detailCheckBox = new CheckBox();
		// pdfButton = new Button();
		excelButton = new Button();
		cleanButton = new Button();
		categoryListBox = new ListBox();

		Widget ui = pageBinder.createAndBindUi(this);
		RootLayoutPanel.get(aonData.getRootPanel()).add(ui);
		
		init();
	}

	public AonData getAonData() {
		return aonData;
	}
	
	public Domain getDomain() {
		return getAonData().getDomain();
	}
	
	public User getUser() {
		return getAonData().getUser();
	}
	
	private void init() {
		
	item.getProductCategories(getDomain(), getUser(), new AsyncCallback<LinkedList<ProductCategory>>() {
		
		@Override
		public void onSuccess(LinkedList<ProductCategory> result) {
			categoryListBox.addItem("-", "0");
			result.stream().forEach(c -> {				
				categoryListBox.addItem(c.getName(), c.getId().toString());
			});
		}
		
		@Override public void onFailure(Throwable caught) {}
	});
	    DateTimeFormat dateFormat = DateTimeFormat.getMediumDateFormat();

		startDate.setStyleName("aon-inputText");
		startDate.setFormat(new DateBox.DefaultFormat(dateFormat));
	    startDate.getDatePicker().setYearArrowsVisible(true);
	    
	    endDate.setStyleName("aon-inputText");
	    endDate.setFormat(new DateBox.DefaultFormat(dateFormat));
	    endDate.getDatePicker().setYearArrowsVisible(true);

		titleLabel.setText(AON.MSG.consumptionTemplates());
		map = new HashMap<String, Boolean>();
		hwMap = new HashMap<String, String>();

		hotelBox = new ListBox();
		hotelBox.setStyleName("aon-box-width-template");
		hotelBox.setVisibleItemCount(10);
		hotelBoxPanel.add(hotelBox);
		
		warehouseBox = new ListBox();
		warehouseBox.setStyleName("aon-box-width-template");
		warehouseBox.setVisibleItemCount(10);
		warehouseBoxPanel.add(warehouseBox);
		
		selectedBox = new ListBox();
		selectedBox.setStyleName("aon-box-width-template");
		selectedBox.setVisibleItemCount(10);
		selectedBoxPanel.add(selectedBox);
		excelButton.setEnabled(true);
		
		twoLastCheckBox.setValue(false);
		twoLastCheckBox.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				startDate.setValue(null);
				startDate.setEnabled(!twoLastCheckBox.getValue());
				endDate.setValue(null);
				endDate.setEnabled(!twoLastCheckBox.getValue());
				withoutInvCheckBox.setValue(false);
			}
		});
		
		withoutInvCheckBox.setValue(false);
		withoutInvCheckBox.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				startDate.setValue(null);
				startDate.setEnabled(!withoutInvCheckBox.getValue());
				endDate.setValue(null);
				endDate.setEnabled(!withoutInvCheckBox.getValue());
				twoLastCheckBox.setValue(false);
			}
		});

		item.getWorkplacesToConsumption(getDomain(), getUser(), new AsyncCallback<LinkedList<Hotel>>() {
			
			@Override
			public void onSuccess(LinkedList<Hotel> result) {
				hotels = result;
				for(Hotel h : result){
					hotelBox.addItem(h.getName(), h.getId().toString());
					map.put(h.getId().toString(), false);
				}
				
				hotelBoxClickHandler();
				warehouseBoxDoubleClickHandler();
				selectedBoxDoubleClickHandler();
				
				warehouseCheckBoxChangeHandler();
				detailCheckBoxChangeHandler();				
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}

	//------------------------------ UI Handlers
	
	@UiHandler("cleanButton")
	void cleanAction(ClickEvent event) {
		clean();
	}
	
	/*
	@UiHandler("pdfButton")
	void pdfAction(ClickEvent event) {
		download(PDF);
	}
	*/
	
	@UiHandler("excelButton")
	void excelAction(ClickEvent event) {
		download(EXCEL);
	}
	
	Integer indexAux;
	private void hotelBoxClickHandler(){
		hotelBox.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Integer index = 0;
				while(index < hotels.size() && !hotelBox.getSelectedValue().equals(hotels.get(index).getId().toString())){
					index++;
				}
				if(index < hotels.size()){
					Hotel hotel = hotels.get(index);
					if(hotel.getWarehouses() == null || hotel.getWarehouses().isEmpty()){
						indexAux = index;
						item.getWarehousesToConsumption(getDomain(), getUser(), hotel.getId(), new AsyncCallback<LinkedList<Warehouse>>() {
							Integer index = indexAux;
							@Override
							public void onSuccess(LinkedList<Warehouse> result) {
								Hotel hotel = hotels.get(index);
								hotel.setWarehouses(result);
								warehouseCheckBox.setValue(map.get(hotel.getId().toString()));
								while(warehouseBox.getItemCount()>0){
									warehouseBox.removeItem(0);
								}	
								for (Warehouse w : hotel.getWarehouses()) {
									warehouseBox.addItem(w.getName(), w.getId().toString());
									hwMap.remove(w.getId().toString());
									hwMap.put(w.getId().toString(), hotelBox.getSelectedValue());
								}
							}
						
							@Override
							public void onFailure(Throwable caught) {}
						});
					}
					else{
						warehouseCheckBox.setValue(map.get(hotel.getId().toString()));
						while(warehouseBox.getItemCount()>0){
							warehouseBox.removeItem(0);
						}	
						for (Warehouse w : hotel.getWarehouses()) {
							warehouseBox.addItem(w.getName(), w.getId().toString());
							hwMap.remove(w.getId().toString());
							hwMap.put(w.getId().toString(), hotelBox.getSelectedValue());
						}
					}
				}
			}
		});
	}
	
	private void warehouseBoxDoubleClickHandler(){
		warehouseBox.addDoubleClickHandler(new DoubleClickHandler() {
			
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				activeDownloadButtons();
				String label = warehouseBox.getSelectedItemText();
				String value = warehouseBox.getSelectedValue();
				Boolean hasElement = false;
				for(Integer index = 0; index < selectedBox.getItemCount(); index++){
					if(selectedBox.getValue(index).equals(value))
						hasElement = true;
				}
				if(!hasElement) selectedBox.addItem(label, value);	
			}
		});
	}
	
	private void selectedBoxDoubleClickHandler(){
		selectedBox.addDoubleClickHandler(new DoubleClickHandler() {
			
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				if(selectedBox.getItemCount() == 1){
					deactiveDownloadButtons();
				}
				map.remove(hwMap.get(selectedBox.getSelectedValue()));
				warehouseCheckBox.setValue(false);
				map.put(hwMap.get(selectedBox.getSelectedValue()), false);
				
				Integer index = selectedBox.getSelectedIndex();
				selectedBox.removeItem(index);

			}
		});
	}

	private void warehouseCheckBoxChangeHandler(){
		warehouseCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				map.remove(hotelBox.getSelectedValue());
				map.put(hotelBox.getSelectedValue(), event.getValue());
				for(Integer i = 0; i < warehouseBox.getItemCount(); i++)
					for(Integer j = 0; j < selectedBox.getItemCount(); j++)
						if(selectedBox.getValue(j).equals(warehouseBox.getValue(i)))
							selectedBox.removeItem(j);	
				
				if(event.getValue())
					for(Integer i = 0; i < warehouseBox.getItemCount(); i++){
						selectedBox.addItem(warehouseBox.getItemText(i),warehouseBox.getValue(i));
						activeDownloadButtons();
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

	//------------------------------ Actions
	ProgressBarDialog pbd;
	public void download(String type){
		LinkedList<Warehouse> warehouses = new LinkedList<Warehouse>();
		for (Integer index= 0; index < selectedBox.getItemCount(); index++) {
			Warehouse warehouse = new Warehouse();
			warehouse.setId(Integer.parseInt(selectedBox.getValue(index)));
			warehouse.setName(selectedBox.getItemText(index));
			warehouses.add(warehouse);
		}
		
		Integer size = selectedBox.getItemCount();
		
		Double time = 20.0;
		if(type.equals(PDF)) time = time + 5.0;
		if(detail) time = time + 5.0;
		pbd = new ProgressBarDialog(size.doubleValue(), time, "Generando Excel...") {
			
		};

		pbd.addStyleName("gwt-PopupPanel-template");
		pbd.setGlassEnabled(true);
		pbd.show();
		
		Integer category = "0".equals(categoryListBox.getSelectedValue()) ? null : Integer.parseInt(categoryListBox.getSelectedValue());
	
		if(!twoLastCheckBox.getValue() && !withoutInvCheckBox.getValue()){
			item.generateConsumptionExcel(getDomain(), getUser(), warehouses, type, errorCheckBox.getValue(), detail,  selectedBox.getItemCount(),
					startDate.getValue(), endDate.getValue(), packagedCheckBox.getValue(), category, difCheckBox.getValue(), new AsyncCallback<String>() {
	
				@Override
				public void onSuccess(String result) {
					final String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_aggregate_consumption/"
								+ "?tmpkey="+result
								+ "&username="+ getUser().getLogin()
								+ "&dname=" + getDomain().getName()
								+ "&did=" + getDomain().getId();
				
					pbd.completed();
					pbd.hide();
				
					Window.open( fileDownloadURL, "_blank",null);
				}
			
				@Override
				public void onFailure(Throwable caught) {}
			});
		} else {
			item.generateConsumptionExcel(getDomain(), getUser(), warehouses, type, errorCheckBox.getValue(), detail,  selectedBox.getItemCount(), packagedCheckBox.getValue(), withoutInvCheckBox.getValue(), category, difCheckBox.getValue(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String result) {
					final String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_aggregate_consumption/"
								+ "?tmpkey="+result
								+ "&username="+ getUser().getLogin()
								+ "&dname=" + getDomain().getName()
								+ "&did=" + getDomain().getId();
				
					pbd.completed();
					pbd.hide();
				
					Window.open( fileDownloadURL, "_blank",null);
				}
			
				@Override
				public void onFailure(Throwable caught) {}
			});
		}
	}
	
	private void clean(){
		deactiveDownloadButtons();
		warehouseCheckBox.setValue(false);
		while(selectedBox.getItemCount() > 0)
			selectedBox.removeItem(0);	
	}
	
	private void activeDownloadButtons() {
		excelButton.setEnabled(true);
		// pdfButton.setEnabled(true);
	}
	
	private void deactiveDownloadButtons() {
		excelButton.setEnabled(false);
		// pdfButton.setEnabled(false);
	}

}
