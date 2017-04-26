package net.aonsolutions.aon.gwt.udapa.client.quality;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.common.JsDataResponse;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.paper.PaperButtonElement;
import com.vaadin.polymer.paper.PaperFabElement;
import com.vaadin.polymer.paper.PaperInputElement;
import com.vaadin.polymer.paper.PaperItemElement;
import com.vaadin.polymer.paper.PaperRadioButtonElement;
import com.vaadin.polymer.paper.PaperToggleButtonElement;
import com.vaadin.polymer.vaadin.widget.VaadinDatePicker;
import com.vaadin.polymer.vaadin.widget.VaadinDatePickerLight;

import net.aonsolutions.aon.gwt.udapa.client.IUdapa;
import net.aonsolutions.aon.gwt.udapa.client.IUdapaAsync;
import net.aonsolutions.aon.gwt.udapa.client.Utils;
import net.aonsolutions.polymer.aon.AonComboBoxElement;
import net.aonsolutions.polymer.aon.widget.AonComboBox;
import net.aonsolutions.polymer.aon.widget.event.SelectedItemChangedEvent;
import net.aonsolutions.polymer.aon.widget.event.SelectedItemChangedEventHandler;

public class UdapaQuality extends AonTemplate2{

	final IUdapaAsync impl = GWT.create(IUdapa.class);
	private API API;
	private UdapaQuality me = this;
	HashMap<String, LinkedList<String>> filterMap;
	private AonData aonData;
	
	public API getAPI() {
		return API;
	}
	
	public AonData getAonData() {
		return aonData;
	}
	
	public HashMap<String, LinkedList<String>> getFilterMap() {
		return filterMap;
	}
	
	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap) {
		this.filterMap = filterMap;
	}
	
	public UdapaQuality(AonData aonData) {
		this.aonData = aonData;
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getUser().getLogin());
	}
	
	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList(
			IronIconsElement.SRC,
			"iron-icons/image-icons.html",
			AonComboBoxElement.SRC,
			PaperButtonElement.SRC,
			PaperRadioButtonElement.SRC,
			PaperItemElement.SRC,
			PaperFabElement.SRC,
			PaperInputElement.SRC,
			PaperToggleButtonElement.SRC
		), o -> {
			startApplication();
			return null;
		});
		
		Polymer.whenReady(o -> {
			super.onModuleLoad();
			startApplication();
			return null;
		});
	}
	
	private void startApplication() {
		initializeFilterMap();
		toolbar();
		westContent();
		principalContent();
	}
	
	private void toolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		Toolbar toolbar = new Toolbar() {
			
			@Override
			protected void reset() {
				resetQuality();
			}
			
			@Override
			protected void remove() {}
			
			@Override
			protected void print() {
				printQuality();
			}
			
			@Override
			protected void next() {}
			
			@Override
			protected void email() {}
			
			@Override
			protected void back() {
				startApplication();
			}
			
			@Override
			protected void ant() {}
		};
		toolbar.setAllVisible(false);
		toolbar.setResetVisible(true);
		setToolbar(toolbar);
	}
	
	private void westContent() {

	}
	
	private void principalContent() {
		setContent(new QualityPrincipal(me, getFilterMap()));
	}
	
	
	public void sheetContent(JsDataResponse js, HashMap<String, LinkedList<String>> map) {
		this.filterMap = map;
		Toolbar toolbar = (Toolbar) getToolbar().getWidget();
		toolbar.setBackVisible(true);
		toolbar.setPrintVisible(true);
		setContent(new QualitySheet(this, js));
	}

	public HashMap<String, LinkedList<String>> initializeFilterMap() {
		filterMap = new HashMap<>();
		LinkedList<String> list = new LinkedList<>();
		list.add("quality");
		filterMap.put("type", list);
		return filterMap;
	}
	
	private void printQuality() {
		QualitySheet sheet = (QualitySheet) getContent().getWidget();
		getAPI().getWarehouse().downloadUdapaQuality(sheet.getDataResponse().getId());
	}
	
	private void resetQuality() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName(AON.AON_CSS.aonWidthAll());
		AonComboBox incomeBox = new AonComboBox();
		incomeBox.setLabel("Albaran");
		incomeBox.setItemLabelPath("reference_code");
		incomeBox.setItemValuePath("reference_code");
		
		// TODO
		HashMap<String,LinkedList<String>> map = new HashMap<>();
		API.getWarehouse().getOrders("income", map ,new AsyncCallback<JSON<JsOrder>>() {
			
			@Override
			public void onSuccess(JSON<JsOrder> result) {
				incomeBox.setItems(result.getData());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		panel.add(incomeBox);
		
		AonComboBox detailBox = new AonComboBox();
		detailBox.setLabel("Detalle");
		detailBox.setItemLabelPath("description");
		detailBox.setItemValuePath("description");
		panel.add(detailBox);
	
		VaadinDatePicker dateBox = new VaadinDatePicker();
		dateBox.setLabel("Fecha");
		panel.add(dateBox);
		
		incomeBox.addSelectedItemChangedHandler(new SelectedItemChangedEventHandler() {
			
			@Override
			public void onSelectedItemChanged(SelectedItemChangedEvent event) {
				JsOrder order = (JsOrder) incomeBox.getSelectedItem();
				
				//TODO
				LinkedList<String>list = new LinkedList<>();
				list.add(order.getId() + "");
				map.put("income", list);
				getAPI().getWarehouse().getDetails("income", map, new AsyncCallback<JSON<JsOrderDetail>>() {
					
					@Override
					public void onSuccess(JSON<JsOrderDetail> result) {
						detailBox.setItems(result.getData());
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		});
		
    	AonDialog dialog = new AonDialog("Nueva Ficha de Calidad", panel) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {
				JsOrderDetail orderDetail = (JsOrderDetail) detailBox.getSelectedItem();
				JsOrder order = (JsOrder) incomeBox.getSelectedItem();

				JSONObject dataResponse = new JSONObject();
				dataResponse.put("number", new JSONString(order.getReferenceCode()));	

				Date date = Utils.parse("yyyy-MM-dd", dateBox.getValue());
				String dateTime = Utils.formatDateTime(date);
				dataResponse.put("issue_date", new JSONString(dateTime));
				
				getAPI().getCommon().insertDataResponse(JsonUtils.stringify(dataResponse.getJavaScriptObject()), new AsyncCallback<JsDataResponse>() {
					
					@Override
					public void onSuccess(JsDataResponse result) {
						JSONObject dataResponseDetail = new JSONObject();
						dataResponseDetail.put("data_response", new JSONString(result.getId() + ""));
						dataResponseDetail.put("type", new JSONString("quality"));
						dataResponseDetail.put("source", new JSONString("income_detail@" + orderDetail.getId()));						
						getAPI().getCommon().insertDataResponseDetail(JsonUtils.stringify(dataResponseDetail.getJavaScriptObject()));
						hide();
						sheetContent(result, getFilterMap());
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		};
		dialog.addAutoHidePartner(incomeBox.getElementById("overlay"));
		dialog.addAutoHidePartner(detailBox.getElementById("overlay"));
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
	
	
	private String getOrderType(JsCarrierPacking carrierPacking){
		String order = "";
		String type = carrierPacking.getType().getId() + "";
		String waybill = CarrierPackingType.WAYBILL.ordinal() + "";
		String status = carrierPacking.getStatus().getId() + "";
		String pending = CarrierPackingStatus.PENDING.ordinal() + "";
					
		if(type.equals(waybill)){
			order = "delivery";
		} else if(status.equals(pending)){
			order = "purchase";
		} else {
			order = "income";
		}
		return order;
	}

}
