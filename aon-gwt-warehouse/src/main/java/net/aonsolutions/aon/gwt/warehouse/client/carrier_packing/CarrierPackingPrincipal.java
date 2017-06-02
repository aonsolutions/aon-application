package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesResources;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class CarrierPackingPrincipal extends Composite{
	
	interface Binder extends UiBinder<Widget, CarrierPackingPrincipal> {}
	public static final AonGwtIssuesCSS I_CSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField DockLayoutPanel contentDockLayoutPanel;
	@UiField SplitLayoutPanel contentSplitLayoutPanel;
	@UiField SimpleLayoutPanel northContent;
	@UiField SimpleLayoutPanel content;
	@UiField SimpleLayoutPanel southContent;
	
	private CarrierPacking parent;
	private HashMap<String, LinkedList<String>> filterMap;
	private CarrierPackingPrincipal me;
	
	public API getAPI() {
		return parent.getAPI();
	}

	public CarrierPackingPrincipal(CarrierPacking carrierPacking, HashMap<String, LinkedList<String>> filterMap) {
		initWidget(binder.createAndBindUi(this));
		this.parent = carrierPacking;
		this.me = this;
		this.filterMap = filterMap;
		
		filterContent();
		gridContent();
		southContent();
		southContentSize(30.0);
	}
	
	public CarrierPackingPrincipal(CarrierPacking carrierPacking) {
		initWidget(binder.createAndBindUi(this));
		this.parent = carrierPacking;
		this.me = this;
		this.filterMap = new HashMap<>();
		LinkedList<String> status = new LinkedList<>();
		status.add(CarrierPackingStatus.PENDING.ordinal() + "");
		status.add(CarrierPackingStatus.ON_ROUTE.ordinal() + "");
		filterMap.put("status", status);
		
		filterContent();
		gridContent();
		southContent();
		southContentSize(30.0);
	}
	
	public void filterContent(){
		northContent.setWidget(new FilterPanel(this));
	}
	
	public void gridContent(){
		LinkedList<String> list = new LinkedList<>();
		list.add("1");
		getFilterMap().put("page", list);
		list = new LinkedList<>();
		list.add("40");
		getFilterMap().put("per_page", list);
		parent.getAPI().getWarehouse().getCarrierPacking(getFilterMap(), new AsyncCallback<JSON<JsCarrierPacking>>() {
			
			@Override
			public void onSuccess(JSON<JsCarrierPacking> result) {
				content.setWidget(new GridPanel(me, result.getData().toLinkedList()));
				//content.setWidget(new VaadinGridPanel(result));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
	}
	
	public void southContent(){
		southContent.setWidget(new PrincipalFootPanel(this));		
	}
	
	public void southContentSize(Double value) {
		contentSplitLayoutPanel.setWidgetSize(southContent, value);	
	}
	
	public void selectCarrierPacking(JsCarrierPacking js){
		parent.carrierPackingContent(js, filterMap);
	}

	public HashMap<String, LinkedList<String>> getFilterMap() {
		return filterMap;
	}

	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap) {
		this.filterMap = filterMap;
	}
	
	public void initializeFilterMap(){
		HashMap<String, LinkedList<String>> map = new HashMap<>();
		LinkedList<String> from = new LinkedList<>();
		from.add(Long.toString(new Date().getTime()));
		map.put("from", from );
    	setFilterMap(map);
	}
	
}
