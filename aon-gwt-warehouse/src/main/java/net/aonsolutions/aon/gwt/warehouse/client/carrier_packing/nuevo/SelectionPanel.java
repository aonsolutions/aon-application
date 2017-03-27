package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing.nuevo;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperItem;

import net.aonsolutions.aon.gwt.warehouse.client.Utils;


public class SelectionPanel extends ResizeComposite implements RequiresResize {
	
	static final String BACKGROUND_COLOR = "#DDDDDD";

	private HashMap<String, LinkedList<String>> selectedFilter;
	private Boolean selectedMore = true;
	private VerticalPanel selectedVerticalPanel;
	private Integer selectedOrder;
	private HashMap<String, LinkedList<String>> selectableFilter;
	private Boolean selectableMore = true;
	private VerticalPanel selectableVerticalPanel;
	private Integer selectableOrder;	
	

	
	private CarrierPackingDetail parent;
	
	private API getAPI(){
		return parent.getAPI();
	}

	private JsCarrierPacking getCarrierPacking(){
		return parent.getJsCarrierPacking();
	}
	
	public SelectionPanel(CarrierPackingDetail parent) {	
		this.parent = parent;
		initSelectedFilter();
		initSelectableFilter();
		SplitLayoutPanel rootPanel = new SplitLayoutPanel(4);

		//  -------------------------- SELECTABLE PANEL ------------------------------
		
		SimpleLayoutPanel selectable = new SimpleLayoutPanel();
		selectable.setStyleName(AON.AON_CSS.aonFlexContainer());
		selectable.addStyleName(AON.AON_CSS.aonPadding2Top());
		selectable.setStyleName(AON.AON_CSS.aonInvoicePanel());
		selectable.getElement().getStyle().setBackgroundColor(BACKGROUND_COLOR);
		selectable.getElement().getStyle().setMarginLeft(0, Unit.PX);
		selectablePanel();
		selectable.setWidget(selectableVerticalPanel);
		rootPanel.addEast(selectable, Window.getClientWidth()/2);

		//  -------------------------- SELECTED PANEL ------------------------------

		SimpleLayoutPanel selected = new SimpleLayoutPanel();
		selected.setStyleName(AON.AON_CSS.aonFlexContainer());
		selected.addStyleName(AON.AON_CSS.aonPadding2Top());
		selected.setStyleName(AON.AON_CSS.aonInvoicePanel());
		selected.getElement().getStyle().setBackgroundColor(BACKGROUND_COLOR);
		selected.getElement().getStyle().setMarginLeft(0, Unit.PX);
		selectedPanel();
		selected.setWidget(selectedVerticalPanel);
		rootPanel.add(selected);
		
		initWidget(rootPanel);
	}

	// -------------------- SELECTED PANEL

	private void initSelectedFilter(){
		selectedFilter = new HashMap<>();
		
		LinkedList<String> list = new LinkedList<>();
		list.add(getCarrierPacking().getId() + "");
		selectedFilter.put("carrier_packing", list);
		
		Date date = new Date();
		date.setYear(date.getYear()-1);
		list = new LinkedList<>();
		list.add(Long.toString(date.getTime()));
		selectedFilter.put("issue_date", list);
		
		// -------------------- per page
		list = new LinkedList<>();
		list.add("30");
		selectedFilter.put("per_page", list);
		
		// -------------------- page
		list = new LinkedList<>();
		list.add("1");
		selectedFilter.put("page", list);
	}
	
	private void selectedPanel(){
		selectedVerticalPanel = new VerticalPanel();
		selectedVerticalPanel.add(selectedFilterPanel());
		selectedVerticalPanel.add(selectedItems());
	}
	
	private HorizontalPanel selectedFilterPanel() {
		HorizontalPanel p = new HorizontalPanel();
		Label label = new Label("Fecha ");
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		label.getElement().getStyle().setPadding(3, Unit.PX);
		p.add(label);
		
		Date date = new Date();
		date.setYear(date.getYear()-1);
		DateBoxEx datebox = new DateBoxEx();
		datebox.setStyleName(AON.AON_CSS.aonTextBox());
		datebox.setValue(date);
		datebox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(Long.toString(datebox.getValue().getTime()));
				selectedFilter.put("issue_date", list);
			
				selectedVerticalPanel.remove(1);
				selectedVerticalPanel.add(selectedItems());
			}
		});
		p.add(datebox);
		return p;
	}
	
	private ScrollPanel selectedItems() {
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addScrollHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
				Integer scrollTop = scrollPanel.getElement().getScrollTop();
				Integer offsetHeight = scrollPanel.getElement().getOffsetHeight();
				Integer physicalSize = scrollPanel.getElement().getScrollHeight();
				Integer maxScrollPosition = physicalSize - offsetHeight;
				if(scrollTop > maxScrollPosition && selectedMore){
					Integer page = Integer.parseInt(selectedFilter.get("page").get(0));
					LinkedList<String> list = new LinkedList<>();
					list.add(Integer.toString(page +1));
					selectedFilter.put("page", list);	
					getAPI().getWarehouse().getOrders(getOrderType(), selectedFilter, new AsyncCallback<JSON<JsOrder>>() {
						
						@Override
						public void onSuccess(JSON<JsOrder> result) {
							addSelected(result);
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
				}
			}
		});
		scrollPanel.add(new VerticalPanel());
		getAPI().getWarehouse().getOrders(getOrderType(), selectedFilter, new AsyncCallback<JSON<JsOrder>>() {
			
			@Override
			public void onSuccess(JSON<JsOrder> result) {
				addSelected(scrollPanel, result);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
		return scrollPanel;
	}
	
	private void addSelected(JSON<JsOrder> orders){
		ScrollPanel scroll = (ScrollPanel) selectedVerticalPanel.getWidget(1);
		addSelectable(scroll, orders);
	}
	
	private void addSelected(ScrollPanel scroll, JSON<JsOrder> orders){
		VerticalPanel vp = (VerticalPanel) scroll.getWidget();
		orders.getData().stream().forEach(order -> buildOrderItem(vp, order, true));
	}

	// -------------------- SELECTABLE PANEL
	
	private void initSelectableFilter(){
		selectableFilter = new HashMap<>();
		
		LinkedList<String> list = new LinkedList<>();
		list.add(getCarrierPacking().getCarrier().getId() + "");
		selectableFilter.put("carrier", list);
		
		list = new LinkedList<>();
		list.add(Long.toString(new Date().getTime()));
		selectableFilter.put("issue_date", list);	
		
		list = new LinkedList<>();
		list.add("");
		selectableFilter.put("not_carrier_packing", list);
		
		// -------------------- per page
		list = new LinkedList<>();
		list.add("30");
		selectableFilter.put("per_page", list);
		
		// -------------------- page
		list = new LinkedList<>();
		list.add("1");
		selectableFilter.put("page", list);
	}
	
	private void selectablePanel() {
		selectableVerticalPanel = new VerticalPanel();
		selectableVerticalPanel.add(selectableFilterPanel());
		selectableVerticalPanel.add(selectableItems());
	}

	private HorizontalPanel selectableFilterPanel() {
		HorizontalPanel p = new HorizontalPanel();
		p.getElement().getStyle().setPaddingTop(5, Unit.PX);
		Label label = new Label("Fecha ");
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		label.getElement().getStyle().setPadding(3, Unit.PX);
		p.add(label);
		DateBoxEx datebox = new DateBoxEx();
		datebox.setStyleName(AON.AON_CSS.aonTextBox());
		datebox.setValue(new Date());
		datebox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(Long.toString(datebox.getValue().getTime()));
				selectableFilter.put("issue_date", list);
			
				selectableVerticalPanel.remove(1);
				selectableVerticalPanel.add(selectableItems());
			}
		});
		p.add(datebox);
		return p;
	}
	
	private ScrollPanel selectableItems() {
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addScrollHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
				Integer scrollTop = scrollPanel.getElement().getScrollTop();
				Integer offsetHeight = scrollPanel.getElement().getOffsetHeight();
				Integer physicalSize = scrollPanel.getElement().getScrollHeight();
				Integer maxScrollPosition = physicalSize - offsetHeight;
				if(scrollTop > maxScrollPosition && selectableMore){
					Integer page = Integer.parseInt(selectableFilter.get("page").get(0));
					LinkedList<String> list = new LinkedList<>();
					list.add(Integer.toString(page +1));
					selectableFilter.put("page", list);	
					getAPI().getWarehouse().getOrders(getOrderType(), selectableFilter, new AsyncCallback<JSON<JsOrder>>() {
						
						@Override
						public void onSuccess(JSON<JsOrder> result) {
							addSelectable(result);
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
				}
			}
		});
		scrollPanel.add(new VerticalPanel());
		getAPI().getWarehouse().getOrders(getOrderType(), selectableFilter, new AsyncCallback<JSON<JsOrder>>() {
			
			@Override
			public void onSuccess(JSON<JsOrder> result) {
				addSelectable(scrollPanel, result);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
		return scrollPanel;
	}
	
	private void addSelectable(JSON<JsOrder> orders){
		ScrollPanel scroll = (ScrollPanel) selectableVerticalPanel.getWidget(1);
		addSelectable(scroll, orders);
	}
	
	private void addSelectable(ScrollPanel scroll, JSON<JsOrder> orders){
		VerticalPanel vp = (VerticalPanel) scroll.getWidget();
		orders.getData().stream().forEach(order -> buildOrderItem(vp, order, false));
	}
	
	
	
	private void buildOrderItem(VerticalPanel vp, JsOrder order, Boolean selected){
		PaperItem pi = new PaperItem();
		HorizontalPanel hp = new HorizontalPanel();

		PaperIconButton pib = new PaperIconButton();
		pib.setIcon(selected ? "remove" : "add");
	    pib.setStyle("height:24px;font-size:12px;padding:0px;font-weight: bold;");
	    pib.setNoink(true);
	    
		pib.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO 
				if(selected){
					removeAllOrder(order); 
				} else {
					addAllOrder(order);
				}
			}
		});
		hp.add(pib);
		
		IronIcon ironIcon = new IronIcon();
	   	ironIcon.setIcon("arrow-drop-down");
	    pi.add(ironIcon);
	    String title = (order.getIssueDate() != null 
	    		? Utils.formatDate(Utils.parseDateTime(order.getIssueDate())) + " - "
	    		: "") + order.getSeriesNumber() + " - " + order.getRegistry().getName(); // + WORKPLACE!
	    pi.add(new Label(title));
	    pi.setStyle("min-height:24px;font-size:12px;padding:0px;font-weight: bold;");
	    
	    SimplePanel sp = new SimplePanel();
		sp.setVisible(false);
		
	    if((selected && selectedOrder != null  && selectedOrder == order.getId())
	    	|| (!selected && selectableOrder != null && selectableOrder == order.getId())){
	    	loadDetails(sp, selected, order);
	    }
	
		pi.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				loadDetails(sp, selected, order);
			}
		});
	    hp.add(pi);
	    vp.add(hp);
		vp.add(sp);
	}
	
	private void loadDetails(SimplePanel sp, Boolean selected, JsOrder order){
		if(!sp.isVisible()){
			HashMap<String, LinkedList<String>> map = new HashMap<>();
			LinkedList<String> list = new LinkedList<>();
			list.add(selected ? (getCarrierPacking().getId() + "") : "null");
			map.put("carrier_packing", list);
			list = new LinkedList<>();
			list.add("" + order.getId());
			map.put("parent_id", list);
			getAPI().getWarehouse().getDetails(getOrderType(), map, new AsyncCallback<JSON<JsOrderDetail>>() {
				
				@Override
				public void onSuccess(JSON<JsOrderDetail> result) {
					sp.setWidget(buildDetail(result, selected));
					sp.setVisible(!sp.isVisible());
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		} else sp.setVisible(!sp.isVisible());
	}
	
	private VerticalPanel buildDetail(JSON<JsOrderDetail> details, Boolean selected) {
		VerticalPanel vp = new VerticalPanel();
		details.getData().stream().forEach(detail ->{
			HorizontalPanel panel = new HorizontalPanel();
			panel.getElement().getStyle().setPaddingLeft(40, Unit.PX);

		    Label label = new Label(detail.getDescription());
		    label.getElement().getStyle().setPaddingTop(2, Unit.PX);
		   
		    
		    DoubleBox db = new DoubleBox();
		    db.getElement().getStyle().setMarginLeft(10, Unit.PX);
		    db.getElement().getStyle().setHeight(13, Unit.PX);
		    db.getElement().getStyle().setWidth(30, Unit.PX);
		    db.getElement().getStyle().setPadding(0, Unit.PX);
		    db.setEnabled(isShipment() && !selected);
		    db.setValue(detail.getQuantity());
		    
		    if(isShipment()){
				PaperIconButton pib = new PaperIconButton();
				pib.setIcon(selected ? "remove" : "add");
				pib.setStyle("height:16px;font-size:12px;padding:0px;font-weight: bold;");
		    	pib.setNoink(false);
		    	panel.add(pib);
			    pib.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						addOneDetail(detail, db.getValue());
					}
				});
			}
		    panel.add(new Label(detail.getDescription()));
		    panel.add(db);
		    vp.add(panel);
		});
		return vp;
	}
	
	
	private void removeAllOrder(JsOrder order){
		String requestData = "{\"carrier_packing\":\"\"}";
		if(isShipment()){
			requestData = "{\"carrier_packing\":\""+ getCarrierPacking().getId() +"\","
					+ "\"purchase\":\""+ order.getId() +"\"," 
					+ "\"action\":\"delete\""+ "}";

			getAPI().getWarehouse().addAllCarrierPacking("purchase", requestData, new AsyncCallback<JSON<JsOrderDetail>>() {
				@Override public void onSuccess(JSON<JsOrderDetail> result) {
					refresh();
				}
				@Override public void onFailure(Throwable caught) {}
			});
		} else {
			getAPI().getWarehouse().updateDelivery(order.getId(), requestData, new AsyncCallback<JsOrder>() {
				@Override public void onFailure(Throwable caught) {}
				@Override public void onSuccess(JsOrder result) {
					refresh();
				}
			});		
		}
	}
	
	private void addAllOrder(JsOrder order){
		String requestData = "{\"carrier_packing\":\""+ getCarrierPacking().getId() +"\","
				+ "\"purchase\":\""+ order.getId() +"\","
				+ "\"action\":\"add\"" + "}";
		if(isShipment()){
			getAPI().getWarehouse().addAllCarrierPacking(getOrderType(), requestData, new AsyncCallback<JSON<JsOrderDetail>>() {
				
				@Override
				public void onSuccess(JSON<JsOrderDetail> result) {
					refresh();
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		} else {
			getAPI().getWarehouse().updateDelivery(order.getId(), requestData, new AsyncCallback<JsOrder>() {
				
				@Override
				public void onSuccess(JsOrder result) {
					refresh();
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		}
	}
	
	private void addOneDetail(JsOrderDetail detail, Double quantity){
		selectedOrder = detail.getPurchase();
		selectableOrder = detail.getPurchase();
		String requestData = "{\"id\":\""+ detail.getId() +"\","
				+"\"action\":\""+ (detail.getCarrierPacking() != null ? "delete" : "add") + "\","
				+"\"carrier_packing\":\""+ getCarrierPacking().getId() + "\","
				+"\"quantity\":\""+ quantity + "\""
				+ "}";
		
		getAPI().getWarehouse().addCarrierPacking(getOrderType(), requestData, new AsyncCallback<JSON<JsOrderDetail>>() {
			
			@Override
			public void onSuccess(JSON<JsOrderDetail> result) {
				refresh();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
		
	private void refresh(){
		selectableVerticalPanel.remove(1);
		selectableVerticalPanel.add(selectableItems());
		selectedVerticalPanel.remove(1);
		selectedVerticalPanel.add(selectedItems());
	}
	
	private Boolean isShipment(){
		return getCarrierPacking().getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName());
	}
	
	private String getOrderType(){
		if(isShipment()){
			return "purchase";
		} else {
			return "delivery";
		}
	}
}
