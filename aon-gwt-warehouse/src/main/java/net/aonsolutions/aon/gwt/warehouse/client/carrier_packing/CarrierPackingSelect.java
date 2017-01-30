package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CarrierPackingSelect extends Composite{
	
	interface Binder extends UiBinder<Widget, CarrierPackingSelect> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField VerticalPanel westPanel;
	@UiField VerticalPanel centerPanel;
	
	private CarrierPacking parent;
	private API API;
	private JsCarrierPacking jsCarrierPacking;
	
	public CarrierPackingSelect(CarrierPacking carrierPacking, JsCarrierPacking js) {
		initWidget(binder.createAndBindUi(this));
		this.API = carrierPacking.API;
		this.parent = carrierPacking;
		this.jsCarrierPacking = js;
		load();
	}
	
	private void load() {
		ListBox l = new ListBox();
		l.getElement().getStyle().setBackgroundColor("#f6f5e3");
		l.setWidth("300px");
		l.setVisibleItemCount(20);
		
		Label label = new Label("Pedidos sin seleccionar");
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		westPanel.add(label);
		westPanel.add(l);
		
		HashMap<String, LinkedList<String>> map = new HashMap<>();
		LinkedList<String> carrierList = new LinkedList<>();
		carrierList.add(jsCarrierPacking.getCarrier().getId() + "");
		map.put("carrier", carrierList);
		LinkedList<String> ncarrierList = new LinkedList<>();
		ncarrierList.add("");
		map.put("not_carrier_packing", ncarrierList);
			
		if(jsCarrierPacking.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName())){
			API.getWarehouse().getPurchases(map,new AsyncCallback<JSON<JsOrder>>() {
				
				@Override
				public void onSuccess(JSON<JsOrder> result) {
					result.getData().stream().forEach(js -> 
						l.addItem(js.getIssueDate() + "-" + js.getSeries() + "/" + js.getNumber() 
							+ "-" + js.getRegistry().getName(),js.getId()+""));
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
		} else {
			API.getWarehouse().getDeliveries(map,new AsyncCallback<JSON<JsOrder>>() {
				
				@Override
				public void onSuccess(JSON<JsOrder> result) {
					result.getData().stream().forEach(js -> 
						l.addItem(js.getIssueDate() + "-" + js.getSeries() + "/" + js.getNumber() 
							+ "-" + js.getRegistry().getName(),js.getId()+""));
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
		}
				
		ListBox l2 = new ListBox();
		l2.setVisibleItemCount(20);
		l2.getElement().getStyle().setBackgroundColor("#f6f5e3");
		l2.setWidth("300px");
		
		Label label2 = new Label("Pedidos seleccionados");
		label2.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		centerPanel.add(label2);
		centerPanel.add(l2);
		
		HashMap<String, LinkedList<String>> map2 = new HashMap<>();
		LinkedList<String> list = new LinkedList<>();
		list.add(jsCarrierPacking.getId() + "");
		map2.put("carrier_packing", list);
		if(jsCarrierPacking.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName())){
			API.getWarehouse().getPurchases(map2,new AsyncCallback<JSON<JsOrder>>() {
			
				@Override
				public void onSuccess(JSON<JsOrder> result) {
					result.getData().stream().forEach(js -> 
						l2.addItem(js.getIssueDate() + "-" + js.getSeries() + "/" + js.getNumber() 
							+ "-" + js.getRegistry().getName(),js.getId()+""));
				}
			
				@Override
				public void onFailure(Throwable caught) {}
			});
		} else {
			API.getWarehouse().getDeliveries(map2,new AsyncCallback<JSON<JsOrder>>() {
				
				@Override
				public void onSuccess(JSON<JsOrder> result) {
					result.getData().stream().forEach(js -> 
						l2.addItem(js.getIssueDate() + "-" + js.getSeries() + "/" + js.getNumber() 
							+ "-" + js.getRegistry().getName(),js.getId()+""));
				}
			
				@Override
				public void onFailure(Throwable caught) {}
			});
		}
		l.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String value = l.getSelectedValue();
				if(jsCarrierPacking.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName())){
					API.getWarehouse().getPurchase(Integer.parseInt(value),new AsyncCallback<JSON<JsOrder>>() {
					
						@Override
						public void onSuccess(JSON<JsOrder> result) {
							API.getWarehouse().getPurchaseDetails(result.getOneData().getId(), new AsyncCallback<JSON<JsOrderDetail>>() {
							
								@Override
								public void onSuccess(JSON<JsOrderDetail> details) {
									parent.southContent(result.getOneData(), details.getData());
								}
								
								@Override public void onFailure(Throwable caught) {}
							});
						}
					
						@Override
						public void onFailure(Throwable caught) {}
					});
				} else {
					API.getWarehouse().getDelivery(Integer.parseInt(value),new AsyncCallback<JSON<JsOrder>>() {
						
						@Override
						public void onSuccess(JSON<JsOrder> result) {
							API.getWarehouse().getDeliveryDetails(result.getOneData().getId(), new AsyncCallback<JSON<JsOrderDetail>>() {
							
								@Override
								public void onSuccess(JSON<JsOrderDetail> details) {
									parent.southContent(result.getOneData(), details.getData());
								}
								
								@Override public void onFailure(Throwable caught) {}
							});
						}
					
						@Override
						public void onFailure(Throwable caught) {}
					});
				}
			}
		});
		
		l.addDoubleClickHandler(new DoubleClickHandler() {
			
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				String label = l.getSelectedItemText();
				String value = l.getSelectedValue();
				
				l.removeItem(l.getSelectedIndex());
				l2.addItem(label, value);
				
				String requestData = "{\"carrier_packing\":\""+ jsCarrierPacking.getId() +"\"}";
				if(jsCarrierPacking.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName())){
					API.getWarehouse().updatePurchase(Integer.parseInt(value), requestData, new AsyncCallback<JsOrder>() {
						@Override public void onFailure(Throwable caught) {}
						@Override public void onSuccess(JsOrder result) {}
					});
				} else {
					API.getWarehouse().updateDelivery(Integer.parseInt(value), requestData, new AsyncCallback<JsOrder>() {
						@Override public void onFailure(Throwable caught) {}
						@Override public void onSuccess(JsOrder result) {}
					});
				}
			}
		});
		
		l2.addDoubleClickHandler(new DoubleClickHandler() {
			
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				String label = l2.getSelectedItemText();
				String value = l2.getSelectedValue();
				
				l2.removeItem(l2.getSelectedIndex());
				l.addItem(label, value);
				
				String requestData = "{\"carrier_packing\":\"\"}";
				if(jsCarrierPacking.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName())){
					API.getWarehouse().updatePurchase(Integer.parseInt(value), requestData, new AsyncCallback<JsOrder>() {
						@Override public void onFailure(Throwable caught) {}
						@Override public void onSuccess(JsOrder result) {}
					});			
				} else {
					API.getWarehouse().updateDelivery(Integer.parseInt(value), requestData, new AsyncCallback<JsOrder>() {
						@Override public void onFailure(Throwable caught) {}
						@Override public void onSuccess(JsOrder result) {}
					});		
				}
				
			}
		});
	
	}

	
	public JsCarrierPacking getJsCarrierPacking() {
		return jsCarrierPacking;
	}
}
