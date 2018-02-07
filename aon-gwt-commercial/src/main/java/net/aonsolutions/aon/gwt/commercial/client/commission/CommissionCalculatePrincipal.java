package net.aonsolutions.aon.gwt.commercial.client.commission;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.commercial.JsCommission;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class CommissionCalculatePrincipal extends Composite{
	
	interface Binder extends UiBinder<Widget, CommissionCalculatePrincipal> {}
	public static final AonGwtIssuesCSS I_CSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField DockLayoutPanel contentDockLayoutPanel;
	@UiField SplitLayoutPanel contentSplitLayoutPanel;
	@UiField SimpleLayoutPanel northContent;
	@UiField SimpleLayoutPanel content;
	@UiField SimpleLayoutPanel southContent;
	
	private CommissionCalculate parent;
	private HashMap<String, LinkedList<String>> filterMap;
	private CommissionCalculatePrincipal me;
	
	
	public Boolean isOffer() {
		return parent.isOffer();
	}
	
	public Boolean isInvoice() {
		return parent.isInvoice();
	}
	
	public API getAPI() {
		return parent.getAPI();
	}

	public CommissionCalculatePrincipal(CommissionCalculate parent, HashMap<String, LinkedList<String>> filterMap) {
		initWidget(binder.createAndBindUi(this));
		this.parent = parent;
		this.me = this;
		if(filterMap == null) {
			this.filterMap = new HashMap<>();
		} else this.filterMap = filterMap;
		filterContent();
		gridContent();
	}
	
	public CommissionCalculatePrincipal(CommissionCalculate carrierPacking) {
		initWidget(binder.createAndBindUi(this));
		this.parent = carrierPacking;
		this.me = this;
		this.filterMap = new HashMap<>();	
		filterContent();
		gridContent();
	}
	
	public void filterContent(){
		northContent.setWidget(new CommissionCalculateFilter(this));
	}
	
	public void gridContent(){
		LinkedList<String> list = new LinkedList<>();
		list.add("1");
		getFilterMap().put("page", list);
		LinkedList<String> list2 = new LinkedList<>();
		list2.add("40");
		getFilterMap().put("per_page", list2);
		
		if(isOffer()) {
			getAPI().getCommission().getOfferCalculatedCommission(getFilterMap(),new AsyncCallback<JSON<JsCommission>>() {
			
				@Override
				public void onSuccess(JSON<JsCommission> result) {
					CommissionCalculateGrid grid = new CommissionCalculateGrid(parent, result.getData().toLinkedList());
					content.setWidget(grid);
				}
			
				@Override public void onFailure(Throwable caught) {}
			});
		} else if(isInvoice()){
			getAPI().getCommission().getInvoiceCalculatedCommission(getFilterMap(),new AsyncCallback<JSON<JsCommission>>() {
				
				@Override
				public void onSuccess(JSON<JsCommission> result) {
					CommissionCalculateGrid grid = new CommissionCalculateGrid(parent, result.getData().toLinkedList());
					content.setWidget(grid);
				}
			
				@Override public void onFailure(Throwable caught) {}
			});
		}
	}
	
	public void southContent(){

	}
	
	public void southContentSize(Double value) {
		contentSplitLayoutPanel.setWidgetSize(southContent, value);	
	}
	
	public HashMap<String, LinkedList<String>> getFilterMap() {
		return filterMap != null ? filterMap : new HashMap<>();
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
