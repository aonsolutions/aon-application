package net.aonsolutions.aon.gwt.udapa.client.quality;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.common.JsDataResponse;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.Toolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
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
			"iron-icons/editor-icons.html",
			AonComboBoxElement.SRC,
			PaperButtonElement.SRC,
			PaperRadioButtonElement.SRC,
			PaperItemElement.SRC,
			PaperFabElement.SRC,
			PaperInputElement.SRC,
			PaperToggleButtonElement.SRC,
			"vaadin-mock-xhr"
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
	
	private void backApplication() {
		toolbar();
		westContent();
		principalContent();
	}
	
	private void toolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		Toolbar toolbar = new Toolbar("Ficha de calidad") {
			
			@Override
			protected void reset() {
				resetQuality();
			}
			
			@Override
			protected void remove() {
				removeQuality();
			}
			
			@Override
			protected void print() {
				printQuality();
			}
			
			@Override
			protected void next() {
				Integer page = Integer.parseInt(filterMap.get("page").get(0));
				page = page + 1;
				LinkedList<String> list = new LinkedList<>();
				list.add(page.toString());
				filterMap.put("page", list);
				list = new LinkedList<>();
				list.add("1");
				filterMap.put("per_page", list);
				getAPI().getCommon().getDataResponse(getFilterMap(), new AsyncCallback<JSON<JsDataResponse>>() {
					
					@Override
					public void onSuccess(JSON<JsDataResponse> result) {
						if(result.getData().length() == 0){
							Integer page = Integer.parseInt(filterMap.get("page").get(0));
							page = page < 2 ? 1 : page - 1;
							LinkedList<String> list = new LinkedList<>();
							list.add(page.toString());
							filterMap.put("page", list);
						}
						setContent(new QualitySheet(me, result.getData().get(0)));						
				}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
			
			@Override
			protected void email() {}
			
			@Override
			protected void back() {
				backApplication();
			}
			
			@Override
			protected void ant() {
				Integer page = Integer.parseInt(filterMap.get("page").get(0));
				
				if(page > 1 ){
					page = page - 1;
					LinkedList<String> list = new LinkedList<>();
					list.add(page.toString());
					filterMap.put("page", list);
					list = new LinkedList<>();
					list.add("1");
					filterMap.put("per_page", list);
					getAPI().getCommon().getDataResponse(getFilterMap(), new AsyncCallback<JSON<JsDataResponse>>() {
						
						@Override
						public void onSuccess(JSON<JsDataResponse> result) {
							setContent(new QualitySheet(me, result.getData().get(0)));
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
				}
			}

			@Override
			protected void excelDownload() {
				printQualityList("excel");
			}

			@Override
			protected void pdfDownload() {
				printQualityList("pdf");
			}
		};
		toolbar.setAllVisible(false);
		toolbar.setResetVisible(true);
		toolbar.setExcelVisible(true);
		toolbar.setPdfVisible(true);
		setToolbar(toolbar);
	}
	
	private void westContent() {

	}
	
	private void principalContent() {
		setContent(new QualityPrincipal(me));
	}
	
	
	public void sheetContent(JsDataResponse js, HashMap<String, LinkedList<String>> map) {
		this.filterMap = map;
		Toolbar toolbar = (Toolbar) getToolbar().getWidget();
		toolbar.setBackVisible(true);
		toolbar.setPrintVisible(true);
		toolbar.setRemoveVisible(true);
		toolbar.setAntVisible(true);
		toolbar.setNextVisible(true);
		toolbar.setExcelVisible(false);
		toolbar.setPdfVisible(false);
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
	
	private void printQualityList(String type) {
		getAPI().getWarehouse().downloadUdapaQualityList(getFilterMap(), type);
	}
	
	private void removeQuality() {
		Label label = new Label("Est\u00e1 seguro que quiere borrar el Carrier Packing ");
		
		AonDialog dialog = new AonDialog("Borrar Carrier Packing", label) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {	
				QualitySheet sheet = (QualitySheet) getContent().getWidget();
				String idStr = sheet.getDataResponse().getId() + "";
				Integer id = Integer.parseInt(idStr);
				impl.deleteQuality(getAonData().getDomain().getName(), getAonData().getDomain().getId(),
					id, new AsyncCallback<Void>() {
					
					@Override public void onSuccess(Void result) {
						hide();
						startApplication();
					}
					
					@Override public void onFailure(Throwable caught) {}
				});							
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
		
		
		
	}
	
	private void resetQuality() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName(AON.AON_CSS.aonWidthAll());
		AonComboBox incomeBox = new AonComboBox();
		incomeBox.setLabel("Albar\u00e1n");
		incomeBox.setItemLabelPath("reference_code");
		incomeBox.setItemValuePath("reference_code");
		
		// TODO
		HashMap<String,LinkedList<String>> map = new HashMap<>();
		API.getWarehouse().getOrders("incomeQ", map ,new AsyncCallback<JSON<JsOrder>>() {
			
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
				map.put("quality", list);
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
				dataResponse.put("source", new JSONString("income_detail@" + orderDetail.getId()));
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
	
	
	

}
