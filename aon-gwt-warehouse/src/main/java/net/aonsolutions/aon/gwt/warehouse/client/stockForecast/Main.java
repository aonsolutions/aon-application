package net.aonsolutions.aon.gwt.warehouse.client.stockForecast;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsStockForecast;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Main extends Composite {

	interface Binder extends UiBinder<Widget, Main> {}
	
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	DockLayoutPanel contentDockLayoutPanel;
	@UiField
	SplitLayoutPanel contentSplitLayoutPanel;
	@UiField
	SimpleLayoutPanel northContent;
	@UiField
	SimpleLayoutPanel content;
	@UiField
	SimpleLayoutPanel southContent;
	
	private StockForecast parent;
	private HashMap<String, LinkedList<String>> filterMap;
	
	public API getAPI() {
		return parent.getAPI();
	}
	

	public Main(StockForecast parent, HashMap<String, LinkedList<String>> filterMap) {
		initWidget(binder.createAndBindUi(this));
		this.parent = parent;
		if(filterMap==null){
			filterMap = new HashMap<>();
			LinkedList<String> status = new LinkedList<>();
//			status.add(ElaborationStatus.PENDING.ordinal() + "");
		}
		this.filterMap = filterMap;
		
		load();
	}
	
	public Main(StockForecast parent) {
		initWidget(binder.createAndBindUi(this));
		this.parent = parent;
		filterMap = new HashMap<>();
		LinkedList<String> status = new LinkedList<>();
//		status.add(ElaborationStatus.PENDING.ordinal() + "");
		filterMap.put("status", status);
//		parent.setFilterMap(filterMap);
		
		load();
	}

	private void load() {
		loadWestContent();
		loadNorthContent();
		loadContent();
		loadSouthContent();
	}


	private void loadWestContent() {

	}

	private void loadNorthContent() {
		contentDockLayoutPanel.setWidgetSize(northContent, 85);
		northContent.setWidget(new FilterPanel(this));
	}

	public void loadContent() {
		LinkedList<String> list = new LinkedList<>();
		list.add("1");
		getFilterMap().put("page", list);
		list = new LinkedList<>();
		list.add("40");
		getFilterMap().put("per_page", list);
		
		getAPI().getWarehouse().getStockForecast(filterMap, new AsyncCallback<JSON<JsStockForecast>>() {

			@Override
			public void onSuccess(JSON<JsStockForecast> result) {
				content.setWidget(new GridPanel(parent, result.getData().toLinkedList()));
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}

	public void loadSouthContent() {
//		FooterPanel fp = new FooterPanel(this);
//		southContent.setWidget(fp);
//		fp.hideFooterPanel();		
	}
	
	
	public void changeSouthContentSize(Double value) {
		contentSplitLayoutPanel.setWidgetSize(southContent, value);	
	}

	public HashMap<String, LinkedList<String>> getFilterMap() {
		return filterMap;
	}

	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap) {
		this.filterMap = filterMap;
	}

	public void refreshSelect() {
		
	}
		
}
