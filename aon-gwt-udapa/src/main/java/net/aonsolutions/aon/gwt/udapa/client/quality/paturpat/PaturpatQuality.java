package net.aonsolutions.aon.gwt.udapa.client.quality.paturpat;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.common.JsDataResponse;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaboration;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaborationDetail;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.FilterPanel;
import com.esferalia.aon.gwt.common.client.widget.Toolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.paper.PaperButtonElement;
import com.vaadin.polymer.paper.PaperFabElement;
import com.vaadin.polymer.paper.PaperInputElement;
import com.vaadin.polymer.paper.PaperItemElement;
import com.vaadin.polymer.paper.PaperRadioButtonElement;
import com.vaadin.polymer.paper.PaperToggleButtonElement;

import net.aonsolutions.aon.gwt.udapa.client.IUdapa;
import net.aonsolutions.aon.gwt.udapa.client.IUdapaAsync;
import net.aonsolutions.aon.gwt.udapa.client.Utils;
import net.aonsolutions.polymer.aon.AonComboBoxElement;
import net.aonsolutions.polymer.aon.widget.AonComboBox;
import net.aonsolutions.polymer.aon.widget.event.SelectedItemChangedEvent;
import net.aonsolutions.polymer.aon.widget.event.SelectedItemChangedEventHandler;

public class PaturpatQuality extends AonTemplate2{

	final IUdapaAsync impl = GWT.create(IUdapa.class);
	private API API;
	private AonData aonData;
	private FilterPanel filterPanel;
	
	public API getAPI() {
		return API;
	}
	
	public AonData getAonData() {
		return aonData;
	}
	
	public FilterPanel getFilterPanel() {
		return filterPanel;
	}
	
	public void setFilterPanel(FilterPanel filterPanel) {
		this.filterPanel = filterPanel;
	}
	
	public HashMap<String, LinkedList<String>> getFilterMap() {
		return getFilterPanel().getFilterMap();
	}
	
	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap) {
		getFilterPanel().setFilterMap(filterMap);
	}
	
	public PaturpatQuality(AonData aonData) {
		this.aonData = aonData;
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
			aonData.getDomain().getName(), aonData.getDomain().getId(),
			aonData.getUser().getLogin());
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
		toolbar();
		westContent();
		content();
	}
	
	private void toolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		
		Toolbar toolbar = new Toolbar("Ficha de calidad");
		toolbar.addButton("Volver", AON.AON_CSS.aonIconCancel(), false).addClickHandler(backClickHandler());
		toolbar.addButton(AON.MSG.newAction(), AON.AON_CSS.aonIconReset()).addClickHandler(resetClickHandler());
		
		setToolbar(toolbar);
	}
	
	private void westContent() {

	}
	
	private void content() {
		DockLayoutPanel dlp = new DockLayoutPanel(Unit.PX);
		SimpleLayoutPanel north = new SimpleLayoutPanel();
		north.addStyleName("aon-margin-left10 aon-margin-right10");
		north.setWidget(filterPanel());
		dlp.addNorth(north, 85);
		SimpleLayoutPanel center = new SimpleLayoutPanel();
		center.addStyleName("aon-margin-left10 aon-margin-right10");
		getAPI().getCommon().getDataResponsePaturpatQuality(getFilterMap(), new AsyncCallback<JSON<JsDataResponse>>() {
			
			@Override
			public void onSuccess(JSON<JsDataResponse> result) {
				center.setWidget(gridPanel(result));
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		dlp.add(center);
		setContent(dlp);
	}
	
	public FilterPanel filterPanel() {
		FilterPanel fp = new FilterPanel() {
			
			@Override
			protected void refresh() {
				Window.alert("REFRESH");
			}
		};
		fp.getElement().getStyle().setLeft(10, Unit.PX);
		fp.getElement().getStyle().setRight(10, Unit.PX);
		
		fp.addDateFilter("Desde", "from");
		fp.addDateFilter("Hasta", "to");
		setFilterPanel(fp);
		return getFilterPanel();
	}
	
	public PaturpatGridPanel gridPanel(JSON<JsDataResponse> result) {
		return new PaturpatGridPanel(this, result.getData().toLinkedList());
	}
	
	public void sheetContent(JsDataResponse js, HashMap<String, LinkedList<String>> map) {
		setFilterMap(map);
		Toolbar toolbar = (Toolbar) getToolbar().getWidget();
		
		toolbar.getButtonPanel().getWidget(0).setVisible(true);// toolbar.setBackVisible(true);

		setContent(new PaturpatQualitySheet(this, js));
	}
	
	private void resetQuality() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName(AON.AON_CSS.aonWidthAll());
		AonComboBox incomeBox = new AonComboBox();
		incomeBox.setLabel("Elaboracion");
		incomeBox.setItemLabelPath("series_number");
		incomeBox.setItemValuePath("series_number");
		
		// TODO
		HashMap<String,LinkedList<String>> map = new HashMap<>();
		getAPI().getWarehouse().getElaborationList(map, new AsyncCallback<JSON<JsElaboration>>() {
			
			@Override
			public void onSuccess(JSON<JsElaboration> result) {
				incomeBox.setItems(result.getData());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
		panel.add(incomeBox);
		
		AonComboBox detailBox = new AonComboBox();
		detailBox.setLabel("Detalle");
		detailBox.setItemLabelPath("item.serial_number");
		detailBox.setItemValuePath("item.serial_number");
		panel.add(detailBox);
		
		incomeBox.addSelectedItemChangedHandler(new SelectedItemChangedEventHandler() {
			
			@Override
			public void onSelectedItemChanged(SelectedItemChangedEvent event) {
				JsOrder order = (JsOrder) incomeBox.getSelectedItem();
		
				getAPI().getWarehouse().getElaborationDetail(order.getId(), new AsyncCallback<JSON<JsElaborationDetail>>() {
					
					@Override
					public void onSuccess(JSON<JsElaborationDetail> result) {
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
				JsElaborationDetail orderDetail = detailBox.getSelectedItem().cast();
				JsElaboration order = incomeBox.getSelectedItem().cast();
				JSONObject dataResponse = new JSONObject();
				dataResponse.put("code", new JSONString(orderDetail.getItem().getSerialNumber()));	
				Date date = Utils.parse("yyyy/MM/dd", order.getDate());
				String dateTime = Utils.formatDateTime(date);
				dataResponse.put("date", new JSONString(dateTime));
				dataResponse.put("source", new JSONNumber(DataResponseSource.PATURPAT_QUALITY.value()));
				dataResponse.put("source_id", new JSONString(orderDetail.getId() + ""));
				getAPI().getCommon().insertDataResponse2(JsonUtils.stringify(dataResponse.getJavaScriptObject()), new AsyncCallback<JsDataResponse>() {
					
					@Override
					public void onSuccess(JsDataResponse result) {
						
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

	/***** BUTTON CLICK HANDLER *****/
	
	private ClickHandler backClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				startApplication();
			}
		};
	}
	
	private ClickHandler resetClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				resetQuality();
			}
		};
	}
	

}
