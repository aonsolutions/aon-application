package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing.nuevo;

import java.util.List;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSearchBox;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

import net.aonsolutions.aon.gwt.warehouse.client.IWarehouse;
import net.aonsolutions.aon.gwt.warehouse.client.IWarehouseAsync;
import net.aonsolutions.aon.gwt.warehouse.shared.CarrierPackingFilter;

public class CarrierPackingEntryPoint extends AonTemplate2{
	
	final static AonResources AON_RESOURCES = GWT.create(AonResources.class);

	private static final CarrierPackingServiceAsync CARRIER_PACKING_SERVICE;
	static {
		CarrierPackingServiceAsync raw = GWT.create(CarrierPackingService.class);
		CARRIER_PACKING_SERVICE = new CarrierPackingServiceAsyncDecorator(raw); 
	}

	final IWarehouseAsync impl = GWT.create(IWarehouse.class);
	private API API;
	public CarrierPackingFilter params = new CarrierPackingFilter();
	
	private CarrierPackingEntryPoint me = this;
	
	public CarrierPackingEntryPoint() {
		this.API = new API(GWT.getModuleBaseURL(), "",
				getOccam().getDomainName(), getOccam().getDomain(),
				getOccam().getUser());
	}
	
	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
		load();
	}
	
	private void load() {
		loadListToolbar();
		loadContent();
	}
	
	private void loadListToolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 50);
		AonToolbar toolbar = new AonToolbar("Packing List");
		
		AonToolbarButton newButton = new AonToolbarButton("Nuevo", AON.CSS.aonIconAdd());
		newButton.addClickHandler(event -> createCarrierPacking());
		newButton.setVisible(true);
		toolbar.add(newButton);
		
		AonToolbarSearchBox searchBox = new AonToolbarSearchBox() {
			
			@Override
			public void onValueChange(String value) {

			}
		};
		toolbar.showSearchPanel(searchBox);
		setToolbar(toolbar);
	}
	
	private void loadContentToolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 50);
		AonToolbar toolbar = new AonToolbar("Packing List");
		
		AonToolbarButton backButton = new AonToolbarButton("Atras", AON.CSS.aonIconBack());
		backButton.addClickHandler(event -> load());
		backButton.setVisible(true);
		toolbar.add(backButton);
		
		AonToolbarButton newButton = new AonToolbarButton("Nuevo", AON.CSS.aonIconAdd());
		newButton.addClickHandler(event -> createCarrierPacking());
		newButton.setVisible(true);
		toolbar.add(newButton);
		
		AonToolbarButton deleteButton = new AonToolbarButton("Borrar", AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(event -> deleteCarrierPacking());
		deleteButton.setVisible(true);
		toolbar.add(deleteButton);
		
		AonToolbarButton printButton = new AonToolbarButton("Imprimir", AON.CSS.aonIconPdf());
		printButton.addClickHandler(event -> printCarrierPacking());
		printButton.setVisible(true);
		toolbar.add(printButton);
		
		AonToolbarButton sendButton = new AonToolbarButton("Enviar", AON.CSS.aonIconPdf());
		sendButton.addClickHandler(event -> sendCarrierPacking());
		sendButton.setVisible(true);
		toolbar.add(sendButton);
		
		AonToolbarSearchBox searchBox = new AonToolbarSearchBox() {
			
			@Override
			public void onValueChange(String value) {

			}
		};
		toolbar.showSearchPanel(searchBox);
		setToolbar(toolbar);
	}

	public void loadContent() {
		CarrierPackingFilter params = new CarrierPackingFilter()
				.setDomain(getOccam().getDomain())
				.setPage(1)
				.setPerPage(40);
		CARRIER_PACKING_SERVICE.getCarrierPackingList(getOccam(), params, new AsyncCallback<List<CarrierPacking>>() {
			public void onSuccess(List<CarrierPacking> result) {
				setContent(new GridPanel(me, result));
			};
			
			@Override
			public void onFailure(Throwable arg0) {
				
			}
		});
		
	}	
	
	private void createCarrierPacking() {
		loadContentToolbar();
		setContent(new Label("CREATE CARRIER PACKING"));
	}
	
	private void deleteCarrierPacking() {
		load();
	}
	
	private void printCarrierPacking() {
	
	}
	
	private void sendCarrierPacking() {
		
	}

	public void selectCarrierPacking(CarrierPacking carrierPacking) {

	}
	
	public API getAPI() {
		return API;
	}
	
	public void setAPI(API aPI) {
		API = aPI;
	}

	public CarrierPackingFilter getParams() {
		return params;
	}
	
	public void setParams(CarrierPackingFilter params) {
		this.params = params;
	}
}
